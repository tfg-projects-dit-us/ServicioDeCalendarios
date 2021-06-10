package guardians.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import guardians.model.entities.Schedule.ScheduleStatus;
import guardians.model.repositories.CalendarRepository;
import guardians.model.repositories.DoctorRepository;
import guardians.model.repositories.ScheduleRepository;

@DataJpaTest
public class ScheduleTest {

	private static final String INVALID_SCHEDULE_MESSAGE = "If the schedule is created, it has to contain all days of the month. Otherwise, it cannot have any day";

	private EntityTester<Schedule> entityTester;
	
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private CalendarRepository calendarRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;

	public ScheduleTest() {
		this.entityTester = new EntityTester<>(Schedule.class);
	}

	public static Schedule createValidSchedule(YearMonth yearMonth, ScheduleStatus status, Set<Doctor> doctors) {
		Schedule schedule = new Schedule(status);
		schedule.setMonth(yearMonth.getMonthValue());
		schedule.setYear(yearMonth.getYear());

		if (status != ScheduleStatus.NOT_CREATED) {
			ScheduleDay scheduleDay;
			SortedSet<ScheduleDay> days = new TreeSet<>();
			for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
				scheduleDay = ScheduleDayTest.createValidScheduleDay(day, doctors);
				scheduleDay.setSchedule(schedule);
				days.add(scheduleDay);
			}
			schedule.setDays(days);
		}

		return schedule;
	}
	
	public static Schedule createValidSchedule(YearMonth yearMonth, ScheduleStatus status) {
		return createValidSchedule(yearMonth, status, new HashSet<>(DoctorTest.createValidDoctors()));
	}

	@Test
	void saveScheduleNotCreated() {
		Calendar calendar = calendarRepository.save(CalendarTest.createValidCalendar());
		
		YearMonth yearMonth = YearMonth.of(2020, 6);
		Schedule schedule = createValidSchedule(yearMonth, ScheduleStatus.NOT_CREATED);
		schedule.setCalendar(calendar);
		
		this.entityTester.assertValidEntity(schedule);
		
		Schedule savedSchedule = scheduleRepository.save(schedule);
		assertEquals(schedule.getMonth(), savedSchedule.getMonth());
		assertEquals(schedule.getYear(), savedSchedule.getYear());
		assertIterableEquals(schedule.getDays(), savedSchedule.getDays());
	}

	@Test
	void saveScheduleConfirmed() {
		Calendar calendar = calendarRepository.save(CalendarTest.createValidCalendar());
		Set<Doctor> doctors = new HashSet<>(doctorRepository.saveAll(DoctorTest.createValidDoctors()));
		
		YearMonth yearMonth = YearMonth.of(2020, 6);
		Schedule schedule = createValidSchedule(yearMonth, ScheduleStatus.CONFIRMED, doctors);
		schedule.setCalendar(calendar);
		
		this.entityTester.assertValidEntity(schedule);
		
		Schedule savedSchedule = scheduleRepository.save(schedule);
		assertEquals(schedule.getMonth(), savedSchedule.getMonth());
		assertEquals(schedule.getYear(), savedSchedule.getYear());
		
		List<ScheduleDay> prevDays = new ArrayList<>(schedule.getDays());
		Collections.sort(prevDays);
		List<ScheduleDay> savedDays = new ArrayList<>(savedSchedule.getDays());
		Collections.sort(savedDays);
		assertIterableEquals(prevDays, savedDays);
	}
	
	@Test
	void scheduleNotCreatedAndWithDays() {
		YearMonth yearMonth = YearMonth.of(2020, 6);
		Schedule schedule = createValidSchedule(yearMonth, ScheduleStatus.CONFIRMED);
		schedule.setStatus(ScheduleStatus.NOT_CREATED);
		this.entityTester.assertEntityViolatedConstraint(schedule, INVALID_SCHEDULE_MESSAGE);
	}

	@Test
	void scheduleWithOneNullDay() {
		YearMonth yearMonth = YearMonth.of(2020, 6);
		Schedule schedule = createValidSchedule(yearMonth, ScheduleStatus.CONFIRMED);
		
		List<ScheduleDay> days = new ArrayList<>(schedule.getDays());
		days.set(4, null);
		assertThrows(NullPointerException.class, () -> {schedule.setDays(new TreeSet<>(days));});
	}
	
	@Test
	void scheduleWithOneDuplicatedDay() {
		YearMonth yearMonth = YearMonth.of(2020, 6);
		Schedule schedule = createValidSchedule(yearMonth, ScheduleStatus.CONFIRMED);

		List<ScheduleDay> days = new ArrayList<>(schedule.getDays());
		days.set(4, days.get(20));
		schedule.setDays(new TreeSet<>(days));
		
		this.entityTester.assertEntityViolatedConstraint(schedule, INVALID_SCHEDULE_MESSAGE);
	}
	
	@Test
	void scheduleWithLessDaysThanNeeded() {
		YearMonth yearMonth = YearMonth.of(2020, 6);
		Schedule schedule = createValidSchedule(yearMonth, ScheduleStatus.CONFIRMED);
		
		List<ScheduleDay> days = new ArrayList<>(schedule.getDays());
		days.remove(3);
		schedule.setDays(new TreeSet<>(days));
		
		this.entityTester.assertEntityViolatedConstraint(schedule, INVALID_SCHEDULE_MESSAGE);
	}
}
