package guardians.controllers.exceptions;

import guardians.model.entities.Calendar;

/**
 * This exception will be thrown whenever a requested {@link Calendar} is not
 * found in the database
 * 
 * @author miggoncan
 */
public class CalendarNotFoundException extends NotFoundException {
	private static final long serialVersionUID = -3853849068364093370L;

	public CalendarNotFoundException(Integer month, Integer year) {
		super("The calendar of " + month + "/" + year + " was not found");
	}
}
