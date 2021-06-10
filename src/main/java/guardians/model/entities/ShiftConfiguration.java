package guardians.model.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import guardians.model.validation.annotations.ValidShiftConfiguration;
import guardians.model.validation.annotations.ValidShiftPreferences;
import lombok.Data;

/**
 * The ShiftConfiguration entities represent the configuration that will be
 * applied whenever a shifts are scheduled.
 * 
 * Note ShiftConfiguration is a weak entity. Hence, the id of the {@link Doctor}
 * associated to this ShiftConfiguration is used as the primary key
 * 
 * There are some constraints that every ShiftConfiguration has to meet, besides
 * the ones declared within the attributes. See {@link ValidShiftConfiguration}
 * 
 * As {@link #unwantedShifts}, {@link #unavailableShifts}, {@link #wantedShifts}
 * and {@link #mandatoryShifts} will be frequently used in the documentation,
 * they will be referred to as shiftPreferences
 * 
 * @author miggoncan
 */
@Data
@Entity
@ValidShiftConfiguration
@ValidShiftPreferences
public class ShiftConfiguration {
	@Id
	@NotNull
	private Long doctorId;
	@MapsId
	@OneToOne
	private Doctor doctor;

	/**
	 * maxShifts represents the maximum number of shifts the associated
	 * {@link Doctor} is allowed to have during a month
	 * 
	 * This number does not take into account the number of cycle-shifts the
	 * associated {@link Doctor} has to have
	 */
	@Column(nullable = false)
	@PositiveOrZero
	@NotNull
	private Integer maxShifts;

	/**
	 * minShifts represents the minimum number of shifts the associated
	 * {@link Doctor} has to have within a month
	 * 
	 * This number does not take into account the number of cycle-shifts the
	 * associated {@link Doctor} has to have
	 */
	@Column(nullable = false)
	@PositiveOrZero
	@NotNull
	private Integer minShifts;

	/**
	 * This Integer will be 0 if this {@link Doctor} does no have consultations, or
	 * a number greater than zero if the {@link Doctor} has to have a certain number
	 * of consultations per month
	 */
	@Column(nullable = false)
	@NotNull
	@PositiveOrZero
	private Integer numConsultations;

	/**
	 * This Boolean will be false if this {@link Doctor} only does non-cycle-shifts
	 */
	@Column(nullable = false)
	@NotNull
	private Boolean doesCycleShifts;

	/**
	 * If this Boolean is set to true, the fields minShifts and maxShifts will be
	 * ignored. In this case, this {@link Doctor} will only have non-cycle-shifts
	 * the same days they have cycle-shifts
	 */
	@Column(nullable = false)
	@NotNull
	private Boolean hasShiftsOnlyWhenCycleShifts;

	/**
	 * unwantedShifts indicates the shifts the associated {@link Doctor} would
	 * rather not have assigned
	 */
	@ManyToMany
	private Set<AllowedShift> unwantedShifts;

	/**
	 * unavailableShifts indicates the shifts the associated {@link Doctor} cannot
	 * have, under any circumstances
	 */
	@ManyToMany
	private Set<AllowedShift> unavailableShifts;

	/**
	 * wantedShifts indicates the shifts the associated {@link Doctor} would like to
	 * have
	 */
	@ManyToMany
	private Set<AllowedShift> wantedShifts;

	/**
	 * mandatoryShifts indicates the shifts the associated {@link Doctor} HAS to
	 * have
	 */
	@ManyToMany
	private Set<AllowedShift> mandatoryShifts;
	
	@ManyToMany
	private Set<AllowedShift> wantedConsultations;

	public ShiftConfiguration(Integer minShifts, Integer maxShifts, Integer numConsultations, Boolean doesCycleShifts, Boolean hasShiftsOnlyWhenCycleShifts) {
		this.minShifts = minShifts;
		this.maxShifts = maxShifts;
		this.numConsultations = numConsultations;
		this.doesCycleShifts = doesCycleShifts;
		this.hasShiftsOnlyWhenCycleShifts = hasShiftsOnlyWhenCycleShifts;
	}

	public ShiftConfiguration() {
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
		if (doctor != null) {
			this.doctorId = doctor.getId();
		} else {
			this.doctorId = null;
		}
	}
}
