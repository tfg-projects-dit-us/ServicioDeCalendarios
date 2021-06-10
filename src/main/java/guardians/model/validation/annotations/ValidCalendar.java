package guardians.model.validation.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import guardians.model.validation.validators.CalendarValidator;

/**
 * Uses the algorithm in {@link CalendarValidator}
 * 
 * @see CalendarValidator
 * 
 * @author miggoncan
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Constraint(validatedBy = { CalendarValidator.class })
public @interface ValidCalendar {
	String message() default "{guardians.model.entityvalidation.ValidCalendar.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
