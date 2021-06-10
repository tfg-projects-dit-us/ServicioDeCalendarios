package guardians.model.entities;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import guardians.model.repositories.DoctorRepository;

@DataJpaTest
public class DoctorTest {
	@Autowired
	private DoctorRepository doctorRepository;
	
	private EntityTester<Doctor> entityTester;
	
	/**
	 * This method creates one valid Doctor instance. 
	 * 
	 * Calls to this method will always return a Doctor with the same 
	 * attribute values
	 * 
	 * @return a new Doctor object that has not been persisted
	 */
	public static Doctor createValidDoctor() {
		return new Doctor("Bilbo", "Baggins", "bilbo@mordor.com", LocalDate.of(2020, 6, 26));
	}
	
	/**
	 * This method creates a list of valid Doctor instances. 
	 * 
	 * Calls to this method will always return the same Doctors
	 * 
	 * @return the new Doctor objects that have not been persisted
	 */
	public static List<Doctor> createValidDoctors() {
		List<Doctor> doctors = new ArrayList<>(6);
		doctors.add(new Doctor("Frodo", "Baggins", "frodo@mordor.com", LocalDate.of(2020, 6, 19)));
		doctors.add(new Doctor("Gollumn", "aka Smeagol", "gollumn@mordor.com", LocalDate.of(2020, 6, 20)));
		doctors.add(new Doctor("Gandalf", "The Gray", "gandalf@lonelymountain.com", LocalDate.of(2020, 6, 21)));
		doctors.add(new Doctor("Samwise", "Gamgee", "samg@mordor.com", LocalDate.of(2020, 6, 22)));
		doctors.add(new Doctor("Galadriel", "the Lady", "lady@lothlorien.com", LocalDate.of(2020, 6, 23)));
		doctors.add(new Doctor("Gimli", "son of Gloin", "gimli@glittetingcaves.com", LocalDate.of(2020, 6, 24)));
		return doctors;
	}
	
	public DoctorTest() {
		this.entityTester = new EntityTester<>(Doctor.class);
	}

	///////////////////////////////////////
	//
	// Tests for valid values
	//
	///////////////////////////////////////
	
	@Test
	void validFirstName() {
		this.entityTester.assertValidValue("firstName", "Aragorn son of Aragorn");
	}
	
	@Test
	void validLastNames() {
		this.entityTester.assertValidValue("lastNames", "the heir of Isildur Elendil's son of Gondor");
	}
	
	@Test
	void validEmail() {
		this.entityTester.assertValidValue("email", "elessar@reunitedkingdom.com");
	}
	
	@Test
	void absenceCanBeNull() {
		this.entityTester.assertValidValue("absence", null);
	}
	
	@Test
	void createAndSaveValidDoctor() {
		Doctor doctor = createValidDoctor();
		
		this.entityTester.assertValidEntity(doctor);
		
		doctor = doctorRepository.save(doctor);
		assertNotEquals(0, doctor.getId());
	}
	
	///////////////////////////////////////
	//
	// Tests for invalid values
	//
	///////////////////////////////////////

	@Test
	void firstNameCannotBeBlank() {
		this.entityTester.assertAttributeCannotBeBlank("firstName");
	}
	
	@Test
	void lastNamesCannotBeBlank() {
		this.entityTester.assertAttributeCannotBeBlank("lastNames");
	}
	
	@Test
	void emailCannotBeBlank() {
		this.entityTester.assertAttributeCannotBeBlank("email");
	}
	
	@Test
	void emailHasToHaveADomain() {
		this.entityTester.assertInvalidValue("email", "aragorn", "must be a well-formed email address");
	}
	
	@Test
	void emailHasToHaveAnAtSymbol() {
		this.entityTester.assertInvalidValue("email", "aragorn.com", "must be a well-formed email address");
	}
	

	@Test
	void emailCannotBeOnlyADomain() {
		this.entityTester.assertInvalidValue("email", "@mordor.com", "must be a well-formed email address");
	}
}
