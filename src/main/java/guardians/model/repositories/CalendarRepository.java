package guardians.model.repositories;

import jakarta.persistence.Entity;

import org.springframework.data.jpa.repository.JpaRepository;

import guardians.model.entities.Calendar;
import guardians.model.entities.primarykeys.CalendarPK;

/**
 * This interface will be used by Jpa to auto-generate a class having all the
 * CRUD operations on the {@link Calendar} {@link Entity}. This operations will
 * be performed differently depending on the configured data-source. But this is
 * completely transparent to the application
 * 
 * @author miggoncan
 */
public interface CalendarRepository extends JpaRepository<Calendar, CalendarPK> {

}
