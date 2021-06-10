package guardians.model.validation.validators;

import java.time.DateTimeException;
import java.time.YearMonth;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import guardians.model.entities.Calendar;
import guardians.model.entities.DayConfiguration;
import guardians.model.validation.annotations.ValidCalendar;
import lombok.extern.slf4j.Slf4j;

/**
 * This validator will make sure that a {@link Calendar} contains all the
 * {@link DayConfiguration}s given its month and year
 * 
 * @author miggoncan
 */
@Slf4j
public class CalendarValidator implements ConstraintValidator<ValidCalendar, Calendar> {

	@Override
	public boolean isValid(Calendar value, ConstraintValidatorContext context) {
		log.debug("Request to validate the Calendar: " + value);
		if (value == null) {
			log.debug("The given calendar is null. The calendar is considered valid");
			return true;
		}

		Integer year = value.getYear();
		Integer month = value.getMonth();
		if (year == null || month == null) {
			log.debug("Either the year or the month are null. The calendar is invalid");
			return false;
		}

		YearMonth yearMonth = null;
		try {
			yearMonth = YearMonth.of(year, month);
		} catch (DateTimeException e) {
			log.debug("The year and month are not valid: " + e.getMessage());
			return false;
		}

		DaysOfMonthChecker<DayConfiguration> checker = new DaysOfMonthChecker<>(yearMonth);
		boolean isValid = checker.areAllDaysPresent(value.getDayConfigurations());

		log.debug("The calendar is valid: " + isValid);
		return isValid;
	}

}
