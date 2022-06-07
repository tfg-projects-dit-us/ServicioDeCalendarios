package guardians.model.repositories;

import java.util.Optional;

import javax.persistence.Entity;

import org.springframework.data.jpa.repository.JpaRepository;

import guardians.model.entities.Rol;

/**
 * This interface will be used by Jpa to auto-generate a class having all the
 * CRUD operations on the {@link Rol} {@link Entity}. This operations will be
 * performed differently depending on the configured data-source. But this is
 * completely transparent to the application
 * @author carcohcal
 *
 */

public interface RolRepository extends JpaRepository<Rol, Long>{

	public Optional<Rol> findBynombreRol(String rol);
}
