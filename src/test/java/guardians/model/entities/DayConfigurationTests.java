package guardians.model.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import guardians.model.repositories.DoctorRepository;
import guardians.model.validation.annotations.ValidShiftPreferences;
import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@Slf4j
public class DayConfigurationTests {
	@Autowired
	private DoctorRepository doctorRepository;

	private EntityTester<DayConfiguration> entityTester;

	public DayConfigurationTests() {
		this.entityTester = new EntityTester<>(DayConfiguration.class);
	}
	
	/**
	 * This method will create a valid {@link DayConfiguration} for the given day.
	 * Note neither a month or a year are configured, nor shift preferences
	 * 
	 * @param day The day the created {@link DayConfiguration} will represent
	 * @return the created {@link DayConfiguration}
	 */
	public static DayConfiguration createValidDayConfiguration(Integer day) {
		return new DayConfiguration(day, true, 2, 0);
	}

	/**
	 * This interface is used to allow createDayConfiguration to modify the
	 * {@link DayConfiguration} easily
	 * 
	 * @author miggoncan
	 */
	private interface DayConfigurationModifier {
		public void configure(DayConfiguration dayConfiguration, List<Doctor> doctors);
	}

	/**
	 * Create a valid {@link DayConfiguration} and change it using
	 * modifier.configure
	 * 
	 * The given {@link Doctor} list to modifier.configure will always have, at
	 * least, 4 different {@link Doctor}s
	 * 
	 * @param modifier The configure method of this object will be used to change
	 *                 the created {@link DayConfiguration}
	 * @return The newly created and configured {@link DayConfiguration}
	 */
	private DayConfiguration createDayConfiguration(DayConfigurationModifier modifier) {
		log.debug("Creating a new DayConfiguration");

		List<Doctor> doctors = doctorRepository.saveAll(DoctorTest.createValidDoctors());

		LocalDate validDate = DateProvider.getValidDate();
		DayConfiguration dayConfiguration = createValidDayConfiguration(validDate.getDayOfMonth());
		dayConfiguration.setMonth(validDate.getMonthValue());
		dayConfiguration.setYear(validDate.getYear());
		
		modifier.configure(dayConfiguration, doctors);
		log.debug("The created DayConfiguration is: " + dayConfiguration);

		return dayConfiguration;
	}

	/**
	 * Create a new {@link DayConfiguration}, change it using modifier.configure and
	 * assert it is a valid {@link DayConfiguration}
	 * 
	 * The given {@link Doctor} list to modifier.configure will always have, at
	 * least, 4 different {@link Doctor}s
	 * 
	 * @param modifier The configure method of this object will be used to modify
	 *                 the {@link DayConfiguration} being tested
	 */
	private void testValidDayConfiguration(DayConfigurationModifier modifier) {
		log.debug("Testing a valid DayConfiguration");
		DayConfiguration dayConfiguration = this.createDayConfiguration(modifier);
		this.entityTester.assertValidEntity(dayConfiguration);
	}

	/**
	 * Create a valid {@link DayConfiguration}, change it using modifier.configure
	 * and assert it is an invalid {@link DayConfiguration}
	 * 
	 * The given {@link Doctor} list to modifier.configure will always have, at
	 * least, 4 different {@link Doctor}s
	 * 
	 * modifier is expected to change the shift preferences, making them invalid (to
	 * try an violate the {@link ValidShiftPreferences} validation
	 * 
	 * @param modifier The configure method of this object will be used to modify
	 *                 the {@link DayConfiguration} being tested
	 */
	private void testInvalidDayConfiguration(DayConfigurationModifier modifier) {
		log.debug("Testing an invalid DayConfiguration");
		DayConfiguration dayConfiguration = this.createDayConfiguration(modifier);
		this.entityTester.assertEntityViolatedConstraint(dayConfiguration,
				"The shift preferences cannot have clashes between one another");
	}

	@Test
	void testDates() {
		DayConfiguration dayConfiguration = new DayConfiguration();
		dayConfiguration.setIsWorkingDay(true);
		dayConfiguration.setNumShifts(3);
		dayConfiguration.setNumConsultations(2);
		DateTester<DayConfiguration> dateTester = new DateTester<>(DayConfiguration.class);
		dateTester.testEntity(dayConfiguration, (day, month, year) -> {
			dayConfiguration.setDay(day);
			dayConfiguration.setMonth(month);
			dayConfiguration.setYear(year);
		});
	}

	////////////////////////////////////
	//
	// Test for valid values
	//
	////////////////////////////////////

	@Test
	void isWorkingDayCanBeTrue() {
		this.entityTester.assertValidValue("isWorkingDay", true);
	}

	@Test
	void isWorkingDayCanBeFalse() {
		this.entityTester.assertValidValue("isWorkingDay", false);
	}

	@Test
	void numShiftsCanBeZero() {
		this.entityTester.assertValidValue("numShifts", 0);
	}

	@Test
	void numShiftsCanBePositive() {
		this.entityTester.assertValidValue("numShifts", 3);
	}

	@Test
	void numConsultationsCanBeZero() {
		this.entityTester.assertValidValue("numConsultations", 0);
	}

	@Test
	void numConsultationsCanBePositive() {
		this.entityTester.assertValidValue("numConsultations", 2);
	}

	@Test
	void unwantedShiftsCanBeNull() {
		this.entityTester.assertValidValue("unwantedShifts", null);
	}

	@Test
	void unwantedShiftsCanBeEmpty() {
		this.entityTester.assertValidValue("unwantedShifts", new HashSet<>());
	}

	@Test
	void unavailableShiftsCanBeNull() {
		this.entityTester.assertValidValue("unavailableShifts", null);
	}

	@Test
	void unavailableShiftsCanBeEmpty() {
		this.entityTester.assertValidValue("unavailableShifts", new HashSet<>());
	}

	@Test
	void wantedShiftsCanBeNull() {
		this.entityTester.assertValidValue("wantedShifts", null);
	}

	@Test
	void wantedShiftsCanBeEmpty() {
		this.entityTester.assertValidValue("wantedShifts", new HashSet<>());
	}

	@Test
	void mandatoryShiftsCanBeNull() {
		this.entityTester.assertValidValue("mandatoryShifts", null);
	}

	@Test
	void mandatoryShiftsCanBeEmpty() {
		this.entityTester.assertValidValue("mandatoryShifts", new HashSet<>());
	}

	@Test
	void cycleChangesCanBeNull() {
		this.entityTester.assertValidValue("cycleChanges", null);
	}

	@Test
	void cycleChangesCanBeEmpty() {
		this.entityTester.assertValidValue("cycleChanges", new HashSet<>());
	}

	@Test
	void validDayConfiguration() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
		});
	}

	@Test
	void validDayConfigurationWithUnwantedShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors));
		});
	}

	@Test
	void validDayConfigurationWithUnavailableShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors));
		});
	}

	@Test
	void validDayConfigurationWithWantedShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setWantedShifts(new HashSet<>(doctors));
		});
	}

	@Test
	void validDayConfigurationWithMandatoryShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors));
		});
	}

	@Test
	void validDayConfigurationWithUnwantedAndUnavailableShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors.subList(2, 4)));
		});
	}

	@Test
	void validDayConfigurationWithUnwantedAndWantedShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setWantedShifts(new HashSet<>(doctors.subList(2, 4)));
		});
	}

	@Test
	void validDayConfigurationWithUnwantedAndMandatoryShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors.subList(2, 4)));
		});
	}

	@Test
	void validDayConfigurationWithUnavailableAndWantedShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setWantedShifts(new HashSet<>(doctors.subList(2, 4)));
		});
	}

	@Test
	void validDayConfigurationWithUnavailableAndMandatoryShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors.subList(2, 4)));
		});
	}

	@Test
	void validDayConfigurationWithWantedAndMandatoryShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setWantedShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors.subList(2, 4)));
		});
	}

	@Test
	void validDayConfigurationWithUnwantedUnavailableAndWantedShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors.subList(0, 1)));
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors.subList(1, 2)));
			dayConfiguration.setWantedShifts(new HashSet<>(doctors.subList(2, 3)));
		});
	}

	@Test
	void validDayConfigurationWithUnwantedUnavailableAndMandatoryShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors.subList(0, 1)));
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors.subList(1, 2)));
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors.subList(2, 3)));
		});
	}

	@Test
	void validDayConfigurationWithUnwantedWantedandMandatoryShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors.subList(0, 1)));
			dayConfiguration.setWantedShifts(new HashSet<>(doctors.subList(1, 2)));
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors.subList(2, 3)));
		});
	}

	@Test
	void validDayConfigurationWithUnavailabledWantedandMandatoryShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors.subList(0, 1)));
			dayConfiguration.setWantedShifts(new HashSet<>(doctors.subList(1, 2)));
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors.subList(2, 3)));
		});
	}

	@Test
	void validDayConfigurationWithAllShifts() {
		testValidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors.subList(0, 1)));
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors.subList(1, 2)));
			dayConfiguration.setWantedShifts(new HashSet<>(doctors.subList(2, 3)));
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors.subList(3, 4)));
		});
	}

	////////////////////////////////////
	//
	// Test for invalid values
	//
	////////////////////////////////////

	@Test
	void isWorkingDayCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("isWorkingDay");
	}

	@Test
	void numShiftsCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("numShifts");
	}

	@Test
	void numShiftsCannotBeNegative() {
		this.entityTester.assertInvalidValue("numShifts", -1, "must be greater than or equal to 0");
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
	void cannotHaveSameUnwantedAndUnavailableShifts() {
		testInvalidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors.subList(0, 2)));
		});
	}

	@Test
	void cannotHaveSameUnwantedAndWantedShifts() {
		testInvalidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setWantedShifts(new HashSet<>(doctors.subList(0, 2)));
		});
	}

	@Test
	void cannotHaveSameUnwantedAndMandatoryShifts() {
		testInvalidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnwantedShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors.subList(0, 2)));
		});
	}

	@Test
	void cannotHaveSameUnavailableAndWantedShifts() {
		testInvalidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setWantedShifts(new HashSet<>(doctors.subList(0, 2)));
		});
	}

	@Test
	void cannotHaveSameUnavailableAndMandatoryShifts() {
		testInvalidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setUnavailableShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors.subList(0, 2)));
		});
	}

	@Test
	void cannotHaveSameWantedAndMandatoryShifts() {
		testInvalidDayConfiguration((dayConfiguration, doctors) -> {
			dayConfiguration.setWantedShifts(new HashSet<>(doctors.subList(0, 2)));
			dayConfiguration.setMandatoryShifts(new HashSet<>(doctors.subList(0, 2)));
		});
	}
}
