package guardians.controllers.exceptions;
/**
 * Excepción que se ejecuta cuando no existe un evento ( @see VEvent) con el id y fecha indicado
 * @author carcohcal
 * @date 12 feb. 2022
 * @version 1.0
 */
public class EventNotFoundException extends NotFoundException {

	
	private static final long serialVersionUID = 185554261145281608L;
/**
 * 
 * @param message
 */
	public EventNotFoundException(String message) {
		super(message);
		
	}
/**
 * 
 * @param id
 * @param fecha
 */
	public EventNotFoundException(String id, String fecha) {
		super("No hay evento con el id: " + id + " para la siguiente fecha: " + fecha);
	}

}
