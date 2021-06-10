package guardians;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import guardians.model.entities.AllowedShift;
import guardians.model.entities.Calendar;
import guardians.model.entities.DayConfiguration;
import guardians.model.entities.Doctor;
import guardians.model.entities.ShiftConfiguration;
import guardians.model.repositories.AllowedShiftRepository;
import guardians.model.repositories.CalendarRepository;
import guardians.model.repositories.DoctorRepository;
import guardians.model.repositories.ShiftConfigurationRepository;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class LoadInitialData {
	@Autowired
	private DoctorRepository doctorRepository;
	@Autowired
	private ShiftConfigurationRepository shiftConfRepository;
	@Autowired
	private CalendarRepository calendarRepository;
	@Autowired
	private AllowedShiftRepository allowedShiftRepository;

	@Bean
	CommandLineRunner preloadInitialData() {
		return args -> {
			log.info("Starting the preload of the initial data");

			LocalDate refDate = LocalDate.of(2020, 5, 1);

			// Preload the Doctors
			preloadDoctor(1, refDate, 0, 0, 0, true, false);
			preloadDoctor(2, refDate, 3, 5, 0, true, true);
			preloadDoctor(3, refDate.plusDays(1), 0, 0, 0, true, false);
			preloadDoctor(4, refDate.plusDays(1), 0, 0, 0, true, false);
			preloadDoctor(5, refDate.plusDays(2), 0, 0, 0, true, false);
			preloadDoctor(6, refDate.plusDays(2), 3, 5, 0, true, true);
			preloadDoctor(7, refDate.plusDays(3), 3, 5, 0, true, false);
			preloadDoctor(8, refDate.plusDays(3), 0, 0, 0, true, false);
			preloadDoctor(9, refDate.plusDays(4), 4, 5, 0, true, false);
			preloadDoctor(10, refDate.plusDays(4), 3, 5, 0, true, false);
			preloadDoctor(11, refDate.plusDays(5), 3, 5, 0, true, true);
			preloadDoctor(12, refDate.plusDays(5), 0, 0, 0, true, false);
			preloadDoctor(13, refDate.plusDays(6), 3, 5, 2, true, false);
			preloadDoctor(14, refDate.plusDays(6), 3, 5, 0, true, false);
			preloadDoctor(15, refDate.plusDays(7), 3, 5, 0, true, true);
			preloadDoctor(16, refDate.plusDays(7), 3, 5, 0, true, true);
			preloadDoctor(17, refDate.plusDays(8), 4, 5, 2, true, false);
			preloadDoctor(18, refDate.plusDays(8), 3, 5, 0, true, false);
			preloadDoctor(19, refDate.plusDays(9), 3, 5, 0, true, false);
			preloadDoctor(20, refDate.plusDays(9), 4, 5, 2, true, false);
			preloadDoctor(21, refDate.plusDays(10), 4, 5, 2, false, false);
			preloadDoctor(22, refDate.plusDays(10), 3, 5, 0, false, false);
			

			// Preload allowed shifts
			AllowedShift allowedShiftMonday = allowedShiftRepository.save(new AllowedShift("Monday"));
			log.info("Preloading " + allowedShiftMonday);
			AllowedShift allowedShiftTuesday = allowedShiftRepository.save(new AllowedShift("Tuesday"));
			log.info("Preloading " + allowedShiftTuesday);
			AllowedShift allowedShiftWednesday = allowedShiftRepository.save(new AllowedShift("Wednesday"));
			log.info("Preloading " + allowedShiftWednesday);
			AllowedShift allowedShiftThursday = allowedShiftRepository.save(new AllowedShift("Thursday"));
			log.info("Preloading " + allowedShiftThursday);
			AllowedShift allowedShiftFriday = allowedShiftRepository.save(new AllowedShift("Friday"));
			log.info("Preloading " + allowedShiftFriday);

			// To preload a calendar, use:
//			preloadCalendarFor(YearMonth.of(2020, 6));
			
			log.info("The preload of the initial data finished");
		};
	}

	private void preloadCalendarFor(YearMonth yearMonth) {
		LocalDate day = null;
		Calendar calendar = new Calendar(yearMonth.getMonthValue(), yearMonth.getYear());
		DayConfiguration dayConf = null;
		SortedSet<DayConfiguration> dayConfs = new TreeSet<>();
		for (int i = 1; yearMonth.isValidDay(i); i++) {
			day = yearMonth.atDay(i);
			boolean isWorkingDay = day.getDayOfWeek() != DayOfWeek.SUNDAY && day.getDayOfWeek() != DayOfWeek.SATURDAY;
			dayConf = new DayConfiguration(i, isWorkingDay, 2, 0);
			dayConf.setCalendar(calendar);
			dayConfs.add(dayConf);
		}
		calendar.setDayConfigurations(dayConfs);
		calendar = calendarRepository.save(calendar);
		log.info("Preloading: " + calendar);
	}

	private void preloadDoctor(String name, String lastNames, String email, LocalDate startDate, int minShifts,
			int maxShifts, int numConsultations, boolean doesCycleShifts, boolean hasShiftsOnlyWhenCycleShifts) {
		preloadDoctor(name, lastNames, email, startDate, minShifts, maxShifts, numConsultations, doesCycleShifts,
				hasShiftsOnlyWhenCycleShifts, null, null, null);
	}

	private void preloadDoctor(String name, String lastNames, String email, LocalDate startDate, int minShifts,
			int maxShifts, int numConsultations, boolean doesCycleShifts, boolean hasShiftsOnlyWhenCycleShifts,
			Set<AllowedShift> wantedShifts, Set<AllowedShift> unwantedShifts, Set<AllowedShift> wantedConsultations) {
		Doctor doctor = new Doctor(name, lastNames, email, startDate);
		doctor = doctorRepository.save(doctor);
		log.info("Preloading: " + doctor);
		ShiftConfiguration shiftConf = new ShiftConfiguration(minShifts, maxShifts, numConsultations, doesCycleShifts,
				hasShiftsOnlyWhenCycleShifts);
		shiftConf.setDoctor(doctor);
		shiftConf.setWantedShifts(wantedShifts);
		shiftConf.setUnwantedShifts(unwantedShifts);
		shiftConf.setWantedConsultations(wantedConsultations);
		shiftConf = shiftConfRepository.save(shiftConf);
		log.info("Preloading: " + shiftConf);
	}

	private void preloadDoctor(int number, LocalDate startDate, int minShifts, int maxShifts, int numConsultations,
			boolean doesCycleShifts, boolean hasShiftsOnlyWhenCycleShifts) {
		preloadDoctor(number + "", number + "", number + "@guardians.com", startDate, minShifts, maxShifts,
				numConsultations, doesCycleShifts, hasShiftsOnlyWhenCycleShifts);
	}
}
