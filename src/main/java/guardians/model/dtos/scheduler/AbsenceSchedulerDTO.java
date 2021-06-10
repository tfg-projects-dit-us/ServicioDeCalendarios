package guardians.model.dtos.scheduler;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import guardians.model.entities.Absence;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * This DTO will represent the information related to an {@link Absence} exposed
 * to the scheduler
 * 
 * @author miggoncan
 */
@Data
@Slf4j
public class AbsenceSchedulerDTO {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // ISO Format
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate start;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // ISO Format
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate end;
	
	public AbsenceSchedulerDTO(Absence absence) {
		log.info("Creating an AbsenceSchedulerDTO from the Absence: " + absence);
		if (absence != null) {
			this.start = absence.getStart();
			log.debug("The start date of the Absence is: " + this.start);
			this.end = absence.getEnd();
			log.debug("The end date of the Absence is : " + this.end);
		}
		log.info("The created AbsenceSchedulerDTO is: " + this);
	}
	
	public AbsenceSchedulerDTO() {
	}

	public Absence toAbsence() {
		log.info("Creating an Absence from this AbsenceSchedulerDTO: " + this);
		Absence absence = new Absence(this.start, this.end);
		log.info("The created Absence is: " + absence);
		return absence;
	}
}
