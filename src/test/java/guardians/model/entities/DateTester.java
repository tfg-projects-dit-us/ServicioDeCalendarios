package guardians.model.entities;

import lombok.extern.slf4j.Slf4j;

/**
 * This class will be used to test the day, month and year field of a class
 * using the date provided by {@link DateProvider}
 * 
 * @author miggoncan
 * 
 * @param <TestedEntity> The class that will be tested
 */
@Slf4j
public class DateTester<TestedEntity> {

	private static final String violationDateMessage = "The day, month and year have to be valid";

	private EntityTester<TestedEntity> entityTester;

	/**
	 * This interface will be used to set the day, month and year of the entity
	 * being tested
	 * 
	 * @author miggoncan
	 */
	public interface DayMonthYearSetter {
		public void setDayMonthYear(Integer day, Integer month, Integer year);
	}

	/**
	 * @param testedEntityClass The class of the entity that will be tested
	 */
	public DateTester(Class<TestedEntity> testedEntityClass) {
		log.debug("Creating a DateTester");
		this.entityTester = new EntityTester<>(testedEntityClass);
	}

	/**
	 * This method will apply the dates from the {@link DateProvider} one by one
	 * using the setter, and then check for constraint violations in the
	 * objectToTest
	 * 
	 * @param objectToTest The {@link Entity} to be tested. All the fields in this
	 *                     entity (excluding day, month and year) are expected to be
	 *                     valid
	 * @param setter       The
	 *                     {@link DayMonthYearSetter#setDayMonthYear(Integer, Integer, Integer)}
	 *                     method will be called with every received date from the
	 *                     {@link DateProvider}. This method should set the
	 *                     corresponding attributes in the objectToTest
	 */
	public void testEntity(TestedEntity objectToTest, DayMonthYearSetter setter) {
		log.debug("Validating entity: " + objectToTest);
		DateProvider dateProvider = new DateProvider();
		Integer day, month, year;
		while (dateProvider.moveToNext()) {
			day = dateProvider.getDay();
			month = dateProvider.getMonth();
			year = dateProvider.getYear();
			log.debug("Setting date: " + day + "/" + month + "/" + year);
			setter.setDayMonthYear(day, month, year);

			if (dateProvider.isValidDate()) {
				log.debug("The provided date is valid. Asserting there are no constraint violations");
				this.entityTester.assertValidEntity(objectToTest);
			} else {
				this.entityTester.assertEntityViolatedConstraint(objectToTest, violationDateMessage);
			}
		}
	}
}
