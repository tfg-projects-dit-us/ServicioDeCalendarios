package guardians.controllers.exceptions;

/**
 * All exceptions that represent a resource has not been found will extend this
 * one
 * 
 * @author miggoncan
 */
public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 4836198802844598407L;

	public NotFoundException(String message) {
		super(message);
	}
}
