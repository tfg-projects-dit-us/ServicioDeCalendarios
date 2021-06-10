package guardians.model.validation.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import guardians.model.entities.DayConfiguration;
import guardians.model.entities.Doctor;
import guardians.model.validation.annotations.ValidShiftPreferences;

/**
 * This validator applies the algorithm in
 * {@link ShiftPreferencesValidator} to the shift preferences of a
 * {@link DayConfiguration}
 * 
 * @author miggoncan
 */
public class ShiftPreferencesDayConfigValidator
		implements ConstraintValidator<ValidShiftPreferences, DayConfiguration> {
	@Override
	public boolean isValid(DayConfiguration value, ConstraintValidatorContext context) {
		ShiftPreferencesValidator<Doctor> validator = new ShiftPreferencesValidator<>();
		return validator.isValid(value.getUnwantedShifts(), value.getUnavailableShifts(), value.getWantedShifts(),
				value.getMandatoryShifts());
	}
}
