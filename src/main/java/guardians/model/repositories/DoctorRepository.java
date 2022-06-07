package guardians.model.repositories;

import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import guardians.model.entities.Doctor;

/**
 * This interface will be used by Jpa to auto-generate a class having all the
 * CRUD operations on the {@link Doctor} {@link Entity}. This operations will be
 * performed differently depending on the configured data-source. But this is
 * completely transparent to the application
 * 
 * @author miggoncan
 *
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

	/**
	 * Retrieve a {@link Doctor} from the database if it exists, provided its
	 * email
	 * 
	 * @param email The email of the {@link Doctor}
	 * @return The {@link Doctor}, if found
	 */
	public Optional<Doctor> findByEmail(String email);
	
	/**Retrieve a {@link Doctor} from the database if it exists, provided its
	 * Telegram ID
	 * @author carcohcal
	 * @param telegramId
	 * @return
	 */
	public Optional<Doctor> findBytelegramId(String telegramId);
	
	
	
	 @Query("SELECT u FROM Doctor u JOIN u.roles r WHERE r.nombreRol LIKE %?1%")
	   public List<Doctor> findByRol(String keyword);
	}
	    
	


