package guardians.controllers;

import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * This controller will receive GET requests to the root URI of this application
 * and respond with links to the existing resources
 * 
 * @author miggoncan
 */
@RestController
@Slf4j
@RequestMapping("/")
public class RootController {
	
	@Value("${api.links.doctors}")
	private String doctorsLink;
	@Value("${api.links.doctor}")
	private String doctorLink;
	@Value("${api.links.newDoctor}")
	private String newDoctorLink;
	@Value("${api.links.shiftconfs}")
	private String shiftConfsLink;
	@Value("${api.links.shiftconf}")
	private String shiftConfLink;
	@Value("${api.links.calendars}")
	private String calendarsLink;
	@Value("${api.links.calendar}")
	private String calendarLink;
	@Value("${api.links.schedules}")
	private String schedulesLink;
	@Value("${api.links.schedule}")
	private String scheduleLink;
	@Value("${api.links.allowedshifts}")
	private String allowedShiftsLink;

	/**
	 * Get requests to the base URL will return links to the main resources of the
	 * application
	 * 
	 * @return A CollectionModel containing only links
	 */
	@GetMapping("/")
	public CollectionModel<Object> getRootLinks() {
		log.info("Request received: get root");
		return new CollectionModel<Object>(Collections.emptyList(),
				linkTo(methodOn(RootController.class).getRootLinks()).withSelfRel(),
				linkTo(methodOn(DoctorController.class).getDoctors(null)).withRel(doctorsLink),
				linkTo(methodOn(DoctorController.class).getDoctor(null)).withRel(doctorLink),
				linkTo(methodOn(DoctorController.class).newDoctor(null, null)).withRel(newDoctorLink),
				linkTo(methodOn(ShiftConfigurationController.class).getShitfConfigurations()).withRel(shiftConfsLink),
				linkTo(methodOn(ShiftConfigurationController.class).getShitfConfiguration(null)).withRel(shiftConfLink),
				linkTo(methodOn(CalendarController.class).getCalendars()).withRel(calendarsLink),
				linkTo(methodOn(CalendarController.class).getCalendar(null)).withRel(calendarLink),
				linkTo(methodOn(ScheduleController.class).getSchedules()).withRel(schedulesLink),
				linkTo(methodOn(ScheduleController.class).getScheduleRequest(null)).withRel(scheduleLink),
				linkTo(methodOn((AllowedShiftsController.class)).getAllowedShifts()).withRel(allowedShiftsLink));
	}

}
