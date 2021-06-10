package guardians.controllers.exceptions;

import guardians.model.entities.Calendar;

/**
 * This exception will be thown whenever a {@link Calendar} is to be created,
 * but it already exists
 * 
 * @author miggoncan
 */
public class CalendarAlreadyExistsException extends AlreadyExistsException {
	private static final long serialVersionUID = 8667397497532703688L;

	public CalendarAlreadyExistsException(Integer month, Integer year) {
		super("The calendar for " + month + "/" + year + " already exists");
	}
}
