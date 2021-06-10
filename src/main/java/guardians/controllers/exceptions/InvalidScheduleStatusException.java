package guardians.controllers.exceptions;

import guardians.model.entities.Schedule.ScheduleStatus;

/**
 * This exception will be thrown when a {@link ScheduleStatus} is to be created
 * from a String, and the status does not exist
 * 
 * @author miggoncan
 */
public class InvalidScheduleStatusException extends RuntimeException {
	private static final long serialVersionUID = 301831686866023550L;

	public InvalidScheduleStatusException(String statusStr) {
		super("The status \"" + statusStr + "\" is not valid");
	}
}
