package guardians.controllers.exceptions;

import javax.persistence.Entity;

/**
 * All exceptions that represent an {@link Entity} is not valid will extend this
 * one
 * 
 * @author miggoncan
 */
public class InvalidEntityException extends RuntimeException {
	private static final long serialVersionUID = 1011155650323241517L;
	
	public InvalidEntityException(String message) {
		super(message);
	}
}
