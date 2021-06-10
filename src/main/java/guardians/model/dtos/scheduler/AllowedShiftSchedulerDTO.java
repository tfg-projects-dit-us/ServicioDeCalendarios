package guardians.model.dtos.scheduler;

import guardians.model.entities.AllowedShift;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO represents the information related to an {@link AllowedShift}
 * exposed to the scheduler
 * 
 * @author miggoncan
 */
@Data
@Slf4j
public class AllowedShiftSchedulerDTO {
	private Integer id;
	private String shift;
	
	public AllowedShiftSchedulerDTO(AllowedShift allowedShift) {
		log.info("Creating an AllowedShiftSchedulerDTO from the AllowedShift: " + allowedShift);
		if (allowedShift != null) {
			this.id = allowedShift.getId();
			log.debug("The AllowedShift id is: " + this.id);
			this.shift = allowedShift.getShift();
			log.debug("The AllowedShift shif is: " + this.shift);
		}
		log.info("The created AllowedShiftSchedulerDTO is: " + this);
	}
	
	public AllowedShiftSchedulerDTO() {
	}
}
