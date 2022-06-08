package guardians.model.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * This {@link Entity} represents the information of a Doctor that will be
 * stored in the database
 *  
 * {@link Doctor}s have some periodic shifts. This is, if some {@link Doctor}s
 * have a shift today, after a certain number of days, they will have another
 * one. This kind of shifts will be referred to as "cycle-shift", and should not
 * be confused with regular shifts. A "regular shift", or "shift" in short,
 * refers to the shifts that will vary from month to month and that do not occur
 * periodically. This kind of shifts are scheduled from {@link Doctor#startDate}
 * 
 * @author miggoncan
 */
@Data
@Entity
public class Doctor {

	public enum DoctorStatus {
		AVAILABLE,
		DELETED
	}
	@Id
	@GeneratedValue(
		strategy = GenerationType.IDENTITY
	)
	@Column(name = "id")
	private Long id;
	@Column(name = "TelegramID")
	private String telegramId;
	@Column(nullable = false)
	@NotBlank
	private String firstName;

	@Column(nullable = false)
	@NotBlank
	private String lastNames;
	
	@Email
	@NotBlank
	@Column(unique = true, nullable = false)
	private String email;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private DoctorStatus status = DoctorStatus.AVAILABLE;

	@OneToOne(optional = true, mappedBy = "doctor", cascade = {CascadeType.ALL})
	private Absence absence;
	
	// Start date will be the date this doctor's reference date to calculate their
	// shift cycle
	@Column(nullable = false)
	private LocalDate startDate;
	
	@ManyToMany( fetch = FetchType.EAGER)
    @JoinTable(
            name = "doctor_roles",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
            )
    private Set<Rol> roles = new HashSet<>();
	
	public Doctor(String firsName, String lastNames, String email, LocalDate startDate) {
		this.firstName = firsName;
		this.lastNames = lastNames;
		this.email = email;
		this.startDate = startDate;
	}

	public Doctor() {
	}

	// The toString method from @Data is not used as it can create an infinite loop
	// between Absence#toString and this method
	@Override
	public String toString() {
		return Doctor.class.getSimpleName() 
				+ "("
					+ "id=" + this.id + ", "
					+ "firstName=" + this.firstName + ", "
					+ "lastNames=" + this.lastNames + ", "
					+ "email=" + this.email + ", "
					+ "status=" + this.status + ", "
					+ "absence=" + this.absence + ", "
					+ "startDate=" + this.startDate
				+ ")";
	}
	
	public void setAbsence(Absence absence) {
		this.absence = absence;
		if (absence != null) {
			this.absence.setDoctor(this);
		}
	}
	
	public void addRole(Rol role) {
        this.roles.add(role);
}
	
	public void setTelegramId(String telID) {
		this.telegramId = telID;
	}
	public String getTelegramId() {
		return this.telegramId;
	}
	 public Rol getRol(String nombre) {
		 for (Rol element :roles) 
		 {
			 if (element.getNombreRol().equals(nombre))
				 return element;
				 
		 }
		return null;
		 
	 }
}
