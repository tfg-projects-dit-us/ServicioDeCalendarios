package guardians.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.caldav4j.exceptions.CalDAV4JException;

import guardians.controllers.assemblers.ScheduleAssembler;
import guardians.controllers.exceptions.CalendarNotFoundException;
import guardians.controllers.exceptions.InvalidScheduleException;
import guardians.controllers.exceptions.InvalidScheduleStatusException;
import guardians.controllers.exceptions.InvalidScheduleStatusTransitionException;
import guardians.controllers.exceptions.ScheduleAlreadyExistsException;
import guardians.controllers.exceptions.ScheduleNotFoundException;

import guardians.model.dtos.general.SchedulePublicDTO;
import guardians.model.dtos.general.ScheduleSummaryPublicDTO;
import guardians.model.entities.Calendar;
import guardians.model.entities.Schedule;
import guardians.model.entities.Schedule.ScheduleStatus;
import guardians.model.entities.ScheduleDay;
import guardians.model.entities.primarykeys.CalendarPK;
import guardians.model.repositories.CalendarRepository;
import guardians.model.repositories.ScheduleRepository;
import guardians.services.CalDav;
import guardians.services.calendarioGeneral;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;

// TODO Create integration test for ScheduleController

/**
 * This class will handle requests related to {@link Schedule}s and
 * {@link ScheduleStatus}
 * 
 * @author miggoncan
 */
@RestController
@RequestMapping("/calendars/schedules")
@Slf4j
public class ScheduleController {
	
	@Autowired
	private calendarioGeneral calendarService;
	@Autowired
	private CalDav servidorCalendario;
	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private CalendarRepository calendarRepository;

	@Autowired
	private ScheduleAssembler scheduleAssembler;
	@Autowired
	private Validator validator;
	@Autowired
	SchedulerHandler schedulerHandler;
	
	/**
	 * This method will return a {@link Schedule} for the given {@link YearMonth} if
	 * the corresponding {@link Calendar} exists
	 * 
	 * @param yearMonth The year and month for which the {@link Schedule} is to be
	 *                  found
	 * @return The found {@link Schedule}
	 * @throws ScheduleNotFoundException if the corresponding {@link Calendar} is
	 *                                   not found
	 * @see ScheduleNotFoundException
	 */
	private Schedule getSchedule(YearMonth yearMonth) {
		log.info("Request to find the calendar for: " + yearMonth);
		CalendarPK pk = new CalendarPK(yearMonth.getMonthValue(), yearMonth.getYear());

		Optional<Calendar> calendar = calendarRepository.findById(pk);

		if (!calendar.isPresent()) {
			log.info("The calendar of the given month was not found. Throwing ScheduleNotFoundException");
			throw new ScheduleNotFoundException(yearMonth.getMonthValue(), yearMonth.getYear());
		}

		Schedule schedule;
		Optional<Schedule> optSchedule = scheduleRepository.findById(pk);
		if (optSchedule.isPresent()) {
			schedule = optSchedule.get();
			log.info("The schedule has been found.");
			log.debug("The schedule found is: " + schedule);
		} else {
			log.info("The schedule has not already been created. Returning a new NOT_CREATED schedule");
			schedule = new Schedule(ScheduleStatus.NOT_CREATED);
			schedule.setYear(yearMonth.getYear());
			schedule.setMonth(yearMonth.getMonthValue());
		}

		schedule.setCalendar(calendar.get());

		return schedule;
	}
	
	/**
	 * This method will handle requests for the whole {@link Schedule} list
	 * 
	 * @return The summarized schedules found in the database. See
	 *         {@link ScheduleSummaryPublicDTO}
	 */
	@GetMapping("")
	public CollectionModel<EntityModel<ScheduleSummaryPublicDTO>> getSchedules() {
		log.info("Request received: returning all available schedules");
		List<Schedule> schedules = scheduleRepository.findAll();
		log.debug("Found schedules are: " + schedules);
		List<ScheduleSummaryPublicDTO> schedulesDTO = schedules.stream()
				.map(schedule -> new ScheduleSummaryPublicDTO(schedule))
				.collect(Collectors.toCollection(() -> new LinkedList<>()));
		log.debug("Schedules mapped to ScheduleSummaryPublicDTOs are: " + schedulesDTO);
		return scheduleAssembler.toCollectionModelSummary(schedulesDTO);
	}

	/**
	 * This method handles GET requests of a specific {@link Schedule}
	 * 
	 * @param yearMonth The year and month of this schedule. E.g. 2020-06
	 * @return The found {@link Schedule}
	 * @throws ScheduleNotFoundException if the {@link Schedule} has not been
	 *                                   generated yet
	 */
	@GetMapping("/{yearMonth}")
	public EntityModel<SchedulePublicDTO> getScheduleRequest(@PathVariable YearMonth yearMonth) {
		log.info("Request received: get schedule of " + yearMonth);
		Schedule schedule = this.getSchedule(yearMonth);
		return scheduleAssembler.toModel(new SchedulePublicDTO(schedule));
	}

	/**
	 * This class handles POST requests used to generate a {@link Schedule}
	 * 
	 * @param yearMonth The year and month for which the {@link Schedule} should be
	 *                  generated
	 * @throws CalendarNotFoundException      if the {@link Calendar} of yearMonth
	 *                                        does not exist
	 * @throws ScheduleAlreadyExistsException if the {@link Schedule} of this
	 *                                        yearMonth has already being generated
	 */
	@PostMapping("/{yearMonth}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void generateSchedule(@PathVariable YearMonth yearMonth) {
		log.info("Request received to generate schedule for: " + yearMonth);
		CalendarPK pk = new CalendarPK(yearMonth.getMonthValue(), yearMonth.getYear());

		Optional<Calendar> calendar = calendarRepository.findById(pk);
		if (!calendar.isPresent()) {
			log.info("Trying to generate a schedule for a non existing calendar. Throwing CalendarNotFoundException");
			throw new CalendarNotFoundException(yearMonth.getMonthValue(), yearMonth.getYear());
		}

		if (scheduleRepository.findById(pk).isPresent()) {
			log.info("The schedule is already generated. Throwing ScheduleAlreadyExistsException");
			throw new ScheduleAlreadyExistsException(yearMonth.getMonthValue(), yearMonth.getYear());
		}

		log.info("Persisting a schedule with status " + ScheduleStatus.BEING_GENERATED);
		Schedule schedule = new Schedule(ScheduleStatus.BEING_GENERATED);
		schedule.setCalendar(calendar.get());
		scheduleRepository.save(schedule);

		// This will be run in a separate thread, so the call is non blocking
		schedulerHandler.startScheduleGeneration(calendar.get());
	}

	/**
	 * This method will handle requests to update a {@link Schedule}
	 * 
	 * @param yearMonth   the year and month for which the {@link Schedule} should
	 *                    be updated
	 * @param scheduleDTO The new value to use for the {@link Schedule}
	 * @return The persisted {@link Schedule}
	 * @throws ScheduleNotFoundException
	 * @throws ConstraintViolationException if any of the {@link ScheduleDay}s is
	 *                                      not valid
	 * @throws DateTimeException            if any of the given dates is not valid
	 */
	@PutMapping("/{yearMonth}")
	public EntityModel<SchedulePublicDTO> updateSchedule(@PathVariable YearMonth yearMonth,
			@RequestBody SchedulePublicDTO scheduleDTO) {
		log.info("Request received: update the schedule of " + yearMonth + " with " + scheduleDTO);

		Schedule schedule = scheduleDTO.toSchedule();
		Set<ConstraintViolation<Schedule>> scheduleViolations = validator.validate(schedule);
		if (!scheduleViolations.isEmpty()) {
			log.info("The given schedule is not valid. Throwing InvalidScheduleException");
			throw new InvalidScheduleException(scheduleViolations);
		}

		// This will throw a ScheduleNotFoundException if the there is no Calendar for
		// the given yearMonth
		Schedule currSchedule = this.getSchedule(yearMonth);

		Set<ConstraintViolation<ScheduleDay>> violations;
		for (ScheduleDay day : schedule.getDays()) {
			violations = validator.validate(day);
			if (violations.size() != 0) {
				log.info("One of the schedule day is not valid: " + violations.toString()
						+ ". Throwing ConstraintViolationException");
				throw new ConstraintViolationException(violations);
			}
		}

		Schedule savedSchedule = null;
		if (currSchedule.getStatus() == ScheduleStatus.CONFIRMED) {
			log.info("The current schedule has already been CONFIRMED, so attemting to update is not allowed. "
					+ "Throwing InvalidScheduleStatusTransitionException");
			throw new InvalidScheduleStatusTransitionException(ScheduleStatus.CONFIRMED, schedule.getStatus());
		} else if (currSchedule.getStatus() == ScheduleStatus.BEING_GENERATED) {
			log.info("The current schedule is BEING_GENERATED, so attemting to update is not allowed. "
					+ "Throwing InvalidScheduleStatusTransitionException");
			throw new InvalidScheduleStatusTransitionException(ScheduleStatus.BEING_GENERATED, schedule.getStatus());
		} else if (schedule.getStatus() != ScheduleStatus.PENDING_CONFIRMATION) {
			log.info(
					"The new schedule is not in the state PENDING_CONFIRMATION, so attemting to update is not allowed. "
							+ "Throwing InvalidScheduleStatusTransitionException");
			throw new InvalidScheduleStatusTransitionException(currSchedule.getStatus(), schedule.getStatus());
		} else {
			schedule.setMonth(yearMonth.getMonthValue());
			schedule.setYear(yearMonth.getYear());
			log.info("Attempting to persist the schedule");
			savedSchedule = scheduleRepository.save(schedule);
			log.info("The persisted schedule is: " + savedSchedule);
		}

		return scheduleAssembler.toModel(new SchedulePublicDTO(savedSchedule));
	}

	/**
	 * This method will handle requests to change the {@link ScheduleStatus} of a
	 * {@link Schedule}
	 * 
	 * Note not all transitions are allowed. For example, if a {@link Schedule} has
	 * already being {@link ScheduleStatus#CONFIRMED}, it cannot be change back to
	 * {@link ScheduleStatus#NOT_CREATED}
	 * 
	 * @param yearMonth the year and month of the {@link Schedule} whose status is
	 *                  to be updated
	 * @param status    The new status of the {@link Schedule}
	 * @return The persisted {@link Schedule} with its new status
	 * @throws ScheduleNotFoundException      if the schedule does not already exist
	 * @throws InvalidScheduleStatusException if the provided status is not valid,
	 *                                        or the {@link Schedule} cannot be
	 *                                        change from its current status to the
	 *                                        given one
	 */
	@PutMapping("/{yearMonth}/{status}")
	public EntityModel<SchedulePublicDTO> changeStatus(@PathVariable YearMonth yearMonth, @PathVariable String status) {
		log.info("Request received: change status of the schedule of " + yearMonth + " to " + status);

		// This can throw a ScheduleNotFoundException
		Schedule schedule = this.getSchedule(yearMonth);

		ScheduleStatus newStatus;
		try {
			newStatus = Enum.valueOf(ScheduleStatus.class, status.toUpperCase());
		} catch (IllegalArgumentException e) {
			log.info("The given status is invalid. Throwing InvalidScheduleStatusException");
			throw new InvalidScheduleStatusException(status);
		}

		// Currently, the only allowed transition is from PENDING_CONFIRMATION to
		// CONFIRMED
		ScheduleStatus currStatus = schedule.getStatus();
		if (currStatus != ScheduleStatus.PENDING_CONFIRMATION || newStatus != ScheduleStatus.CONFIRMED) {
			log.info("Cannot transition from status " + currStatus + " to " + newStatus
					+ ". Throwing InvalidScheduleStatusTransitionException");
			throw new InvalidScheduleStatusTransitionException(currStatus, newStatus);
		}

		log.info("The requested state transition is valid. Attemting to persist the schedule with its new status");
		schedule.setStatus(newStatus);
		Schedule savedSchedule = scheduleRepository.save(schedule);
		/**
		 * @author Carcohcal
		 * */
		calendarService.init(schedule);
		try {
			calendarService.creaCalendario();
		} catch (IOException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CalDAV4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		log.info("The persited schedule is: " + savedSchedule);

		return scheduleAssembler.toModel(new SchedulePublicDTO(savedSchedule));
	}
	
	/**
	 * This method handles GET requests of a specific {@link Schedule}
	 * @author carcohcal
	 * @param yearMonth The year and month of this schedule. E.g. 2020-06
	 * @return The found {@link Schedule}
	 * @throws ScheduleNotFoundException if the {@link Schedule} has not been
	 *                                   generated yet
	 */
	@GetMapping("/individual/{yearMonth}")
	public net.fortuna.ical4j.model.Calendar getCalendario(@PathVariable YearMonth yearMonth,@RequestBody String email) {
		log.info("Request received: get schedule of " + yearMonth);
		net.fortuna.ical4j.model.Calendar schedule = servidorCalendario.recuperarCalendario(yearMonth,email);
		return schedule;
	}

	
	/**
	 * This method will handle requests to DELETE a specific {@link Schedule}
	 * 
	 * Note the {@link Schedule} cannot be already {@link ScheduleStatus#CONFIRMED}
	 * 
	 * This method is useful to regenerate a {@link Schedule}.
	 * 
	 * @param yearMonth the year and month for which the {@link Schedule} should be
	 *                  deleted
	 * @throws ScheduleNotFoundException
	 */
	@DeleteMapping("/{yearMonth}")
	public ResponseEntity<?> deleteSchedule(@PathVariable YearMonth yearMonth) {
		log.info("Request received: delete schedule for " + yearMonth);

		// Note this can throw a ScheduleNotFoundException
		Schedule schedule = this.getSchedule(yearMonth);

		ResponseEntity<?> responseEntity;

		if (schedule.getStatus() != ScheduleStatus.NOT_CREATED) {
			if (schedule.getStatus() == ScheduleStatus.CONFIRMED) {
				log.info("Attemting to delete a CONFIRMED schedule. The operation is forbidden");
				responseEntity = ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			} else {
				log.info("The requested schedule has already been created. Attemting to delete it");
				scheduleRepository.deleteById(new CalendarPK(schedule.getMonth(), schedule.getYear()));
				responseEntity = ResponseEntity.noContent().build();
			}
		} else {
			log.info("The requested schedule has not yet been created");
			responseEntity = ResponseEntity.noContent().build();
		}

		return responseEntity;
	}
}
