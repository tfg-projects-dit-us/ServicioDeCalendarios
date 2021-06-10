package guardians.controllers.exceptions;

import guardians.model.entities.Schedule;

/**
 * This exception will be thrown whenever a {@link Schedule} is to be generated,
 * but it already exists
 * 
 * @author miggoncan
 */
public class ScheduleAlreadyExistsException extends AlreadyExistsException {
	private static final long serialVersionUID = -7858096920008335789L;

	public ScheduleAlreadyExistsException(Integer month, Integer year) {
		super("The schedule of " + month + "/" + year + " already exists");
	}
}
