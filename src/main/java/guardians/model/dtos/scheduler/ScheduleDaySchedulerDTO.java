package guardians.model.dtos.scheduler;

import java.util.HashSet;
import java.util.Set;

import guardians.model.entities.Doctor;
import guardians.model.entities.ScheduleDay;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO will represent the information related to a {@link ScheduleDay}
 * exposed to the scheduler
 * 
 * @author miggoncan
 */
@Data
@Slf4j
public class ScheduleDaySchedulerDTO implements Comparable<ScheduleDaySchedulerDTO>{
	private Integer day;
	private Boolean isWorkingDay;
	private Set<DoctorSchedulerDTO> cycle;
	private Set<DoctorSchedulerDTO> shifts;
	private Set<DoctorSchedulerDTO> consultations;
	
	@Override
	public int compareTo(ScheduleDaySchedulerDTO scheduleDay) {
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

	public ScheduleDay toScheduleDay() {
		log.info("Converting to a ScheduleDay this ScheduleDaySchedulerDTO: " + this);
		ScheduleDay scheduleDay = new ScheduleDay();
		scheduleDay.setDay(this.day);
		scheduleDay.setIsWorkingDay(this.isWorkingDay);
		scheduleDay.setCycle(this.toSetDoctors(this.cycle));
		scheduleDay.setShifts(this.toSetDoctors(this.shifts));
		scheduleDay.setConsultations(this.toSetDoctors(this.consultations));
		log.info("The converted ScheduleDay is: " + scheduleDay);
		return scheduleDay;
	}
	
	private Set<Doctor> toSetDoctors(Set<DoctorSchedulerDTO> doctorDTOs) {
		log.info("Converting to a Set of Doctors the set: " + doctorDTOs);
		Set<Doctor> doctors = new HashSet<>();
		if (doctorDTOs != null) {
			for (DoctorSchedulerDTO doctorDTO : doctorDTOs) {
				doctors.add(doctorDTO.toDoctor());
			}
		}
		log.info("The converted set is: " + doctors);
		return doctors;
	}

}
