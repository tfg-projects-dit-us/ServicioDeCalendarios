package guardians.controllers.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import guardians.model.entities.Calendar;

/**
 * This exception will be thrown whenever a {@link Calendar} violates one or
 * more constraints
 * 
 * @author miggoncan
 */
public class InvalidCalendarException extends InvalidEntityException {
	private static final long serialVersionUID = -6656008816495913599L;

	private String message;

	public InvalidCalendarException(Set<ConstraintViolation<Calendar>> violations) {
		super("Invalid Calendar");
		this.message = "Invalid Calendar: ";
		for (ConstraintViolation<Calendar> constraintViolation : violations) {
			message += constraintViolation.getPropertyPath() + " \"" + constraintViolation.getMessage() + "\" , ";
		}
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
