package guardians.controllers.exceptions;
/**
 * 
 * @author carcohcal
 * Excepción que se ejecuta cuando no existe un evento con el id y fecha indicado
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
