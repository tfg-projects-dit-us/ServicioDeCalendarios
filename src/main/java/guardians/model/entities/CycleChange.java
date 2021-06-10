package guardians.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.hibernate.validator.constraints.Range;

import guardians.model.entities.primarykeys.DayMonthYearPK;
import guardians.model.validation.annotations.ValidCycleChange;
import guardians.model.validation.annotations.ValidDayMonthYear;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The CycleChange {@link Entity} is used to represent that two {@link Doctor}
 * have changed their cycle shift a certain day of a certain
 * {@link Calendar}
 * 
 * Note the primary key used for this {@link Entity} is composite. Hence, the
 * {@link IdClass} annotation is used. Moreover, CycleChange is a weak entity,
 * so it receives its primary key from the corresponding
 * {@link DayConfiguration}
 * 
 * @see DayMonthYearPK
 * 
 * @author miggoncan
 */
@Data
@EqualsAndHashCode(exclude = "dayConfiguration")
@Entity
@IdClass(DayMonthYearPK.class)
@ValidDayMonthYear
@ValidCycleChange
public class CycleChange {
	@Id
	@Range(min = 1, max = 31)
	@Column(name = "day_configuration_day")
	private Integer day;
	@Id
	@Range(min = 1, max = 12)
	@Column(name = "day_configuration_calendar_month")
	private Integer month;
	@Id
	@Range(min = 1970)
	@Column(name = "day_configuration_calendar_year")
	private Integer year;
	@ManyToOne
	@MapsId
	private DayConfiguration dayConfiguration;

	/**
	 * The cycle giver is the {@link Doctor} that gives their cycle shift to
	 * another {@link Doctor}. Hence, the cycle giver will not have a cycle-shift
	 * this {@link CycleChange#day}
	 */
	@ManyToOne
	private Doctor cycleGiver;

	/**
	 * The cycle receiver is the {@link Doctor} that will take the
	 * cycle shift. Hence, although the cycleReceiver would not have had a
	 * cycle-shift this {@link CycleChange#day}, they will have it.
	 */
	@ManyToOne
	private Doctor cycleReceiver;
	
	public CycleChange(Doctor cycleGiver, Doctor cycleReceiver) {
		this.cycleGiver = cycleGiver;
		this.cycleReceiver = cycleReceiver;
	}
	
	public CycleChange() {}

	public void setDayConfiguration(DayConfiguration dayConfiguration) {
		this.dayConfiguration = dayConfiguration;
		if (dayConfiguration != null) {
			this.day = dayConfiguration.getDay();
			this.month = dayConfiguration.getMonth();
			this.year = dayConfiguration.getYear();
		}
	}
	
	@Override
	public String toString() {
		return CycleChange.class.getSimpleName()
				+ "("
					+ "day=" + day + ", "
					+ "cycleGiver=" + cycleGiver + ", "
					+ "cycleReceiver=" + cycleReceiver
				+ ")";
	}
}
