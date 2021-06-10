package guardians.controllers.exceptions;

import guardians.model.entities.AllowedShift;

/**
 * This class represents the exception that will be thrown whenever a requested
 * {@link AllowedShift} is not found
 * 
 * @see NotFoundException
 * 
 * @author miggoncan
 */
public class AllowedShiftNotFoundException extends NotFoundException {
	private static final long serialVersionUID = -2170722200871199083L;

	/**
	 * @param allowedShiftId The id that could not be matched to any
	 *                       {@link AllowedShift}
	 */
	public AllowedShiftNotFoundException(Integer allowedShiftId) {
		super("Could not find the AllowedShift " + allowedShiftId);
	}

}
