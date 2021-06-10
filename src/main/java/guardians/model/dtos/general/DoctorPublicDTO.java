package guardians.model.dtos.general;

import org.springframework.hateoas.server.core.Relation;

import guardians.model.entities.Absence;
import guardians.model.entities.Doctor;
import guardians.model.entities.Doctor.DoctorStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO will represent the information exposed through the REST interface
 * related to a {@link Doctor}
 * 
 * @author miggoncan
 */
@Data
@Relation(value = "doctor", collectionRelation = "doctors")
@Slf4j
public class DoctorPublicDTO {
	private Long id;
	private String firstName;
	private String lastNames;
	private String email;
	private DoctorStatus status;
	private AbsencePublicDTO absence;

	public DoctorPublicDTO(Doctor doctor) {
		log.info("Creating a DoctorPublicDTO from the Doctor: " + doctor);
		if (doctor != null) {
			this.id = doctor.getId();
			this.firstName = doctor.getFirstName();
			this.lastNames = doctor.getLastNames();
			this.email = doctor.getEmail();
			this.status = doctor.getStatus();
			Absence absence = doctor.getAbsence();
			if (absence != null) {
				this.absence = new AbsencePublicDTO(absence);
			}
		}
		log.info("The created DoctorPublicDTO is: " + this);
	}

	public DoctorPublicDTO() {
	}

	public Doctor toDoctor() {
		log.info("Converting this DoctorPublicDTO to a Doctor: " + this);
		Doctor doctor = new Doctor();
		doctor.setId(this.id);
		doctor.setFirstName(this.firstName);
		doctor.setLastNames(this.lastNames);
		doctor.setEmail(this.email);
		doctor.setStatus(this.status);
		if (this.absence != null) {
			doctor.setAbsence(this.absence.toAbsence());
		}
		log.info("The converted Doctor is: " + doctor);
		return doctor;
	}

}
