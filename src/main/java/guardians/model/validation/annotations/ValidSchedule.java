package guardians.model.validation.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import guardians.model.validation.validators.ScheduleValidator;

/**
 * Uses the algorithm in {@link ScheduleValidator}
 * 
 * @see ScheduleValidator
 * 
 * @author miggoncan
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Constraint(validatedBy = { ScheduleValidator.class })
public @interface ValidSchedule {
	String message() default "{guardians.model.entityvalidation.ValidSchedule.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
