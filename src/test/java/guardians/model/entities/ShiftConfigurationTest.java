package guardians.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import guardians.model.repositories.AllowedShiftRepository;
import guardians.model.repositories.DoctorRepository;
import guardians.model.repositories.ShiftConfigurationRepository;
import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@Slf4j
public class ShiftConfigurationTest {
	@Autowired
	private ShiftConfigurationRepository shiftConfigurationRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private AllowedShiftRepository allowedShiftRepository;

	private EntityTester<ShiftConfiguration> entityTester;

	/**
	 * Create a {@link ShiftConfiguration} without Shift Preferences (e.g.
	 * unwantedShifts)
	 * 
	 * @param doctor The doctor to whom this shift configuration will apply. It
	 *               should already be persisted
	 * @return The created {@link ShiftConfiguration}
	 */
	public static ShiftConfiguration createValidShiftConfiguration(Doctor doctor) {
		ShiftConfiguration shiftConfiguration = new ShiftConfiguration(2, 3, 0, true, false);
		shiftConfiguration.setDoctor(doctor);
		return shiftConfiguration;
	}

	public ShiftConfigurationTest() {
		this.entityTester = new EntityTester<>(ShiftConfiguration.class);
	}

	/**
	 * This interface is used to allow createShiftConfiguration to modify the
	 * ShiftConfiguration easily
	 * 
	 * @author miggoncan
	 */
	private interface ShiftConfigurationModifier {
		public void configure(ShiftConfiguration shiftConfiguration, List<AllowedShift> allowedShifts);
	}

	/**
	 * Create a {@link ShiftConfiguration} and change it according to op.configure
	 * 
	 * The allowedShifts given to the op.configure method will always have, at
	 * least, 4 elements
	 * 
	 * @param op The configure method of this object will be used to change the
	 *           tested shiftConfiguration
	 * @return The created {@link ShiftConfiguration}. It will not be persisted
	 */
	private ShiftConfiguration createShiftConfiguration(ShiftConfigurationModifier op) {
		log.debug("Creating a new ShiftConfiguraton");
		Doctor myDoctor = doctorRepository.save(DoctorTest.createValidDoctor());
		List<AllowedShift> allowedShifts = allowedShiftRepository.saveAll(AllowedShiftTest.createValidAllowedShifts());

		ShiftConfiguration shiftConfiguration = createValidShiftConfiguration(myDoctor);
		op.configure(shiftConfiguration, allowedShifts);
		log.debug("The created configuration is: " + shiftConfiguration);

		return shiftConfiguration;
	}

	/**
	 * Create a valid {@link ShiftConfiguration}, change it according to
	 * op.configure and test it
	 * 
	 * The allowedShifts given to the op.configure method will always have, at
	 * least, 4 elements
	 * 
	 * The expected changed made by op are expected to create a valid
	 * {@link ShiftConfiguration}
	 * 
	 * @param op The configure method of this object will be used to change the
	 *           tested shiftConfiguration
	 */
	private void testValidShiftConfiguration(ShiftConfigurationModifier op) {
		log.debug("Testing a valid ShiftConfiguration");
		ShiftConfiguration shiftConfiguration = createShiftConfiguration(op);

		this.entityTester.assertValidEntity(shiftConfiguration);

		shiftConfiguration = shiftConfigurationRepository.save(shiftConfiguration);
		assertEquals(shiftConfiguration.getDoctor().getId(), shiftConfiguration.getDoctorId());
	}

	/**
	 * Create an valid ShiftConfiguration, change it according to op.configure and
	 * test it
	 * 
	 * The allowedShifts given to the op.configure method will always have, at
	 * least, 4 elements
	 * 
	 * The expected changed made by op are expected to create ShiftConfiguration
	 * that violates the restrictions from ValidShiftConfiguration
	 * 
	 * @param op The configure method of this object will be used to change the
	 *           tested shiftConfiguration
	 */
	private void testInvalidShiftConfiguration(ShiftConfigurationModifier op) {
		log.debug("Testing an invalid ShiftConfiguration");
		ShiftConfiguration shiftConfiguration = createShiftConfiguration(op);

		this.entityTester.assertEntityViolatedConstraint(shiftConfiguration,
				"The shift preferences cannot have clashes between one another");
	}

	///////////////////////////////////////
	//
	// Tests for valid values
	//
	///////////////////////////////////////

	//
	// Tests for maxShifts, minShifts and doesConsultations
	//

	@Test
	void validMaxShifts() {
		this.entityTester.assertValidValue("maxShifts", 2);
	}

	@Test
	void maxShiftsCanBeZero() {
		this.entityTester.assertValidValue("maxShifts", 0);
	}

	@Test
	void validMinShifts() {
		this.entityTester.assertValidValue("minShifts", 2);
	}

	@Test
	void minShiftsCanBeZero() {
		this.entityTester.assertValidValue("minShifts", 0);
	}

	@Test
	void numConsultationCanBeZero() {
		this.entityTester.assertValidValue("numConsultations", 0);
	}

	@Test
	void numConsultationCanBePositive() {
		this.entityTester.assertValidValue("numConsultations", 2);
	}
	
	@Test
	void doesCycleShiftsCanBeTrue() {
		this.entityTester.assertValidValue("doesCycleShifts", true);
	}
	
	@Test
	void doesCycleShiftsCanBeFalse() {
		this.entityTester.assertValidValue("doesCycleShifts", false);
	}
	
	@Test
	void hasShiftsOnlyWhenCycleShiftsCanBeTrue() {
		this.entityTester.assertValidValue("hasShiftsOnlyWhenCycleShifts", true);
	}
	
	@Test
	void hasShiftsOnlyWhenCycleShiftsCanBeFalse() {
		this.entityTester.assertValidValue("hasShiftsOnlyWhenCycleShifts", false);
	}

	//
	// Tests for unwanted, unavailable, wanted and mandatory shifts
	//

	@Test
	void validUnwantedShifts() {
		this.entityTester.assertValidValue("unwantedShifts", AllowedShiftTest.createValidAllowedShifts());
	}

	@Test
	void unwantedShiftsCanBeNull() {
		this.entityTester.assertValidValue("unwantedShifts", null);
	}

	@Test
	void validUnavailableShifts() {
		this.entityTester.assertValidValue("unavailableShifts", AllowedShiftTest.createValidAllowedShifts());
	}

	@Test
	void unavailableShiftsCanBeNull() {
		this.entityTester.assertValidValue("unavailableShifts", null);
	}

	@Test
	void validWantedShifts() {
		this.entityTester.assertValidValue("wantedShifts", AllowedShiftTest.createValidAllowedShifts());
	}

	@Test
	void wantedShiftsCanBeNull() {
		this.entityTester.assertValidValue("wantedShifts", null);
	}

	@Test
	void validMandatoryShifts() {
		this.entityTester.assertValidValue("mandatoryShifts", AllowedShiftTest.createValidAllowedShifts());
	}

	@Test
	void mandatoryShiftsCanBeNull() {
		this.entityTester.assertValidValue("mandatoryShifts", null);
	}

	//
	// Tests to create several shiftConfigurations and persist them
	//

	@Test
	void createAndSaveValidShiftConfiguration() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnwantedShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnavailableShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithWantedShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithMandatoryShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnwantedAndUnavailableShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts.subList(2, 4)));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnwantedAndWantedShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts.subList(2, 4)));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnwantedAndMandatoryShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts.subList(2, 4)));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnavailableAndWantedShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts.subList(2, 4)));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnavailableAndMandatoryShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts.subList(2, 4)));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithWantedAndMandatoryShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts.subList(2, 4)));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnwantedUnavailableAndWantedShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts.subList(0, 1)));
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts.subList(1, 2)));
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts.subList(2, 3)));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnwantedUnavailableAndMandatoryShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts.subList(0, 1)));
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts.subList(1, 2)));
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts.subList(2, 3)));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnwantedWantedandMandatoryShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts.subList(0, 1)));
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts.subList(1, 2)));
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts.subList(2, 3)));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithUnavailabledWantedandMandatoryShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts.subList(0, 1)));
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts.subList(1, 2)));
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts.subList(2, 3)));
		});
	}

	@Test
	void createAndSaveValidShiftConfigurationWithAllShifts() {
		testValidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts.subList(0, 1)));
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts.subList(1, 2)));
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts.subList(2, 3)));
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts.subList(3, 4)));
		});
	}

	///////////////////////////////////////
	//
	// Tests for invalid values
	//
	///////////////////////////////////////

	@Test
	void doctorIdCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("doctorId");
	}

	@Test
	void minShiftCannotBeLargerThanMaxShifts() {
		Doctor doctor = doctorRepository.save(DoctorTest.createValidDoctor());
		ShiftConfiguration shiftConfiguration = createValidShiftConfiguration(doctor);
		shiftConfiguration.setMinShifts(4);
		shiftConfiguration.setMaxShifts(2);
		this.entityTester.assertEntityViolatedConstraint(shiftConfiguration,
				"The minimum shifts have to be less than the maximum shifts");
	}

	//
	// Tests for maxShifts, minShifts and numConsultations
	//

	@Test
	void maxShiftsCannotBeNegative() {
		this.entityTester.assertInvalidValue("maxShifts", -1, "must be greater than or equal to 0");
	}

	@Test
	void maxShiftsCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("maxShifts");
	}

	@Test
	void minShiftsCannotBeNegative() {
		this.entityTester.assertInvalidValue("minShifts", -1, "must be greater than or equal to 0");
	}

	@Test
	void minShiftsCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("minShifts");
	}

	@Test
	void numConsultationsCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("numConsultations");
	}
	
	@Test
	void numConsultationsCannotBeNegative() {
		this.entityTester.assertInvalidValue("numConsultations", -1, "must be greater than or equal to 0");
	}
	
	@Test
	void doesCycleShiftsCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("doesCycleShifts");
	}
	
	@Test
	void hasShiftsOnlyWhenCycleShiftsCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("hasShiftsOnlyWhenCycleShifts");
	}

	@Test
	void cannotHaveSameUnwantedAndUnavailableShifts() {
		testInvalidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts.subList(0, 2)));
		});
	}

	@Test
	void cannotHaveSameUnwantedAndWantedShifts() {
		testInvalidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts.subList(0, 2)));
		});
	}

	@Test
	void cannotHaveSameUnwantedAndMandatoryShifts() {
		testInvalidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnwantedShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts.subList(0, 2)));
		});
	}

	@Test
	void cannotHaveSameUnavailableAndWantedShifts() {
		testInvalidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts.subList(0, 2)));
		});
	}

	@Test
	void cannotHaveSameUnavailableAndMandatoryShifts() {
		testInvalidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setUnavailableShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts.subList(0, 2)));
		});
	}

	@Test
	void cannotHaveSameWantedAndMandatoryShifts() {
		testInvalidShiftConfiguration((shiftConfiguration, allowedShifts) -> {
			shiftConfiguration.setWantedShifts(new HashSet<>(allowedShifts.subList(0, 2)));
			shiftConfiguration.setMandatoryShifts(new HashSet<>(allowedShifts.subList(0, 2)));
		});
	}
}
