package guardians.model.entities;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import guardians.model.repositories.DoctorRepository;

@DataJpaTest
public class CycleChangeTest {

	@Autowired
	private DoctorRepository doctorRepository;
	
	private EntityTester<CycleChange> entityTester;

	public CycleChangeTest() {
		entityTester = new EntityTester<>(CycleChange.class);
	}

	@Test
	void testDates() {
		List<Doctor> doctors = DoctorTest.createValidDoctors();
		Doctor giver = doctorRepository.save(doctors.get(0));
		Doctor receiver = doctorRepository.save(doctors.get(1));
		CycleChange cycleChange = new CycleChange(giver, receiver);
		DateTester<CycleChange> dateTester = new DateTester<>(CycleChange.class);
		dateTester.testEntity(cycleChange, (day, month, year) -> {
			cycleChange.setDay(day);
			cycleChange.setMonth(month);
			cycleChange.setYear(year);
		});
	}

	@Test
	void giverAndReceiverCannotBeTheSame() {
		Doctor doctor = doctorRepository.save(DoctorTest.createValidDoctor());
		CycleChange cycleChange = new CycleChange(doctor, doctor);
		cycleChange.setDay(30);
		cycleChange.setMonth(5);
		cycleChange.setYear(2020);
		this.entityTester.assertEntityViolatedConstraint(cycleChange,
				"The giver Doctor and the receiver Doctor in the cycle change cannot be the same");
	}
}
