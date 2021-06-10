package guardians.model.dtos.general;

import org.springframework.hateoas.server.core.Relation;

import guardians.model.entities.AllowedShift;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO represents the information related to an {@link AllowedShift}
 * exposed through the REST interface
 * 
 * @author miggoncan
 */
@Data
@Relation(value = "allowedShift", collectionRelation = "allowedShifts")
@Slf4j
public class AllowedShiftPublicDTO {
	private Integer id;
	private String shift;

	public AllowedShiftPublicDTO(AllowedShift allowedShift) {
		log.info("Creating an AllowedShiftPublicDTO from the AllowedShift: " + allowedShift);
		if (allowedShift != null) {
			this.id = allowedShift.getId();
			log.debug("The AllowedShift id is: " + this.id);
			this.shift = allowedShift.getShift();
			log.debug("The AllowedShift shif is: " + this.shift);
		}
		log.info("The created AllowedShiftPublicDTO is: " + this);
	}

	public AllowedShiftPublicDTO() {
	}

	public AllowedShift toAllowedShift() {
		log.info("Creating an AllowedShift from this AllowedShiftPublicDTO: " + this);
		AllowedShift allowedShift = new AllowedShift();
		allowedShift.setId(this.id);
		log.debug("The AllowedShift id is: " + allowedShift.getId());
		allowedShift.setShift(this.shift);
		log.debug("The AllowedShift shift is: " + allowedShift.getShift());
		log.info("The created AllowedShift is: " + allowedShift);
		return allowedShift;
	}
}
