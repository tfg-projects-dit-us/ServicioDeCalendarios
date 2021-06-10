package guardians.model.dtos.general;

import java.util.HashSet;
import java.util.Set;

import org.springframework.hateoas.server.core.Relation;

import guardians.model.entities.AllowedShift;
import guardians.model.entities.ShiftConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO represents the information related to a {@link ShiftConfiguration}
 * exposed through the REST interface
 * 
 * @author miggoncan s
 */
@Data
@Relation(value = "shiftConfig", collectionRelation = "shifConfigs")
@Slf4j
public class ShiftConfigurationPublicDTO {
	private Long doctorId;
	private Integer maxShifts;
	private Integer minShifts;
	private Integer numConsultations;
	private Boolean doesCycleShifts;
	private Boolean hasShiftsOnlyWhenCycleShifts;
	private Set<AllowedShiftPublicDTO> unwantedShifts;
	private Set<AllowedShiftPublicDTO> unavailableShifts;
	private Set<AllowedShiftPublicDTO> wantedShifts;
	private Set<AllowedShiftPublicDTO> mandatoryShifts;
	private Set<AllowedShiftPublicDTO> wantedConsultations;

	public ShiftConfigurationPublicDTO(ShiftConfiguration shiftConfiguration) {
		log.info("Creating a ShictConfigurationPublicDTO from the ShiftConfiguration: " + shiftConfiguration);
		if (shiftConfiguration != null) {
			this.doctorId = shiftConfiguration.getDoctorId();
			this.maxShifts = shiftConfiguration.getMaxShifts();
			this.minShifts = shiftConfiguration.getMinShifts();
			this.numConsultations = shiftConfiguration.getNumConsultations();
			this.doesCycleShifts = shiftConfiguration.getDoesCycleShifts();
			this.hasShiftsOnlyWhenCycleShifts = shiftConfiguration.getHasShiftsOnlyWhenCycleShifts();
			this.unwantedShifts = this.toSetAllowedShiftDTOs(shiftConfiguration.getUnwantedShifts());
			this.unavailableShifts = this.toSetAllowedShiftDTOs(shiftConfiguration.getUnavailableShifts());
			this.wantedShifts = this.toSetAllowedShiftDTOs(shiftConfiguration.getWantedShifts());
			this.mandatoryShifts = this.toSetAllowedShiftDTOs(shiftConfiguration.getMandatoryShifts());
			this.wantedConsultations = this.toSetAllowedShiftDTOs(shiftConfiguration.getWantedConsultations());
		}
		log.info("The created ShictConfigurationPublicDTO is: " + this);
	}

	public ShiftConfigurationPublicDTO() {
	}

	public ShiftConfiguration toShiftConfiguration() {
		log.info("Creating a ShiftConfiguration from this ShictConfigurationPublicDTO: " + this);
		ShiftConfiguration shiftConf = new ShiftConfiguration();
		shiftConf.setDoctorId(this.doctorId);
		shiftConf.setMaxShifts(this.maxShifts);
		shiftConf.setMinShifts(this.minShifts);
		shiftConf.setNumConsultations(this.numConsultations);
		shiftConf.setDoesCycleShifts(this.doesCycleShifts);
		shiftConf.setHasShiftsOnlyWhenCycleShifts(this.hasShiftsOnlyWhenCycleShifts);
		shiftConf.setUnwantedShifts(this.toSetAllowedShifts(this.unwantedShifts));
		shiftConf.setWantedShifts(this.toSetAllowedShifts(this.wantedShifts));
		shiftConf.setWantedConsultations(this.toSetAllowedShifts(this.wantedConsultations));
		log.info("The created ShiftConfigurations is: " + shiftConf);
		return shiftConf;
	}

	private Set<AllowedShiftPublicDTO> toSetAllowedShiftDTOs(Set<AllowedShift> allowedShifts) {
		log.info("Creating a Set of AllowedShiftPublicDTOs from the set: " + allowedShifts);
		Set<AllowedShiftPublicDTO> allowedShiftDTOs = new HashSet<>();
		if (allowedShifts != null) {
			for (AllowedShift allowedShift : allowedShifts) {
				allowedShiftDTOs.add(new AllowedShiftPublicDTO(allowedShift));
			}
		}
		log.info("The created set is: " + allowedShiftDTOs);
		return allowedShiftDTOs;
	}

	private Set<AllowedShift> toSetAllowedShifts(Set<AllowedShiftPublicDTO> allowedShiftDTOs) {
		log.info("Creating a Set of AllowedShifts from: " + allowedShiftDTOs);
		Set<AllowedShift> allowedShifts = new HashSet<>();
		if (allowedShiftDTOs != null) {
			for (AllowedShiftPublicDTO allowedShiftDTO : allowedShiftDTOs) {
				allowedShifts.add(allowedShiftDTO.toAllowedShift());
			}
		}
		log.info("The created Set is: " + allowedShifts);
		return allowedShifts;
	}
}
