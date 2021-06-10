package guardians.model.entities;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

/**
 * This class will supply with dates, some of the valid and some of them
 * invalid.
 * 
 * Its purpose is to allow other Test classes to easily have dates to test, and
 * not have to hard-code them in every single test. The way in which this class
 * is intended to be used is as follows:
 * 
 * DateProvider myDateProvider = new DateProvider(); 
 * Integer day, month, year; 
 * while (myDateProvider.moveToNext()) { 
 *     day = myDateProvider.getDay(); 
 *     month = myDateProvider.getMonth(); 
 *     year = myDateProvider.getYear(); 
 *     if (myDateProvider.isValidDate()) { 
 *         // Do something 
 *     } else { 
 *         // Do something else 
 *     } 
 * }
 * 
 * @author miggoncan
 */
@Slf4j
public class DateProvider {

	/**
	 * The data array will contain an entry for each date to be provided
	 * 
	 * The first int will be the day, the second one will be the month, the third
	 * one will be the year, and the fourth and last one will be 1 if the date is
	 * valid or 0 if invalid
	 */
	private Integer[][] data = { 
			// First, valid dates
			{01, 05, 2020, 1},
			{30, 06, 2026, 1},
			{16, 11, 2017, 1},
			{31, 12, 2023, 1},
			{29, 02, 2020, 1}, // 2020 is a leap year
			// Then, invalid dates
			{29, 02, 2021, 0}, // 2021 is not a leap year
			{30, 02, 2021, 0}, // February cannot have 30 days
			{31, 04, 2016, 0}, // April has 30 days
			{01, 13, 2020, 0}, // There are only 12 months
			{32, 05, 2020, 0}, //No month can have 32 days
			// Null values are invalid
			{null, 06,   2020, 0},
			{null, null, 2020, 0},
			{null, 06,   null, 0},
			{null, null, null, 0},
			{26,   null, 2020, 0},
			{26,   null, null, 0},
			{26,   06,   null, 0}
	};
	
	// Index starts in -1 so that, when moveToNext is first called, it will be 0 
	// and access the first element in data
	private int index = -1;

	public DateProvider() {
		log.debug("This date provider has " + this.data.length + " dates to provide");
	}
	
	/**
	 * This method will create a valid date Note the returned date will be the same
	 * every time this method is called
	 * 
	 * @return The created date
	 */
	public static LocalDate getValidDate() {
		return LocalDate.of(2020, 6, 26);
	}

	public boolean moveToNext() {
		this.index++;
		log.debug("Moving to index: " + this.index);
		boolean hasMoreDateToProvide = this.index < this.data.length;
		log.debug("Are there more available dates: " + hasMoreDateToProvide);
		return hasMoreDateToProvide;
	}

	public Integer getDay() {
		this.checkIndex();
		Integer day = this.data[this.index][0];
		log.debug("Requested day is: " + day);
		return day;
	}

	public Integer getMonth() {
		this.checkIndex();
		Integer month = this.data[this.index][1];
		log.debug("Requested month is: " + month);
		return month;
	}

	public Integer getYear() {
		this.checkIndex();
		Integer year = this.data[this.index][2];
		log.debug("Requested year is: " + year);
		return year;
	}

	public boolean isValidDate() {
		this.checkIndex();
		boolean isValid = this.data[this.index][3] == 1;
		log.debug("Requested date is valid: " + isValid);
		return isValid;
	}
	
	private void checkIndex() {
		if (index == -1) {
			log.debug("Index is -1. moveToNext has to be called first. Throwing RunTimeException");
			throw new RuntimeException("To retrieve a date, first call moveToNext()");
		}
		
		if (index >= data.length) {
			log.debug("Index is " + index + ", which is out of bound. Throwing RunTimeException");
			throw new RuntimeException("All dates have already been retrieved");
		}
	}
}
