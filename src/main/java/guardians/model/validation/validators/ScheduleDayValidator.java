package guardians.model.validation.validators;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import guardians.model.entities.Doctor;
import guardians.model.entities.ScheduleDay;
import guardians.model.validation.annotations.ValidScheduleDay;
import lombok.extern.slf4j.Slf4j;

/**
 * This validator will make sure that if a {@link ScheduleDay} is a working day,
 * then its shifts have to be set. Otherwise, it cannot have any shifts.
 * 
 * @author miggoncan
 */
@Slf4j
public class ScheduleDayValidator implements ConstraintValidator<ValidScheduleDay, ScheduleDay> {

	@Override
	public boolean isValid(ScheduleDay value, ConstraintValidatorContext context) {
		log.info("Request to validate the schedule day: " + value);
		if (value == null) {
			log.debug("The given ScheduleDay is null, which is considered a valid value");
			return true;
		}

		Boolean isWorkingDay = value.getIsWorkingDay();
		if (isWorkingDay == null) {
			log.debug("IsWorkingDay is null. The scheduleDay is not valid");
			return false;
		}

		boolean isValid = false;
		Set<Doctor> shifts = value.getShifts();
		if (isWorkingDay) {
			log.debug("The day is a working day. Checking that shifts is not null and not empty");
			isValid = shifts != null && shifts.size() != 0;
		} else {
			log.debug("The day is not a working day. Checking that shifts is null or empty");
			isValid = shifts == null || shifts.size() == 0;
		}

		log.info("The given schedule day is valid: " + isValid);
		return isValid;
	}

}
