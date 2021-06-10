package guardians.model.dtos.general;

import org.springframework.hateoas.server.core.Relation;

import guardians.model.entities.Calendar;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO represents a summarized version of a {@link Calendar}
 * @author miggoncan
 */
@Data
@Relation(value = "calendar", collectionRelation = "calendars")
@Slf4j
public class CalendarSummaryPublicDTO {
	private Integer month;
	private Integer year;
	
	public CalendarSummaryPublicDTO() {}
	
	public CalendarSummaryPublicDTO(Calendar calendar) {
		log.info("Creating a CalendarSummaryPublicDTO from the calendar: " + calendar);
		this.month = calendar.getMonth();
		this.year = calendar.getYear();
		log.info("The created summary is: " + this);
	}
}
