package guardians.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

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
