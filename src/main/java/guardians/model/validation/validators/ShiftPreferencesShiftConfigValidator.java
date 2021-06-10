package guardians.model.validation.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import guardians.model.entities.AllowedShift;
import guardians.model.entities.ShiftConfiguration;
import guardians.model.validation.annotations.ValidShiftPreferences;

/**
 * This validator applies the algorithm in
 * {@link ShiftPreferencesValidator} to the shift preferences of a
 * {@link ShiftConfiguration}
 * 
 * @author miggoncan
 */
public class ShiftPreferencesShiftConfigValidator
		implements ConstraintValidator<ValidShiftPreferences, ShiftConfiguration> {
	@Override
	public boolean isValid(ShiftConfiguration value, ConstraintValidatorContext context) {
		ShiftPreferencesValidator<AllowedShift> validator = new ShiftPreferencesValidator<>();
		return validator.isValid(value.getUnwantedShifts(), value.getUnavailableShifts(), value.getWantedShifts(),
				value.getMandatoryShifts());
	}
}
