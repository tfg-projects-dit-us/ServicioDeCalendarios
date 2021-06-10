package guardians.model.entities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import guardians.model.repositories.AllowedShiftRepository;

@DataJpaTest
public class AllowedShiftTest {
	@Autowired
	private AllowedShiftRepository allowedShiftRepository;
	
	private EntityTester<AllowedShift> entityTester;
	
	public AllowedShiftTest() {
		entityTester = new EntityTester<>(AllowedShift.class);
	}
	
	/**
	 * This method creates a Set of valid AllowedShift instances,
	 * all different from each other. 
	 * 
	 * Calls to this method will always return the same Set (the instances 
	 * will be new, but the attributes of the AllowedShifts will be the same)
	 * 
	 * The Set will have 4 or more AllowedShifts
	 * 
	 * @return The valid allowed shifts, but not persisted
	 */
	static public Set<AllowedShift> createValidAllowedShifts() {
		Set<AllowedShift> allowedShifts = new HashSet<>();
		allowedShifts.add(new AllowedShift("Monday"));
		allowedShifts.add(new AllowedShift("Wednesday"));
		allowedShifts.add(new AllowedShift("Tuesday"));
		allowedShifts.add(new AllowedShift("Thursday"));
		return allowedShifts;
	}
	
	///////////////////////////////////////
	//
	// Tests for valid values
	//
	///////////////////////////////////////
	
	@Test
	void validShift() {
		this.entityTester.assertValidValue("shift", "Monday");
	}
	
	@Test 
	void createAndSaveValidAllowedShift() {
		AllowedShift allowedShift = new AllowedShift("Tuesday");
		
		allowedShift = allowedShiftRepository.save(allowedShift);
		assertNotEquals(0, allowedShift.getId());
	}
	
	@Test
	void compareAllowedShifts() {
		AllowedShift shift1 = new AllowedShift("Monday");
		AllowedShift shift2 = new AllowedShift("Monday");
		AllowedShift shift3 = new AllowedShift("Tuesday");
		assertTrue(shift1.equals(shift2));
		assertFalse(shift1.equals(shift3));
	}
	
	@Test
	void compareSavedAllowedShifts() {
		AllowedShift shift1 = allowedShiftRepository.save(new AllowedShift("Monday"));
		AllowedShift shift2 = allowedShiftRepository.save(new AllowedShift("Tuesday"));
		assertFalse(shift1.equals(shift2));
	}
	
	///////////////////////////////////////
	//
	// Tests for invalid values
	//
	///////////////////////////////////////
	
	@Test
	void idCannotBeNull() {
		this.entityTester.assertAttributeCannotBeNull("id");
	}
	
	@Test
	void shiftCannotBeBlank() {
		this.entityTester.assertAttributeCannotBeBlank("shift");
	}
}
