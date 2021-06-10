package guardians.controllers.exceptions;

import guardians.model.entities.Calendar;
import guardians.model.entities.Schedule;
import guardians.model.entities.Schedule.ScheduleStatus;

/**
 * This class represents the exception that will be thrown whenever the
 * {@link Calendar} related to a {@link Schedule} is not found in the database.
 * 
 * This is because, if the related {@link Calendar} exists, the {@link Schedule}
 * is considered to be in the {@link ScheduleStatus#NOT_CREATED} state.
 * 
 * Note: a {@link Schedule} is said to be related to a {@link Calendar} if both
 * their month and year are equal
 * 
 * @see NotFoundException
 * 
 * @author miggoncan
 */
public class ScheduleNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 7293574842299023541L;

	public ScheduleNotFoundException(Integer month, Integer year) {
		super("The schedule for " + month + "-" + year + " could no be found");
	}
}
