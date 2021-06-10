package guardians.model.dtos.scheduler;

import guardians.model.entities.CycleChange;
import guardians.model.entities.Doctor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO represents the information related to a {@link CycleChange} exposed
 * to the scheduler
 * 
 * @author miggoncan
 */
@Data
@Slf4j
public class CycleChangeSchedulerDTO {
	private DoctorSchedulerDTO cycleGiver;
	private DoctorSchedulerDTO cycleReceiver;
	
	public CycleChangeSchedulerDTO(CycleChange cycleChange) {
		log.info("Creating a CycleChangeSchedulerDTO from the CycleChange: " + cycleChange);
		if (cycleChange != null) {
			Doctor cycleGiver = cycleChange.getCycleGiver();
			log.debug("The cycleGiver is: " + cycleGiver);
			this.cycleGiver = new DoctorSchedulerDTO(cycleGiver);
			Doctor cycleReceiver = cycleChange.getCycleReceiver();
			log.debug("The cycleReceiver is: " + cycleReceiver);
			this.cycleReceiver = new DoctorSchedulerDTO(cycleReceiver);
		}
		log.info("The created CycleChangeSchedulerDTO is: " + this);
	}
	
	public CycleChangeSchedulerDTO() {
	}
}
