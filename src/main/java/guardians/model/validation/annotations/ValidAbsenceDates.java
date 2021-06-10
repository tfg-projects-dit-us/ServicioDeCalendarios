package guardians.model.validation.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import guardians.model.validation.validators.AbsenceDatesValidator;

/**
 * Uses the algorithm in {@link AbsenceDatesValidator}
 * 
 * @see AbsenceDatesValidator
 * 
 * @author miggoncan
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Constraint(validatedBy = { AbsenceDatesValidator.class })
public @interface ValidAbsenceDates {
	String message() default "{guardians.model.entityvalidation.ValidAbsenceDates.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
