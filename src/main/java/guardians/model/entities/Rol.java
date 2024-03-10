package guardians.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;
/**
 * 
 * @author carcohcal
 *
 */
@Data
@Entity
public class Rol {
	
	
	@Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  @Column(nullable = false)
	private Long id;
	
	private String nombreRol;

	
	public Rol() {
		
	}
	
	public Rol(String rol) {
		
		this.nombreRol=rol;
		
	}
	
	@Override
	public String toString() {
		return Rol.class.getSimpleName() + "("  + "Nombre rol=" + this.nombreRol+ ")";
	}
	
	public String getNombreRol() {
		return this.nombreRol;
	}
	
	public void setNombreRol(String rol)
	{
		this.nombreRol=rol;
	}
	
	
	
}
