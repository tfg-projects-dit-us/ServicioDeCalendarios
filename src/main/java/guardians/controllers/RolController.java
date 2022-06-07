package guardians.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import guardians.controllers.exceptions.AlreadyExistsException;
import guardians.controllers.exceptions.DoctorNotFoundException;
import guardians.controllers.exceptions.TelegramIDNotFoundException;
import guardians.model.entities.Doctor;
import guardians.model.entities.Rol;
import guardians.model.repositories.RolRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rol")
public class RolController {

	@Autowired
	private RolRepository rolRepository;
	
	@GetMapping("")
	public String getRols() {
		
		log.info("Request received: returning all available rols");
		List<Rol> roles = rolRepository.findAll();
		return roles.toString();	
		
	}
	@PutMapping("/{rol}")
	public String addNuevoRol(@PathVariable("rol") String rol) {
		
		Optional<Rol> roles = null;
				
		log.info("Request received: add rol: " + rol);
		roles = rolRepository.findBynombreRol(rol);
		if (roles.isPresent()){
			log.info("The rol already exists. Thorwing AlreadyExistsException");
			throw new AlreadyExistsException("Rol "+ rol+" ya existe");
			
		}else {
			rolRepository.save(new Rol(rol));
			
		} 
		List<Rol> listRoles = rolRepository.findAll();
		String res = "Lista completa de roles: "+listRoles.toString();
				return res;
		
	}
}
