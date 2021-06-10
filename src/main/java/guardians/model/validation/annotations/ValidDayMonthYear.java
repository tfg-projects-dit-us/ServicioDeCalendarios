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

import guardians.model.validation.validators.DayMonthYearCycleChangeValidator;
import guardians.model.validation.validators.DayMonthYearDayConfigurationValidator;
import guardians.model.validation.validators.DayMonthYearScheduleDayValidator;
import guardians.model.validation.validators.DayMonthYearValidator;

/**
 * The {@link ConstraintValidator}s used to validate classes annotated with
 * {@link ValidDayMonthYear} will use the algorithm in
 * {@link DayMonthYearValidator}
 * 
 * @see DayMonthYearValidator
 * @see DayMonthYearDayConfigurationValidator
 * @see DayMonthYearScheduleDayValidator
 * @see DayMonthYearCycleChangeValidator
 * 
 * @author miggoncan
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Constraint(validatedBy = { DayMonthYearScheduleDayValidator.class, DayMonthYearCycleChangeValidator.class,
		DayMonthYearDayConfigurationValidator.class })
public @interface ValidDayMonthYear {
	String message() default "{guardians.model.entityvalidation.ValidDayMonthYear.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
