package guardians.model.dtos.general;

import java.util.HashSet;
import java.util.Set;

import org.springframework.hateoas.server.core.Relation;

import guardians.model.entities.Doctor;
import guardians.model.entities.ScheduleDay;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO represents the information related to a {@link ScheduleDay} exposed
 * through the REST interface
 * 
 * @author miggoncan
 */
@Data
@Relation(value = "scheduleDay", collectionRelation = "scheduleDays")
@Slf4j
public class ScheduleDayPublicDTO implements Comparable<ScheduleDayPublicDTO>{
	private Integer day;
	private Boolean isWorkingDay;
	private Set<DoctorPublicDTO> cycle;
	private Set<DoctorPublicDTO> shifts;
	private Set<DoctorPublicDTO> consultations;
	
	@Override
	public int compareTo(ScheduleDayPublicDTO scheduleDay) {
		if (scheduleDay == null) {
			return -1;
		}
		
		int result = 0;
		Integer scheduleDayDay = scheduleDay.getDay();
		if (scheduleDayDay == null) {
			if (this.day == null) {
				result = 0;
			} else {
				result = -1;
			}
		} else if (this.day == null) {
			result = 1;
		} else {
			result = this.day - scheduleDayDay;
		}
		
		return result;
	}
	
	public ScheduleDayPublicDTO(ScheduleDay scheduleDay) {
		log.info("Creating a ScheduleDayPublicDTO from the ScheduleDay: " + scheduleDay);
		if (scheduleDay != null) {
			this.day = scheduleDay.getDay();
			this.isWorkingDay = scheduleDay.getIsWorkingDay();
			this.cycle = this.toSetDoctorsDTO(scheduleDay.getCycle());
			this.shifts = this.toSetDoctorsDTO(scheduleDay.getShifts());
			this.consultations = this.toSetDoctorsDTO(scheduleDay.getConsultations());
		}
		log.info("The crated ScheduleDayPublicDTO is: " + this);
	}
	
	public ScheduleDayPublicDTO() {
	}
	
	public ScheduleDay toScheduleDay() {
		log.info("Converting to a ScheduleDay this ScheduleDayPublicDTO: " + this);
		ScheduleDay scheduleDay = new ScheduleDay();
		scheduleDay.setDay(this.day);
		scheduleDay.setIsWorkingDay(this.isWorkingDay);
		scheduleDay.setCycle(this.toSetDoctors(this.cycle));
		scheduleDay.setShifts(this.toSetDoctors(this.shifts));
		scheduleDay.setConsultations(this.toSetDoctors(this.consultations));
		log.info("The converted ScheduleDay is: " + scheduleDay);
		return scheduleDay;
	}
	
	private Set<Doctor> toSetDoctors(Set<DoctorPublicDTO> doctorDTOs) {
		log.info("Converting to a Set of Doctors the set: " + doctorDTOs);
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
		log.info("Converting to a Set of DoctorPublicDTOss the set: " + doctors);
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
