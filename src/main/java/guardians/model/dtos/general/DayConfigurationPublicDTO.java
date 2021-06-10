package guardians.model.dtos.general;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.hateoas.server.core.Relation;

import guardians.model.entities.CycleChange;
import guardians.model.entities.DayConfiguration;
import guardians.model.entities.Doctor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO represents the information related to a {@link DayConfiguration}
 * exposed through the REST interface
 * 
 * @author miggoncan
 */
@Data
@Relation(value = "dayConfiguration", collectionRelation = "dayConfigurations")
@Slf4j
public class DayConfigurationPublicDTO implements Comparable<DayConfigurationPublicDTO> {
	private Integer day;
	private Boolean isWorkingDay;
	private Integer numShifts;
	private Integer numConsultations;
	// TODO Should these doctors have to be represented as embedded resources when
	// serializing?
	private Set<DoctorPublicDTO> unwantedShifts;
	private Set<DoctorPublicDTO> unavailableShifts;
	private Set<DoctorPublicDTO> wantedShifts;
	private Set<DoctorPublicDTO> mandatoryShifts;
	private List<CycleChangePublicDTO> cycleChanges;

	@Override
	public int compareTo(DayConfigurationPublicDTO dayConf) {
		if (dayConf == null) {
			return -1;
		}

		int result = 0;
		Integer dayConfday = dayConf.getDay();
		if (dayConfday == null) {
			if (this.day == null) {
				result = 0;
			} else {
				result = -1;
			}
		} else if (this.day == null) {
			result = 1;
		} else {
			result = this.day - dayConfday;
		}

		return result;
	}

	public DayConfigurationPublicDTO(DayConfiguration dayConfig) {
		log.info("Creating a DayConfigurationPublicDTO from the DayConfiguration: " + dayConfig);
		if (dayConfig != null) {
			this.day = dayConfig.getDay();
			log.debug("The day is: " + this.day);
			this.isWorkingDay = dayConfig.getIsWorkingDay();
			log.debug("Is working day: " + this.isWorkingDay);
			this.numShifts = dayConfig.getNumShifts();
			log.debug("Num shifts is: " + this.numShifts);
			this.numConsultations = dayConfig.getNumConsultations();
			this.unwantedShifts = this.toSetDoctorsDTO(dayConfig.getUnwantedShifts());
			log.debug("unwantedShifts are: " + this.unwantedShifts);
			this.unavailableShifts = this.toSetDoctorsDTO(dayConfig.getUnavailableShifts());
			log.debug("unavailableShifts are: " + this.unavailableShifts);
			this.wantedShifts = this.toSetDoctorsDTO(dayConfig.getWantedShifts());
			log.debug("wantedShifts are: " + this.wantedShifts);
			this.mandatoryShifts = this.toSetDoctorsDTO(dayConfig.getMandatoryShifts());
			log.debug("mandatoryShifts are: " + this.mandatoryShifts);
			
			List<CycleChangePublicDTO> cycleChangeDTOs = new LinkedList<>();
			List<CycleChange> cycleChanges = dayConfig.getCycleChanges();
			for (CycleChange cycleChange : cycleChanges) {
				cycleChangeDTOs.add(new CycleChangePublicDTO(cycleChange));
			}
			this.cycleChanges = cycleChangeDTOs;
			log.debug("cycleChanges are: " + this.cycleChanges);
		}
		log.info("The created DayConfigurationPublicDTO is: " + this);
	}

	public DayConfigurationPublicDTO() {
	}

	public DayConfiguration toDayConfiguration() {
		log.info("Converting to DayConfiguration this DayConfigurationPublicDTO: " + this);
		DayConfiguration dayConfig = new DayConfiguration();
		dayConfig.setDay(this.day);
		log.debug("The DayConfiguration day is: " + dayConfig.getDay());
		dayConfig.setIsWorkingDay(this.isWorkingDay);
		log.debug("The DayConfiguration is a working day: " + dayConfig.getIsWorkingDay());
		dayConfig.setNumShifts(this.numShifts);
		log.debug("The number of shifts is: " + dayConfig.getNumShifts());
		dayConfig.setNumConsultations(this.numConsultations);
		log.debug("The number of consultations is: " + dayConfig.getNumConsultations());
		dayConfig.setUnwantedShifts(this.toSetDoctors(this.unwantedShifts));
		log.debug("The unwantedShifts are: " + dayConfig.getUnwantedShifts());
		dayConfig.setUnavailableShifts(this.toSetDoctors(this.unavailableShifts));
		log.debug("The unavailableShifts are: " + dayConfig.getUnavailableShifts());
		dayConfig.setWantedShifts(this.toSetDoctors(this.wantedShifts));
		log.debug("The wantedShifts are: " + dayConfig.getWantedShifts());
		dayConfig.setMandatoryShifts(this.toSetDoctors(this.mandatoryShifts));
		log.debug("The mandatoryShifts are: " + dayConfig.getMandatoryShifts());
		List<CycleChange> cycleChanges = new LinkedList<>();
		if (this.cycleChanges != null) {
			for (CycleChangePublicDTO cycleChangeDTO : this.cycleChanges) {
				cycleChanges.add(cycleChangeDTO.toCycleChange());
			}
		}
		dayConfig.setCycleChanges(cycleChanges);
		log.debug("The cycleChanges are: " + dayConfig.getCycleChanges());
		log.info("The converted DayConfiguration is: " + dayConfig);
		return dayConfig;
	}
	
	private Set<Doctor> toSetDoctors(Set<DoctorPublicDTO> doctorDTOs) {
		log.info("Converting to a Set of Doctors the Set: " + doctorDTOs);
		Set<Doctor> doctors = new HashSet<>();
		if (doctorDTOs != null) {
			for (DoctorPublicDTO doctorDTO : doctorDTOs) {
				doctors.add(doctorDTO.toDoctor());
			}
		}
		log.info("The converted set is: " + doctors);
		return doctors;
	}
	
	private Set<DoctorPublicDTO> toSetDoctorsDTO(Set<Doctor> doctors) {
		log.info("Converting to a Set of DoctorPublicDTOs the Set: " + doctors);
		Set<DoctorPublicDTO> doctorDTOs = new HashSet<>();
		if (doctors != null) {
			for (Doctor doctor : doctors) {
				doctorDTOs.add(new DoctorPublicDTO(doctor));
			}
		}
		log.info("The converted set is: " + doctorDTOs);
		return doctorDTOs;
	}
}
