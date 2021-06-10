package guardians.controllers.exceptions;

import guardians.model.entities.Doctor;

/**
 * This class represents the exception that will be thrown when a requested
 * {@link Doctor} is not found.
 * 
 * @see NotFoundException
 * 
 * @author miggoncan
 */
public class DoctorNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 6757222623745324542L;

	/**
	 * @param doctorId The provided id that could not be matched to an existing
	 *                 {@link Doctor}
	 */
	public DoctorNotFoundException(Long doctorId) {
		super("Could not find the doctor " + doctorId);
	}
	
	/**
	 * @param email An email that no {@link Doctor} has assigned
	 */
	public DoctorNotFoundException(String email) {
		super("Could not fing a doctor with email: " + email);
	}
}
