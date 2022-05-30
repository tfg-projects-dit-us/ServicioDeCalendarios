package guardians.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
public class Rol {

	/**
	 * doctor_id is the primary key of the {@link Doctor} with this Rol
	 */
	@Id
	private Long doctorId;
	
	

	@Column(nullable = false)
	@NotNull
	private String rol;

	
	public Rol() {
		
	}
	
	@Override
	public String toString() {
		return Rol.class.getSimpleName() + "(" + "doctorId=" + this.doctorId + ", " + "manafer=" + this.rol+ ")";
	}
	
}
