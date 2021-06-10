package guardians.controllers.exceptions;

import guardians.model.entities.Doctor;

/**
 * This exception will be thrown whenever a {@link Doctor} is going to be
 * edited, but it is in the DELETED status
 * 
 * @author miggoncan
 */
public class DoctorDeletedException extends RuntimeException {
	private static final long serialVersionUID = -4886690300445689866L;

	public DoctorDeletedException(Long doctorId) {
		super("The doctor with id: " + doctorId + " is deleted, so it cannot be modified");
	}
}
