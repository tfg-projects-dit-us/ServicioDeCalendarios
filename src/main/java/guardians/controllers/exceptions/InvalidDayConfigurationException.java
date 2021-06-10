package guardians.controllers.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import guardians.model.entities.DayConfiguration;

/**
 * This exception will be thrown whenever a {@link DayConfiguration} violates
 * one or more constraint
 * 
 * @author miggoncan
 */
public class InvalidDayConfigurationException extends InvalidEntityException {
	private static final long serialVersionUID = 1061709581152919258L;

	private String message;

	public InvalidDayConfigurationException(Set<ConstraintViolation<DayConfiguration>> violations) {
		super("Invalid DayConfiguration");
		this.message = "Invalid day configuration: ";
		for (ConstraintViolation<DayConfiguration> constraintViolation : violations) {
			message += constraintViolation.getPropertyPath() + " \"" + constraintViolation.getMessage() + "\" , ";
		}
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
