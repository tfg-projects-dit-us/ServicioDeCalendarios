package guardians.model.dtos.scheduler;

import java.util.SortedSet;
import java.util.TreeSet;

import guardians.model.entities.Schedule;
import guardians.model.entities.ScheduleDay;
import guardians.model.entities.Schedule.ScheduleStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO will represent the information related to a {@link Schedule} exposed
 * to the Scheduler
 * 
 * @author miggoncan
 */
@Data
@Slf4j
public class ScheduleSchedulerDTO {
	private Integer month;
	private Integer year;
	private ScheduleStatus status;
	private SortedSet<ScheduleDaySchedulerDTO> days;

	public Schedule toSchedule() {
		log.info("Converting to a Schedule this ScheduleDaySchedulerDTO: " + this);
		Schedule schedule = new Schedule();
		schedule.setMonth(this.month);
		schedule.setYear(this.year);
		schedule.setStatus(this.status);

		SortedSet<ScheduleDay> scheduleDays = new TreeSet<>();
		if (this.days != null) {
			for (ScheduleDaySchedulerDTO scheduleDayDTO : this.days) {
				ScheduleDay scheduleDay = scheduleDayDTO.toScheduleDay();
				scheduleDay.setSchedule(schedule);
				scheduleDays.add(scheduleDay);
			}
		}
		schedule.setDays(scheduleDays);

		log.info("The converted Schedule is: " + schedule);
		return schedule;
	}
}
