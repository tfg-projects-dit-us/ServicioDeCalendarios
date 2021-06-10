package guardians.model.dtos.general;

import guardians.model.entities.Schedule;
import guardians.model.entities.Schedule.ScheduleStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO represents a summarized version of a {@link Schedule}
 * 
 * @author miggoncan
 */
@Data
@Slf4j
public class ScheduleSummaryPublicDTO {
	private Integer month;
	private Integer year;
	private ScheduleStatus status;

	public ScheduleSummaryPublicDTO() {
	}

	public ScheduleSummaryPublicDTO(Schedule schedule) {
		log.info("Creating a SchedulePublicDTO from the schedule: " + schedule);
		this.month = schedule.getMonth();
		this.year = schedule.getYear();
		this.status = schedule.getStatus();
	}
}
