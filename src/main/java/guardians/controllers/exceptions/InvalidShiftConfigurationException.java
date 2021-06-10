package guardians.controllers.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import guardians.model.entities.ShiftConfiguration;

/**
 * This exception will be thrown whenever a {@link ShiftConfiguration} violates
 * one or more constraints
 * 
 * @author miggoncan
s */
public class InvalidShiftConfigurationException extends InvalidEntityException {
	private static final long serialVersionUID = -8805210128281768744L;

	private String message;

	public InvalidShiftConfigurationException(Set<ConstraintViolation<ShiftConfiguration>> violations) {
		super("Invalid ShiftConfiguration");
		this.message = "Invalid ShiftConfiguration: ";
		for (ConstraintViolation<ShiftConfiguration> constraintViolation : violations) {
			message += constraintViolation.getPropertyPath() + " \"" + constraintViolation.getMessage() + "\" , ";
		}
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
