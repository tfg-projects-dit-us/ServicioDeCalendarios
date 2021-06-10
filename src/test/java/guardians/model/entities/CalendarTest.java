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

import guardians.model.repositories.CalendarRepository;
import guardians.model.repositories.DoctorRepository;

@DataJpaTest
public class CalendarTest {
	private final String INVALID_CALENDAR_MESSAGE = "The calendar has to contains all days of the month";
	
	private EntityTester<Calendar> entityTester;
	
	@Autowired
	private CalendarRepository calendarRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	public CalendarTest() {
		this.entityTester = new EntityTester<>(Calendar.class);
	}
	
	public static Calendar createValidCalendar() {
		YearMonth yearMonth = YearMonth.of(2020, 6);
		Calendar calendar = new Calendar(yearMonth.getMonthValue(), yearMonth.getYear());
		
		SortedSet<DayConfiguration> days = new TreeSet<>();
		for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
			DayConfiguration day = DayConfigurationTests.createValidDayConfiguration(i);
			day.setCalendar(calendar);
			days.add(day);
		}
		calendar.setDayConfigurations(days);
		
		return calendar;
	}
	
	@Test
	void createAndSaveValidCalendar() {
		Calendar calendar = createValidCalendar();
		
		this.entityTester.assertValidEntity(calendar);
		
		Calendar savedCalendar = calendarRepository.save(calendar);
		assertEquals(calendar.getMonth(), savedCalendar.getMonth());
		assertEquals(calendar.getYear(), savedCalendar.getYear());
		assertIterableEquals(calendar.getDayConfigurations(), savedCalendar.getDayConfigurations());
	}
	
	@Test
	void createAndSaveValidCalendarWithShiftPreferences() {
		List<Doctor> doctors = doctorRepository.saveAll(DoctorTest.createValidDoctors());
		Calendar calendar = createValidCalendar();
		Set<DayConfiguration> days = calendar.getDayConfigurations();
		
		days.forEach((day) -> {
			day.setUnwantedShifts(new HashSet<>(doctors.subList(0, 2)));
			day.setWantedShifts(new HashSet<>(doctors.subList(2, 4)));
		});
		
		this.entityTester.assertValidEntity(calendar);

		Calendar savedCalendar = calendarRepository.save(calendar);
		assertEquals(calendar.getMonth(), savedCalendar.getMonth());
		assertEquals(calendar.getYear(), savedCalendar.getYear());
		
		List<DayConfiguration> prevDaysAsList = new ArrayList<>(calendar.getDayConfigurations());
		Collections.sort(prevDaysAsList);
		List<DayConfiguration> savedDaysAsList = new ArrayList<>(savedCalendar.getDayConfigurations());
		Collections.sort(savedDaysAsList);
		assertIterableEquals(prevDaysAsList, savedDaysAsList);
	}
	
	@Test
	void calendarWithOneNullDay() {
		Calendar calendar = createValidCalendar();
		
		List<DayConfiguration> days = new ArrayList<>(calendar.getDayConfigurations());
		days.set(3, null);
		assertThrows(NullPointerException.class, () -> {calendar.setDayConfigurations(new TreeSet<>(days));});
	}
	
	@Test
	void calendarWithOneDuplicateDay() {
		Calendar calendar = createValidCalendar();
		
		List<DayConfiguration> days = new ArrayList<>(calendar.getDayConfigurations());
		days.set(3, days.get(20));
		calendar.setDayConfigurations(new TreeSet<>(days));
		
		this.entityTester.assertEntityViolatedConstraint(calendar, INVALID_CALENDAR_MESSAGE);
	}
	
	@Test
	void calendarWithOneLessDay() {
		Calendar calendar = createValidCalendar();
		
		List<DayConfiguration> days = new ArrayList<>(calendar.getDayConfigurations());
		days.remove(5);
		calendar.setDayConfigurations(new TreeSet<>(days));
		
		this.entityTester.assertEntityViolatedConstraint(calendar, INVALID_CALENDAR_MESSAGE);
	}
}
