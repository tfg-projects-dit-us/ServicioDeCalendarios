package guardians.model.dtos.scheduler;

import java.util.SortedSet;
import java.util.TreeSet;

import guardians.model.entities.Calendar;
import guardians.model.entities.DayConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This class represents the information related to a {@link Calendar} that is
 * exposed to the scheduler
 * 
 * @author miggoncan
s */
@Data
@Slf4j
public class CalendarSchedulerDTO {
	private Integer month;
	private Integer year;
	private SortedSet<DayConfigurationSchedulerDTO> dayConfigurations;
	
	public CalendarSchedulerDTO(Calendar calendar) {
		log.info("Creating CalendarSchedulerDTO from Calendar: " + calendar);
		if (calendar == null) {
			log.info("The calendar is null");
		} else {
			this.month = calendar.getMonth();
			log.debug("The calendar's month is: " + this.month);
			this.year = calendar.getYear();
			log.debug("The calendar's year is: " + this.year);
			SortedSet<DayConfigurationSchedulerDTO> dayConfDTOs = new TreeSet<>();
			SortedSet<DayConfiguration> dayConfs = calendar.getDayConfigurations();
			log.debug("The calendar's dayConfs are: " + dayConfs);
			for (DayConfiguration dayConf : dayConfs) {
				log.debug("Mapping to DayConfigurationSchedulerDTO the DayConfiguration: " + dayConf);
				DayConfigurationSchedulerDTO dayConfDTO = new DayConfigurationSchedulerDTO(dayConf);
				log.debug("The mapped DayConfigurationSchedulerDTO is: " + dayConfDTO);
				dayConfDTOs.add(dayConfDTO);
			}
			this.dayConfigurations = dayConfDTOs;
			log.debug("The set of DayConfigurationSchedulerDTO is: " + this.dayConfigurations);
		}
		log.info("The created CalendarSchedulerDTO is: " + this);
	}
	
	public CalendarSchedulerDTO() {
	}
}
