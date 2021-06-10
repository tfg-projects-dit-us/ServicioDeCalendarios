package guardians.model.validation.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import guardians.model.entities.Doctor;
import guardians.model.entities.ShiftConfiguration;
import guardians.model.validation.annotations.ValidShiftConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to validate the {@link ShiftConfiguration}s annotated with
 * {@link ValidShiftConfiguration}
 * 
 * Particularly, this class is used to make sure that the number of minimum
 * shifts a {@link Doctor} does is less than or equal to its maximum number of
 * shifts
 * 
 * @author miggoncan
 */
@Slf4j
public class ShiftConfigurationValidator
		implements ConstraintValidator<ValidShiftConfiguration, ShiftConfiguration> {
	@Override
	public boolean isValid(ShiftConfiguration value, ConstraintValidatorContext context) {
		log.debug("Request to validate the shift configuration: " + value);
		if (value == null) {
			log.debug("As the shift configuration is null, it is considered valid");
			return true;
		}

		Integer min = value.getMinShifts();
		Integer max = value.getMaxShifts();
		boolean minMaxShiftsAreValid = min != null && max != null && min <= max;
		log.debug("The given shift configuration has valid min and max shifts: " + minMaxShiftsAreValid);


		return minMaxShiftsAreValid;
	}
}
