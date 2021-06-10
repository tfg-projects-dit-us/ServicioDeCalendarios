package guardians.model.entities.primarykeys;

import java.io.Serializable;

import javax.persistence.Entity;

import guardians.model.entities.Calendar;
import lombok.Data;

/**
 * This class represents the primary key for the {@link Entity} {@link Calendar}
 * 
 * A {@link Calendar} is uniquely identified with a month and a year
 * 
 * @author miggoncan
 */
@Data
public class CalendarPK implements Serializable {
	private static final long serialVersionUID = 66688158711309197L;

	private Integer month;
	private Integer year;

	public CalendarPK(Integer month, Integer year) {
		this.month = month;
		this.year = year;
	}

	public CalendarPK() {
	}
}