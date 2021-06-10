package guardians.model.dtos.general;

import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.hateoas.server.core.Relation;

import guardians.model.entities.Calendar;
import guardians.model.entities.DayConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This class represents the information related to a {@link Calendar} that is
 * exposed through the REST interface
 * 
 * @author miggoncan
 */
@Data
@Relation(value = "calendar", collectionRelation = "calendars")
@Slf4j
public class CalendarPublicDTO {
	private Integer month;
	private Integer year;
	// dayConfigurations are not mapped correctly
	private SortedSet<DayConfigurationPublicDTO> dayConfigurations;

	public CalendarPublicDTO(Calendar calendar) {
		log.info("Creating CalendarPublicDTO from Calendar: " + calendar);
		if (calendar == null) {
			log.info("The calendar is null");
		} else {
			this.month = calendar.getMonth();
			log.debug("The calendar's month is: " + this.month);
			this.year = calendar.getYear();
			log.debug("The calendar's year is: " + this.year);
			SortedSet<DayConfigurationPublicDTO> dayConfDTOs = new TreeSet<>();
			SortedSet<DayConfiguration> dayConfs = calendar.getDayConfigurations();
			log.debug("The calendar's dayConfs are: " + dayConfs);
			for (DayConfiguration dayConf : dayConfs) {
				log.debug("Mapping to DayConfigurationPublicDTO the DayConfiguration: " + dayConf);
				DayConfigurationPublicDTO dayConfDTO = new DayConfigurationPublicDTO(dayConf);
				log.debug("The mapped DayConfigurationPublicDTO is: " + dayConfDTO);
				dayConfDTOs.add(dayConfDTO);
			}
			this.dayConfigurations = dayConfDTOs;
			log.debug("The set of DayConfigurationPublicDTO is: " + this.dayConfigurations);
		}
		log.info("The created CalendarPublicDTO is: " + this);
	}

	public CalendarPublicDTO() {
	}

	public Calendar toCalendar() {
		log.info("Mapping this CalendarPublicDTO to a Calendar");
		log.debug("This CalendarPublicDTO is: " + this);
		Calendar calendar = new Calendar();
		calendar.setMonth(this.month);
		log.debug("The calendar's month is: " + calendar.getMonth());
		calendar.setYear(this.year);
		log.debug("The calendar's year is: " + calendar.getYear());
		SortedSet<DayConfiguration> dayConfigs = new TreeSet<>();
		log.debug("Mapping DayConfigurationPublicDTOs to DayConfigurations: " + this.dayConfigurations);
		if (this.dayConfigurations != null) {
			for (DayConfigurationPublicDTO dayConfigDTO : this.dayConfigurations) {
				log.debug("Converting to DayConfiguration the DayConfigurationPublicDTO: " + dayConfigDTO);
				DayConfiguration dayConf = dayConfigDTO.toDayConfiguration();
				dayConf.setCalendar(calendar);
				log.debug("The converted DayConfiguration is: " + dayConf);
				dayConfigs.add(dayConf);
			}
		}
		log.debug("The resulting DayConfigurations are: " + dayConfigs);
		calendar.setDayConfigurations(dayConfigs);
		log.info("The resulting Calendar is: " + calendar);
		return calendar;
	}
}
