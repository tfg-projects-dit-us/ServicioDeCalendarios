package guardians.model.validation.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;

import guardians.model.validation.validators.ShiftPreferencesDayConfigValidator;
import guardians.model.validation.validators.ShiftPreferencesShiftConfigValidator;
import guardians.model.validation.validators.ShiftPreferencesValidator;

/**
 * The {@link ConstraintValidator}s used to validate classes annotated by
 * {@link ValidShiftPreferences} will use the algorithm defined in
 * {@link ShiftPreferencesValidator}
 * 
 * @see ShiftPreferencesValidator
 * @see ShiftPreferencesShiftConfigValidator
 * @see ShiftPreferencesDayConfigValidator
 * 
 * @author miggoncan
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Constraint(validatedBy = { ShiftPreferencesShiftConfigValidator.class, ShiftPreferencesDayConfigValidator.class })
public @interface ValidShiftPreferences {
	String message() default "{guardians.model.entityvalidation.ValidShiftPreferences.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
