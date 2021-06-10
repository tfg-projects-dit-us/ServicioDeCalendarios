package guardians.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import javax.persistence.Entity;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import lombok.extern.slf4j.Slf4j;

/**
 * This class provides some useful methods that can be used while testing
 * {@link Entity}s
 * 
 * @param <TestedEntity> Is the {@link Entity} class that will be tested
 * 
 * @author miggoncan
 */
@Slf4j
public class EntityTester<TestedEntity> {
	// Will be used to validate the constraints
	protected Validator validator;

	protected Class<TestedEntity> testedEntityClass;

	/**
	 * @param testedEntityClass The Entity class that will be tested
	 */
	public EntityTester(Class<TestedEntity> testedEntityClass) {
		log.debug("Creating a new EntityTester for class " + testedEntityClass.getCanonicalName());
		this.testedEntityClass = testedEntityClass;
		this.validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	/**
	 * This method will assert the given entity does not violate any constraint
	 * 
	 * @param entity The {@link Entity} to be checked
	 */
	public void assertValidEntity(TestedEntity entity) {
		log.debug("Request to assert a valid entity: " + entity);
		Set<ConstraintViolation<TestedEntity>> constraintViolations = validator.validate(entity);
		log.debug("The contrains violations are: " + constraintViolations);
		assertEquals(0, constraintViolations.size());
	}

	/**
	 * This method will assert the given entity violates a constraint with message
	 * 
	 * @param entity  The {@link Entity} to be tested
	 * @param message The message contained in the {@link ConstraintViolation}
	 *                violated
	 */
	public void assertEntityViolatedConstraint(TestedEntity entity, String message) {
		log.debug("Request to assert the entity: " + entity + " violates the constraint \"" + message + "\"");
		Set<ConstraintViolation<TestedEntity>> constraintViolations = validator.validate(entity);
		log.debug("The contrains violations are: " + constraintViolations);
		assertNotEquals(0, constraintViolations.size());
		assertTrue(this.constraintViolationsContains(constraintViolations, message));
	}

	/**
	 * Check if value is valid for the attribute of the tested entity
	 * 
	 * @param attribute The name of the attribute to be tested
	 * @param value     The value to be tested
	 */
	public void assertValidValue(String attribute, Object value) {
		log.debug("Testing the attribute " + attribute + " of the class " + this.testedEntityClass.getCanonicalName()
				+ " can have value " + value);
		Set<ConstraintViolation<TestedEntity>> constraintViolations = validator.validateValue(this.testedEntityClass,
				attribute, value);
		log.debug("The contrains violations are: " + constraintViolations);
		assertEquals(0, constraintViolations.size());
	}

	/**
	 * Assert value is not valid for attribute. One of the resulting constraint
	 * violations will contain expectedConstraintMessage
	 * 
	 * @param attribute                 The name of the attribute to be tested
	 * @param value                     The value to be tested
	 * @param expectedConstraintMessage The message expected in the
	 *                                  ConstraingViolations
	 */
	public void assertInvalidValue(String attribute, Object value, String expectedConstraintMessage) {
		log.debug("Testing the attribute " + attribute + " of the class " + this.testedEntityClass.getCanonicalName()
				+ " cannot have value " + value + ". Expected constraint violation is \"" + expectedConstraintMessage
				+ "\"");
		Set<ConstraintViolation<TestedEntity>> constraintViolations = validator.validateValue(this.testedEntityClass,
				attribute, value);
		log.debug("The contrains violations are: " + constraintViolations);
		assertNotEquals(0, constraintViolations.size());
		assertTrue(this.constraintViolationsContains(constraintViolations, expectedConstraintMessage));
	}

	/**
	 * Make sure the attribute cannot be blank
	 * 
	 * @param attribute The name of the attribute to be tested
	 */
	public void assertAttributeCannotBeBlank(String attribute) {
		log.debug("Testing the attribute " + attribute + " of the class " + this.testedEntityClass.getCanonicalName()
				+ " cannot be blank");
		this.assertInvalidValue(attribute, null, "must not be blank");
		this.assertInvalidValue(attribute, "", "must not be blank");
		this.assertInvalidValue(attribute, "   ", "must not be blank");
	}

	/**
	 * Make sure the attribute cannot be null
	 * 
	 * @param attribute The name of the attribute to be tested
	 */
	public void assertAttributeCannotBeNull(String attribute) {
		log.debug("Testing the attribute " + attribute + " of the class " + this.testedEntityClass.getCanonicalName()
				+ " cannot be null");
		this.assertInvalidValue(attribute, null, "must not be null");
	}
	
	/**
	 * Make sure the attribute cannot be empty
	 * 
	 * @param attribute The name of the attribute to be tested
	 */
	public void assertAttributeCannotBeEmpty(String attribute) {
		log.debug("Testing the attribute " + attribute + " of the class " + this.testedEntityClass.getCanonicalName()
				+ " cannot be empty");
		this.assertInvalidValue(attribute, null, "must not be empty");
	}
	
	/**
	 * This method checks if constraintViolations has a {@link ConstraintViolation}
	 * message containing the parameter message
	 * 
	 * @param constraintViolations The {@link ConstraintViolation}s in which to find
	 *                             the message
	 * @param message              The message to be looked for
	 * @return True if the message was found. False otherwise
	 */
	private boolean constraintViolationsContains(Set<ConstraintViolation<TestedEntity>> constraintViolations,
			String message) {
		log.debug("Checking if the constrainViolations: " + constraintViolations + " contain the message \"" + message
				+ "\"");
		boolean containsMessage = false;
		for (ConstraintViolation<?> constraintViolation : constraintViolations) {
			if (constraintViolation.getMessage().contains(message)) {
				containsMessage = true;
				break;
			}
		}
		log.debug("The given message was found: " + containsMessage);
		return containsMessage;
	}
}
