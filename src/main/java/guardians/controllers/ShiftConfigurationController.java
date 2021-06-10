package guardians.controllers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import guardians.controllers.assemblers.ShiftConfigurationAssembler;
import guardians.controllers.exceptions.AllowedShiftNotFoundException;
import guardians.controllers.exceptions.DoctorDeletedException;
import guardians.controllers.exceptions.DoctorNotFoundException;
import guardians.controllers.exceptions.InvalidEntityException;
import guardians.controllers.exceptions.InvalidShiftConfigurationException;
import guardians.controllers.exceptions.ShiftConfigurationAlreadyExistsException;
import guardians.controllers.exceptions.ShiftConfigurationNotFoundException;
import guardians.model.dtos.general.ShiftConfigurationPublicDTO;
import guardians.model.entities.AllowedShift;
import guardians.model.entities.Doctor;
import guardians.model.entities.ShiftConfiguration;
import guardians.model.entities.Doctor.DoctorStatus;
import guardians.model.repositories.AllowedShiftRepository;
import guardians.model.repositories.DoctorRepository;
import guardians.model.repositories.ShiftConfigurationRepository;
import lombok.extern.slf4j.Slf4j;

// TODO Create integration test for ShiftConfiguratinController

/**
 * The ShiftConfigurationController will handle all requests related to the
 * shift configuration of doctors
 * 
 * @author miggoncan
 */
@RestController
@RequestMapping("/doctors/shift-configs")
@Slf4j
public class ShiftConfigurationController {
	@Autowired
	private ShiftConfigurationRepository shiftConfigurationRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private AllowedShiftRepository allowedShiftRepository;

	@Autowired
	private ShiftConfigurationAssembler shiftConfigurationAssembler;

	@Autowired
	private Validator validator;

	/**
	 * This method checks that the given {@link AllowedShift} in the
	 * {@link ShiftConfiguration} are valid. This is, they all exist in the
	 * database, and the given id corresponds to the given shift
	 * 
	 * @param shiftConf The {@link ShiftConfiguration} that will be checked
	 * @throws AllowedShiftNotFoundException if one of the {@link AllowedShift} was
	 *                                       not found in the database
	 * @throws InvalidEntityException        if one of the {@link AllowedShift}'s id
	 *                                       does not correspond with its shift
	 */
	private void checkValidShiftPreferences(ShiftConfiguration shiftConf) {
		log.info("Checking shift preferences for: " + shiftConf);
		Set<AllowedShift> shiftPreferences = new HashSet<>();
		Set<AllowedShift> tempShifts = shiftConf.getUnwantedShifts();
		if (tempShifts != null) {
			shiftPreferences.addAll(tempShifts);
		}
		tempShifts = shiftConf.getUnavailableShifts();
		if (tempShifts != null) {
			shiftPreferences.addAll(tempShifts);
		}
		tempShifts = shiftConf.getWantedShifts();
		if (tempShifts != null) {
			shiftPreferences.addAll(tempShifts);
		}
		tempShifts = shiftConf.getMandatoryShifts();
		if (tempShifts != null) {
			shiftPreferences.addAll(tempShifts);
		}
		Optional<AllowedShift> foundShift = null;
		for (AllowedShift allowedShift : shiftPreferences) {
			foundShift = allowedShiftRepository.findById(allowedShift.getId());
			if (!foundShift.isPresent()) {
				log.info("The received allowed shift with id: " + allowedShift.getId()
						+ " was not found. Throwing AllowedShiftNotFound");
				throw new AllowedShiftNotFoundException(allowedShift.getId());
			} else if (!foundShift.get().getShift().equals(allowedShift.getShift())) {
				String message = "The allowed shift with id " + allowedShift.getId() + " should have the shift "
						+ foundShift.get().getShift() + " instead of " + allowedShift.getShift();
				log.info("The received allowed shift's id does not match its shift: " + message
						+ ". Throwing InvalidEntityException");
				throw new InvalidEntityException(message);
			}
		}
		log.info("The shift preferences are valid");
	}

	/**
	 * This method will convert a {@link ShiftConfigurationPublicDTO} to a valid
	 * {@link ShiftConfiguration}
	 * 
	 * @param shiftConfDTO
	 * 
	 * @return
	 * 
	 * @throws InvalidShiftConfigurationException if the
	 *                                            {@link ShiftConfigurationPublicDTO}
	 *                                            canont be converted to a valid
	 *                                            {@link ShiftConfiguration}
	 */
	private ShiftConfiguration getValidShiftConfiguration(ShiftConfigurationPublicDTO shiftConfDTO) {
		ShiftConfiguration shiftConf = shiftConfDTO.toShiftConfiguration();
		Set<ConstraintViolation<ShiftConfiguration>> violations = validator.validate(shiftConf);
		if (!violations.isEmpty()) {
			log.info("The given ShiftConfiguration is invalid. Throwing InvalidShiftConfigurationException");
			throw new InvalidShiftConfigurationException(violations);
		}
		return shiftConf;
	}

	/**
	 * This method handles GET requests for the complete list of
	 * {@link ShiftConfiguration}
	 * 
	 * @return A collection with all the {@link ShiftConfiguration} available in the
	 *         database
	 */
	@GetMapping("")
	public CollectionModel<EntityModel<ShiftConfigurationPublicDTO>> getShitfConfigurations() {
		log.info("Request received: returning all available shift configuratoins");
		List<ShiftConfiguration> shiftConfigurations = shiftConfigurationRepository.findAll();
		List<ShiftConfigurationPublicDTO> shiftConfigurationsDTO = shiftConfigurations.stream()
				.map(shiftConfiguration ->  new ShiftConfigurationPublicDTO(shiftConfiguration))
				.collect(Collectors.toCollection(() -> new LinkedList<>()));
		return shiftConfigurationAssembler.toCollectionModel(shiftConfigurationsDTO);
	}

	/**
	 * This method handles requests to create a {@link ShiftConfiguration} for an
	 * already existent {@link Doctor}
	 * 
	 * @param newShiftConfDTO the new {@link ShiftConfiguration} that will be
	 *                        persisted
	 * @return The persisted {@link ShiftConfiguration}
	 * @throws DoctorNotFoundException                  if the
	 *                                                  {@link ShiftConfiguration#getDoctorId()}
	 *                                                  is not found in the database
	 * @throws ShiftConfigurationAlreadyExistsException if the {@link Doctor} with
	 *                                                  id
	 *                                                  {@link ShiftConfiguration#getDoctorId()}
	 *                                                  already has a
	 *                                                  {@link ShiftConfiguration}
	 */
	@PostMapping("")
	public EntityModel<ShiftConfigurationPublicDTO> newShiftConfiguration(
			@RequestBody ShiftConfigurationPublicDTO newShiftConfDTO) {
		log.info("Request received: create new shift configuration: " + newShiftConfDTO);

		ShiftConfiguration newShiftConf = this.getValidShiftConfiguration(newShiftConfDTO);

		Long doctorId = newShiftConf.getDoctorId();
		Optional<Doctor> doctor = doctorRepository.findById(doctorId);
		if (!doctor.isPresent()) {
			log.info("The provided doctor id \"" + doctorId
					+ "\"does not match any existing doctor. Throwing DoctorNotFoundException");
			throw new DoctorNotFoundException(doctorId);
		}
		log.info("The doctor that will receive a shift configuration is " + doctor.get());

		if (doctor.get().getStatus() == DoctorStatus.DELETED) {
			log.info("The doctor is marked as deleted. Throwing DoctorDeletedException");
			throw new DoctorDeletedException(doctorId);
		}

		if (shiftConfigurationRepository.findById(doctorId).isPresent()) {
			log.info("The doctor with id \"" + doctorId
					+ "\" already has a shiftConfiguration. Throwing ShiftConfigurationAlreadyExists");
			throw new ShiftConfigurationAlreadyExistsException(doctorId);
		}

		// Note this might throw AllowedShiftNotFoundExceltion or InvalidEntityException
		this.checkValidShiftPreferences(newShiftConf);

		newShiftConf.setDoctor(doctor.get());
		log.info("Attemting to persist the shift configuration");
		ShiftConfiguration savedShiftConf = shiftConfigurationRepository.save(newShiftConf);
		log.info("Persisted shift configuration: " + savedShiftConf);

		return shiftConfigurationAssembler.toModel(new ShiftConfigurationPublicDTO(savedShiftConf));
	}

	/**
	 * This method handles GET requests for the {@link ShiftConfiguration} of one
	 * precise {@link Doctor}
	 * 
	 * @param doctorId The id of the {@link Doctor}
	 * @return The corresponding {@link ShiftConfiguration}
	 * @throws ShiftConfigurationNotFoundException
	 */
	@GetMapping("/{doctorId}")
	public EntityModel<ShiftConfigurationPublicDTO> getShitfConfiguration(@PathVariable Long doctorId) {
		log.info("Request received: returning shift configuration for doctor " + doctorId);
		ShiftConfiguration shiftConfig = shiftConfigurationRepository.findById(doctorId)
				.orElseThrow(() -> new ShiftConfigurationNotFoundException(doctorId));
		return shiftConfigurationAssembler.toModel(new ShiftConfigurationPublicDTO(shiftConfig));
	}

	/**
	 * This method handles requests to update the existing
	 * {@link ShiftConfiguration} of an existing {@link Doctor}
	 * 
	 * @param doctorId       The id of the {@link Doctor} whose
	 *                       {@link ShiftConfiguration} wants to be updated
	 * @param shiftConfigDTO The desired value of the {@link ShiftConfiguration}
	 * @return the saved {@link ShiftConfiguration}
	 * @throws DoctorNotFoundException
	 * @throws ShiftConfigurationNotFoundException if the {@link Doctor} with
	 *                                             doctorId does not already have a
	 *                                             {@link ShiftConfiguration}. In
	 *                                             this case, the request
	 *                                             {@link ShiftConfigurationController#newShiftConfiguration(ShiftConfigurationPublicDTO)}
	 *                                             should be used
	 * @throws AllowedShiftNotFoundException       if one of the
	 *                                             {@link AllowedShift} in the shift
	 *                                             preferences does not already
	 *                                             exist
	 * @throws InvalidEntityException              if id and shift in any of the
	 *                                             given {@link AllowedShift} in the
	 *                                             shift preferences do not match
	 */
	@PutMapping("/{doctorId}")
	public EntityModel<ShiftConfigurationPublicDTO> updateShiftConfiguration(@PathVariable Long doctorId,
			@RequestBody ShiftConfigurationPublicDTO shiftConfigDTO) {
		log.info("Request received: update shift configuration for doctor " + doctorId + " with " + shiftConfigDTO);

		ShiftConfiguration shiftConfig = this.getValidShiftConfiguration(shiftConfigDTO);

		Optional<Doctor> doctor = doctorRepository.findById(doctorId);
		if (!doctor.isPresent()) {
			log.info("The requested doctor id \"" + doctorId + "\" was not found. Throwing DoctorNotFoundException");
			throw new DoctorNotFoundException(doctorId);
		}

		if (doctor.get().getStatus() == DoctorStatus.DELETED) {
			log.info("The doctor is marked as deleted. Throwing DoctorDeletedException");
			throw new DoctorDeletedException(doctorId);
		}

		if (!shiftConfigurationRepository.findById(doctorId).isPresent()) {
			log.info("The shift configuration with id " + doctorId
					+ " was not found. Throwing ShiftConfigurationNotFoundException");
			throw new ShiftConfigurationNotFoundException(doctorId);
		}

		// Note this might throw AllowedShiftNotFoundExceltion or InvalidEntityException
		this.checkValidShiftPreferences(shiftConfig);

		shiftConfig.setDoctor(doctor.get());

		log.info("Attemting to persist the provided shift configuration");
		ShiftConfiguration savedShiftConf = shiftConfigurationRepository.save(shiftConfig);
		log.info("The persisted shift configuration is: " + savedShiftConf);

		return shiftConfigurationAssembler.toModel(new ShiftConfigurationPublicDTO(savedShiftConf));
	}
}
