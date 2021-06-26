package guardians.controllers.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import guardians.controllers.CalendarController;
import guardians.controllers.RootController;
import guardians.controllers.ScheduleController;
import guardians.controllers.exceptions.InvalidEntityException;
import guardians.model.dtos.general.SchedulePublicDTO;
import guardians.model.dtos.general.ScheduleSummaryPublicDTO;
import guardians.model.entities.Schedule.ScheduleStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible of converting a {@link SchedulePublicDTO} into its
 * {@link EntityModel} representation. This is, adding links to the
 * {@link SchedulePublicDTO}
 * 
 * @author miggoncan
 */
@Component
@Slf4j
public class ScheduleAssembler
		implements RepresentationModelAssembler<SchedulePublicDTO, EntityModel<SchedulePublicDTO>> {
	@Value("${api.links.root}")
	private String rootLink;
	@Value("${api.links.calendar}")
	private String calendarLink;
	@Value("${api.links.scheduleStatus}")
	private String scheduleStatusLink;
	@Value("${api.links.confirmSchedule}")
	private String confirmSchedulLink;

	@Override
	public EntityModel<SchedulePublicDTO> toModel(SchedulePublicDTO schedule) {
		log.info("Request to map to model the SchedulePublicDTO: " + schedule);
		YearMonth yearMonth = null;
		try {
			yearMonth = YearMonth.of(schedule.getYear(), schedule.getMonth());
		} catch (DateTimeParseException e) {
			throw new InvalidEntityException("Trying to map to an EntityModel an invalid Schedule. "
					+ "Its month and year are: " + schedule.getMonth() + "/" + schedule.getYear());
		}
		EntityModel<SchedulePublicDTO> entity = new EntityModel<>(schedule, 
				this.getLinks(yearMonth, schedule.getStatus()));
		log.info("The model created is: " + entity);
		return entity;
	}
	
	public EntityModel<ScheduleSummaryPublicDTO> toModel(ScheduleSummaryPublicDTO schedule) {
		log.info("Request to map to model the ScheduleSummaryPublicDTO: " + schedule);
		YearMonth yearMonth =  YearMonth.of(schedule.getYear(), schedule.getMonth());
		EntityModel<ScheduleSummaryPublicDTO> entity = new EntityModel<>(schedule, 
				this.getLinks(yearMonth, schedule.getStatus()));
		log.info("The model created is: " + entity);
		return entity;
	}
	
	private List<Link> getLinks(YearMonth yearMonth, ScheduleStatus status) {
		log.info("Request to get links to schedule of " + yearMonth + " with status " + status);
		List<Link> links = new LinkedList<>();
		links.add(linkTo(methodOn(ScheduleController.class).getScheduleRequest(yearMonth)).withSelfRel());
		links.add(linkTo(methodOn(CalendarController.class).getCalendar(yearMonth)).withRel(calendarLink));

		if (status == ScheduleStatus.PENDING_CONFIRMATION) {
			log.debug("The schedule is in the PENFING_CONFIRMATION status. Adding a link to confirm it");
			links.add(linkTo(methodOn(ScheduleController.class).changeStatus(yearMonth,
					ScheduleStatus.CONFIRMED.toString().toLowerCase())).withRel(confirmSchedulLink));
		} else {
			log.debug("The schedule is not in the PENFING_CONFIRMATION status");
		}
		log.info("The generated links are: " + links);
		return links;
	}

	public CollectionModel<EntityModel<ScheduleSummaryPublicDTO>> toCollectionModelSummary(
			Iterable<? extends ScheduleSummaryPublicDTO> entities) {
		log.info("Request to map to collection model a summary of schedules");
		log.debug("The summaries are: " + entities);
		List<EntityModel<ScheduleSummaryPublicDTO>> schedules = new LinkedList<>();
		for (ScheduleSummaryPublicDTO entity : entities) {
			schedules.add(this.toModel(entity));
		}
		return new CollectionModel<>(schedules, 
				linkTo(methodOn(ScheduleController.class).getSchedules()).withSelfRel(),
				linkTo(methodOn(RootController.class).getRootLinks()).withRel(rootLink));
	}

}
