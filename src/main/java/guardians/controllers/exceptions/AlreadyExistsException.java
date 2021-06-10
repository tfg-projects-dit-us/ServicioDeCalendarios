package guardians.controllers.exceptions;

import javax.persistence.Entity;

/**
 * All exceptions that represent an {@link Entity} has been tried to be
 * newly created but already existed will extend this one
 * 
 * @author miggoncan
 */
public class AlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 5603968399455121451L;

	public AlreadyExistsException(String message) {
		super(message);
	}

}
