package guardians.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


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
		strategy = GenerationType.AUTO
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
