package guardians.controllers.exceptions;

import java.time.Month;

import guardians.model.entities.Calendar;

/**
 * This exception will be thrown whenever a requested {@link Calendar} is not
 * found in the database
 * @author miggoncan
 * @version 2.0 by carcohcal 
 */
public class CalendarNotFoundException extends NotFoundException {
	private static final long serialVersionUID = -3853849068364093370L;

	public CalendarNotFoundException(Integer month, Integer year) {
		super("The calendar of " + month + "/" + year + " was not found");
	}
	
	public CalendarNotFoundException(Month month, Integer year, String email) {
		super("El calendario para " + month + "/" + year + " no ha sido encontrado para el doctor con email "+email);
	}
}
