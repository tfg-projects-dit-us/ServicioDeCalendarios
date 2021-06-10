package guardians.controllers.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import guardians.model.entities.Schedule;

/**
 * This exception will be thrown whenever a {@link Schedule} violates one or
 * more constraints
 * 
 * @author miggoncan
 */
public class InvalidScheduleException extends InvalidEntityException {
	private static final long serialVersionUID = -2609473270307366154L;

	private String message;

	public InvalidScheduleException(Set<ConstraintViolation<Schedule>> violations) {
		super("Invalid Schedule");
		this.message = "Invalid Schedule: ";
		for (ConstraintViolation<Schedule> constraintViolation : violations) {
			message += constraintViolation.getPropertyPath() + " \"" + constraintViolation.getMessage() + "\" , ";
		}
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
