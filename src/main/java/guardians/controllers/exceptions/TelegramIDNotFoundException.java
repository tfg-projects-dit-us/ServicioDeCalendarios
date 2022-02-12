package guardians.controllers.exceptions;
/**
 * Excepción para cuando se solicita recuperar  un id de telegram inexistente.
 * @author carcohcal
 * @date 12 feb. 2022
 */

public class TelegramIDNotFoundException extends NotFoundException {

	public TelegramIDNotFoundException(Long id) {
		super("No hay TelegramID para el doctor: "+id);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1349266216059380613L;

}
