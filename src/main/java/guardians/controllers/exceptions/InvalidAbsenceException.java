package guardians.controllers.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import guardians.model.entities.Absence;

/**
 * This class represents the exception to be thrown when an {@link Absence} is
 * not valid
 * 
 * @see InvalidEntityException
 * 
 * @author miggoncan
 */
public class InvalidAbsenceException extends InvalidEntityException {
	private static final long serialVersionUID = -1741747343694240379L;

	private String message;

	public InvalidAbsenceException(Set<ConstraintViolation<Absence>> violations) {
		super("Invalid Absence");
		this.message = "Invalid Absence: ";
		for (ConstraintViolation<Absence> constraintViolation : violations) {
			message += constraintViolation.getPropertyPath() + " \"" + constraintViolation.getMessage() + "\" , ";
		}
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
