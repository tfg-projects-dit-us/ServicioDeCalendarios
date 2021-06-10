package guardians.model.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import guardians.model.validation.annotations.ValidAbsenceDates;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This {@link Entity} represents the Absence of a {@link Doctor} during a
 * certain period An absence may occur, for example, if the {@link Doctor} is
 * sick or in their holidays
 * 
 * Absence is a weak entity. Hence, it receives its primary key from the
 * corresponding {@link Doctor}
 * 
 * There are certain constraints an Absence has to meet to be considered valid.
 * See {@link ValidAbsenceDates}
 * 
 * @author miggoncan
 */
@Data
@EqualsAndHashCode(exclude = "doctor")
@Entity
@ValidAbsenceDates
public class Absence {
	/**
	 * doctor_id is the primary key of the {@link Doctor} with this Absence
	 */
	@Id
	private Long doctorId;
	@MapsId
	@OneToOne(optional = false)
	private Doctor doctor;

	/**
	 * start is the day in which the Absence will begin
	 */
	@Column(nullable = false)
	@NotNull
	private LocalDate start;

	/**
	 * end is the day in which the Absence will finish
	 */
	@Column(nullable = false)
	@NotNull
	private LocalDate end;

	public Absence(LocalDate start, LocalDate end) {
		this.start = start;
		this.end = end;
	}

	public Absence() {
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
		if (doctor != null) {
			this.doctorId = doctor.getId();
		} else {
			this.doctorId = null;
		}
	}

	// The toString method from @Data is not used as it can create an infinite loop
	// between Doctor#toString and this method
	@Override
	public String toString() {
		return Absence.class.getSimpleName() + "(" + "doctorId=" + this.doctorId + ", " + "start=" + this.start + ", "
				+ "end=" + this.end + ")";
	}
}
