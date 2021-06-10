package guardians.model.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class ScheduleDayTest {

	private EntityTester<ScheduleDay> entityTester;

	public ScheduleDayTest() {
		this.entityTester = new EntityTester<>(ScheduleDay.class);
	}

	/**
	 * This method will create a {@link ScheduleDay} with valid fields (a valid
	 * cycle, valid shifts and a valid isWorkingDay).
	 * 
	 * Note that, for it to be completely valid, a month and a year have to be
	 * assigned.
	 * 
	 * @param day [1, 31] The day of the month of this {@link ScheduleDay}. If day
	 *            is even, the day will be a working day. Otherwise, it won't
	 * @return The created {@link ScheduleDay}
	 */
	public static ScheduleDay createValidScheduleDay(int day, Set<Doctor> doctors) {
		boolean isWorkingDay = day % 2 == 0;
		ScheduleDay scheduleDay = new ScheduleDay(day, isWorkingDay);
		scheduleDay.setCycle(doctors);
		if (isWorkingDay) {
			scheduleDay.setShifts(doctors);
		}
		return scheduleDay;
	}

	@Test
	void testDates() {
		ScheduleDay scheduleDay = new ScheduleDay();
		Set<Doctor> doctors = new HashSet<>();
		doctors.add(DoctorTest.createValidDoctor());
		scheduleDay.setIsWorkingDay(true);
		scheduleDay.setCycle(doctors);
		scheduleDay.setShifts(doctors);
		DateTester<ScheduleDay> dateTester = new DateTester<>(ScheduleDay.class);
		dateTester.testEntity(scheduleDay, (day, month, year) -> {
			scheduleDay.setDay(day);
			scheduleDay.setMonth(month);
			scheduleDay.setYear(year);
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
	void consultationsCanBeNull() {
		this.entityTester.assertValidValue("consultations", null);
	}

	@Test
	void consultationsCanBeEmpty() {
		this.entityTester.assertValidValue("consultations", Collections.emptySet());
	}

	@Test
	void validCycle() {
		Set<Doctor> doctors = new HashSet<>(DoctorTest.createValidDoctors());
		this.entityTester.assertValidValue("cycle", doctors);
	}

	@Test
	void validShifts() {
		Set<Doctor> doctors = new HashSet<>(DoctorTest.createValidDoctors());
		this.entityTester.assertValidValue("shifts", doctors);
	}

	@Test
	void validConsultations() {
		Set<Doctor> doctors = new HashSet<>(DoctorTest.createValidDoctors());
		this.entityTester.assertValidValue("consultations", doctors);
	}

	////////////////////////////////////
	//
	// Test for invalid values
	//
	////////////////////////////////////

	@Test
	void dayCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("day");
	}

	@Test
	void monthCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("month");
	}

	@Test
	void yearCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("year");
	}

	@Test
	void isWorkingDayCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("isWorkingDay");
	}

	@Test
	void cycleCannotBeEmpty() {
		this.entityTester.assertAttributeCannotBeEmpty("cycle");
	}
}
