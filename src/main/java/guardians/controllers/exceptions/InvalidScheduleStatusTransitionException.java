package guardians.controllers.exceptions;

import guardians.model.entities.Schedule;
import guardians.model.entities.Schedule.ScheduleStatus;

/**
 * This exception will be thrown when a {@link Schedule} is to be transitions
 * from one state to another, but the transition is not valid
 * 
 * @author miggoncan
 */
public class InvalidScheduleStatusTransitionException extends RuntimeException {
	private static final long serialVersionUID = -3904285005206514514L;

	public InvalidScheduleStatusTransitionException(ScheduleStatus oldStatus, ScheduleStatus newStatus) {
		super("Cannot transition from the status " + oldStatus + " to " + newStatus);
	}
}
