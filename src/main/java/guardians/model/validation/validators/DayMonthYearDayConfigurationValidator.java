package guardians.model.validation.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import guardians.model.entities.DayConfiguration;
import guardians.model.validation.annotations.ValidDayMonthYear;
import lombok.extern.slf4j.Slf4j;

/**
 * This class will apply the validation done by
 * {@link DayMonthYearValidator} to a {@link DayConfiguration}
 * 
 * @author miggoncan
 * @see DayMonthYearValidator
 */
@Slf4j
public class DayMonthYearDayConfigurationValidator extends DayMonthYearValidator
		implements ConstraintValidator<ValidDayMonthYear, DayConfiguration> {
	@Override
	public boolean isValid(DayConfiguration value, ConstraintValidatorContext context) {
		log.debug("Requested validation of DayConfiguration: " + value);
		if (value == null) {
			log.debug("As the DayConfiguration is null, it is considered valid");
			return true;
		}
		
		Integer day = value.getDay();
		Integer month = value.getMonth();
		Integer year = value.getYear();
		if (day == null || month == null || year == null) {
			log.debug("Either the given day, month or year are false. The date is invalid");
			return false;
		}
		
		return this.isValid(day, month, year);
	}

}
