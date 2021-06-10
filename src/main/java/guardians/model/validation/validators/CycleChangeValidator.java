package guardians.model.validation.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import guardians.model.entities.CycleChange;
import guardians.model.entities.Doctor;
import guardians.model.validation.annotations.ValidCycleChange;
import lombok.extern.slf4j.Slf4j;

/**
 * This validator is used to make sure the giver and receiver {@link Doctor}s of
 * a {@link CycleChange} are different.
 * 
 * Particularly, this validator checks that both {@link Doctor}s are not null,
 * their ids are not null either, and the ids are different from one another
 * 
 * @author miggoncan
 */
@Slf4j
public class CycleChangeValidator implements ConstraintValidator<ValidCycleChange, CycleChange> {

	@Override
	public boolean isValid(CycleChange value, ConstraintValidatorContext context) {
		log.debug("Request to validate CycleChange: " + value);
		if (value == null) {
			log.debug("Cycle change is null, so it is valid");
			return true;
		}

		Doctor giver = value.getCycleGiver();
		Doctor receiver = value.getCycleReceiver();

		if (giver == null || receiver == null) {
			log.debug("Either the giver Doctor or the receiver Doctor are null, so the CycleChange is invalid");
			return false;
		}

		Long idGiver = giver.getId();
		Long idReceiver = receiver.getId();
		boolean isValid = idGiver != null && idReceiver != null && !idGiver.equals(idReceiver);
		log.debug("After comparing the ids, the CycleChange is valid: " + isValid);
		return isValid;
	}

}
