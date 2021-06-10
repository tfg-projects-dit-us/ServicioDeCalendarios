package guardians.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaSystemException;

import guardians.model.repositories.AbsenceRepository;
import guardians.model.repositories.DoctorRepository;

@DataJpaTest
public class AbsenceTest {
	@Autowired
	private AbsenceRepository absenceRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	private EntityTester<Absence> entityTester;

	public AbsenceTest() {
		this.entityTester = new EntityTester<>(Absence.class);
	}

	/**
	 * This method creates one valid Absence instance.
	 * 
	 * Calls to this method will always return a Absence with the same attribute
	 * values
	 * 
	 * @return a new Absence object that has not been persisted
	 */
	public static Absence createValidAbsence() {
		return new Absence(LocalDate.of(2020, 5, 25), LocalDate.of(2020, 6, 30));
	}

	///////////////////////////////////////
	//
	// Tests for valid values
	//
	///////////////////////////////////////

	@Test
	void validStartDate() {
		this.entityTester.assertValidValue("start", new Date(System.currentTimeMillis() + 2 * 24 * 3600 * 1000));
	}

	@Test
	void validEndDate() {
		this.entityTester.assertValidValue("end", new Date(System.currentTimeMillis() + 2 * 24 * 3600 * 1000));
	}

	@Test
	void createAndSaveValidAbsence() {
		Doctor myDoctor = doctorRepository.save(DoctorTest.createValidDoctor());
		Absence absence = createValidAbsence();
		absence.setDoctor(myDoctor);

		this.entityTester.assertValidEntity(absence);

		absence = absenceRepository.save(absence);
		assertEquals(myDoctor.getId(), absence.getDoctorId());
	}

	///////////////////////////////////////
	//
	// Tests for invalid values
	//
	///////////////////////////////////////

	@Test
	void endCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("end");
	}

	@Test
	void startCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("start");
	}

	@Test
	void createAbsenceWithStartAfterEnd() {
		Doctor myDoctor = doctorRepository.save(DoctorTest.createValidDoctor());
		Absence absence = new Absence(LocalDate.of(2020, 7, 30), LocalDate.of(2020, 4, 20));
		absence.setDoctor(myDoctor);
		this.entityTester.assertEntityViolatedConstraint(absence,
				"The start date of the Absence must be before its end date");
	}

	@Test
	void saveAbsenceWithoutDoctor() {
		Absence absence = new Absence();
		Exception ex = assertThrows(JpaSystemException.class, () -> {
			absenceRepository.save(absence);
		});
		assertTrue(ex.getMessage().contains("attempted to assign id from null"));
	}

}
