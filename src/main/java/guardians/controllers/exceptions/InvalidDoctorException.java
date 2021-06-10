package guardians.controllers.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import guardians.model.entities.Doctor;

/**
 * This exception will be thrown whenever a {@link Doctor} violates one or more
 * constraints
 * 
 * @author miggoncan
 */
public class InvalidDoctorException extends InvalidEntityException {
	private static final long serialVersionUID = 2980648984020151610L;

	private String message;

	public InvalidDoctorException(Set<ConstraintViolation<Doctor>> violations) {
		super("Invalid Doctor");
		this.message = "Invalid Doctor: ";
		for (ConstraintViolation<Doctor> constraintViolation : violations) {
			message += constraintViolation.getPropertyPath() + " \"" + constraintViolation.getMessage() + "\" , ";
		}
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
