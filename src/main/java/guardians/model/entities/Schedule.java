package guardians.model.entities;

import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.SortNatural;
import org.hibernate.validator.constraints.Range;

import guardians.model.entities.primarykeys.CalendarPK;
import guardians.model.validation.annotations.ValidSchedule;
import lombok.Data;

/**
 * The Schedule {@link Entity} represents the scheduled shifts of a specific
 * {@link Calendar}
 * 
 * Note the primary key of this entity is composite, hence the {@link IdClass}
 * annotation. Moreover, this is a weak entity, so it receives its primary key
 * from the {@link Calendar} it is associated to
 * 
 * @see ScheduleDay
 * @author miggoncan
 */
@Data
@Entity
@IdClass(CalendarPK.class)
@ValidSchedule
public class Schedule {
	
	public enum ScheduleStatus {
		NOT_CREATED,
		BEING_GENERATED,
		PENDING_CONFIRMATION,
		CONFIRMED, 
		GENERATION_ERROR
	}
	
	@Id
	@Column(name = "calendar_month")
	@Range(min = 1, max = 12)
	@NotNull
	private Integer month;
	@Id
	@Column(name = "calendar_year")
	@Range(min = 1970)
	@NotNull
	private Integer year;
	@MapsId
	@OneToOne
	private Calendar calendar;

	/**
	 * This represents the status in which this schedule is. For example, the
	 * schedule of this {@link Calendar} could not have been created yet. Or it
	 * could be waiting for approval
	 */
	@Enumerated(EnumType.STRING)
	@NotNull
	private ScheduleStatus status = ScheduleStatus.NOT_CREATED;

	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
	@SortNatural
	private SortedSet<ScheduleDay> days;

	public Schedule(ScheduleStatus status) {
		this.status = status;
	}

	public Schedule() {
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
		if (calendar != null) {
			this.month = calendar.getMonth();
			this.year = calendar.getYear();
			// If not updated yet, this will update the references in days
			this.setDays(this.getDays());
		}
	}
	
	public void setDays(SortedSet<ScheduleDay> days) {
		this.days = days;
		if (days != null) {
			for (ScheduleDay scheduleDay : days) {
				scheduleDay.setSchedule(this);
			}
		}
	}
}
