package guardians.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


import lombok.Data;

/**
 * AllowedShift represents the shifts a {@link Doctor} may have in their
 * unwanted/unavailable/wanted/mandatory shifts
 * 
 * @see ShiftConfiguration
 * @author miggoncan
 */
@Data
@Entity
public class AllowedShift {
	@Id
	@GeneratedValue(
		strategy = GenerationType.IDENTITY
	)
	@NotNull
	private Integer id;

	/**
	 * shift is the meaningful name used to specify an AllowedShift. For example,
	 * they could be days of the week: "Monday", "Tuesday", "Wednesday", ...
	 * 
	 * Although shift is unique, and could be used as the id for the database,
	 * numerical primary keys are preferred over string ones
	 */
	@Column(unique = true, nullable = false)
	@NotBlank
	private String shift;
	
	public AllowedShift(String shift) {
		this.shift = shift;
	}
	
	public AllowedShift() {}
}
