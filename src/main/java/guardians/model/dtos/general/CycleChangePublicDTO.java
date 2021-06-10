package guardians.model.dtos.general;

import org.springframework.hateoas.server.core.Relation;

import guardians.model.entities.CycleChange;
import guardians.model.entities.Doctor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO represents the information related to a {@link CycleChange} exposed
 * through the REST interface
 * 
 * @author miggoncan
 */
@Data
@Relation(value = "cycleChange", collectionRelation = "cycleChanges")
@Slf4j
public class CycleChangePublicDTO {
	// TODO Should these doctors have to be represented as embedded resources when serializing?
	private DoctorPublicDTO cycleGiver;
	private DoctorPublicDTO cycleReceiver;
	
	public CycleChangePublicDTO(CycleChange cycleChange) {
		log.info("Creating a CycleChangePublicDTO from the CycleChange: " + cycleChange);
		if (cycleChange != null) {
			Doctor cycleGiver = cycleChange.getCycleGiver();
			log.debug("The cycleGiver is: " + cycleGiver);
			this.cycleGiver = new DoctorPublicDTO(cycleGiver);
			Doctor cycleReceiver = cycleChange.getCycleReceiver();
			log.debug("The cycleReceiver is: " + cycleReceiver);
			this.cycleReceiver = new DoctorPublicDTO(cycleReceiver);
		}
		log.info("The created CycleChangePublicDTO is: " + this);
	}
	
	public CycleChangePublicDTO() {
	}

	public CycleChange toCycleChange() {
		log.info("Creating a CycleChange from this CycleChangePublicDTO: " + this);
		CycleChange cycleChange = new CycleChange();
		log.debug("This cycleGiver is: " + this.cycleGiver);
		cycleChange.setCycleGiver(this.cycleGiver.toDoctor());
		log.debug("The cycleChange cycleGiver is: " + cycleChange.getCycleGiver());
		log.debug("This cycleReceiver is: " + this.cycleReceiver);
		cycleChange.setCycleReceiver(this.cycleReceiver.toDoctor());
		log.debug("The cycleChange cycleReceiver is: " + cycleChange.getCycleReceiver());
		log.info("The created CycleChange is: " + cycleChange);
		return cycleChange;
	}
}
