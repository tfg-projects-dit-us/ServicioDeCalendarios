package guardians.services;


import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import guardians.MetodosCalendario;
import guardians.controllers.exceptions.DoctorNotFoundException;
import guardians.model.entities.Doctor;
import guardians.model.entities.Schedule;
import guardians.model.repositories.DoctorRepository;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.ValidationException;



/**
 * This Service will represent the information related to a {@link Schedule} in
 * a ics file so it can be export to a calendar service
 * 
 * @author Carcohcal
 * **/
@Slf4j
@Service
public class CalendariosIndivuales {
	@Autowired
	private EmailService emailController;
	private HashMap<Integer, Calendar> calIndividuales= new HashMap<Integer, Calendar>();
	private HashMap<Integer, String> emails = new HashMap<Integer, String> ();
	
	@Autowired
	private DoctorRepository doctorRepository;
	@Autowired
	private MetodosCalendario metodos;
	
	/** Metodo que crea los parámetros globales
	 * @param schedule es el horario del servicio para el mes indicado*/
	public void setFecha(Integer mes,Integer anio){
		log.info("Iniciado parámetros calendario individuales");
	}
	
	/**Función que genera los ficheros ics de los calenarios individuales y llama a la función que los envía por correo 
	 * @throws IOException 
	 * @throws ValidationException */
	public void enviaCalendarios() throws ValidationException, IOException {
		
			
			for (Integer i : calIndividuales.keySet()) {
				 String nomFich = "calendario"+i+".ics";
				 metodos.generaFichero(calIndividuales.get(i), nomFich);
				 //Se envia por email el calendario individual
				 emailController.enviarEmail(emails.get(i), nomFich);
				  
				}
		log.info("calendarios individuales mandados a enviar");
	}

	
	
public void addEvent(VEvent event, Attendee medico) {
	
	String email=medico.getCalAddress().getSchemeSpecificPart();
	
	/* recuperar el ID del doctor*/
	Optional<Doctor> doctor = null;
	
	
	log.info("Request received: return the doctor with emailT: " + email);
	doctor = doctorRepository.findByEmail(email);
	if (!doctor.isPresent()){
		log.info("The email could not be found. Thorwing DoctorNotFoundException");
		throw new DoctorNotFoundException(email);
		
	}

  
		int id = doctor.get().getId().intValue();
		
		
		//Si no existe la clave se crea un calendario individual para el doctor
		
		if (calIndividuales.containsKey(id)!=true) {
			
			Calendar calInd = new Calendar();
			String prodId = "-//Turnos Dr"+medico.getParameters(Parameter.CN)+"//iCal4j 1.0//EN";
			calInd.getProperties(Property.PRODID).add(new ProdId(prodId));
			calInd.getProperties(Property.VERSION).add(Version.VERSION_2_0);
			calInd.getProperties(Property.CALSCALE).add(CalScale.GREGORIAN);
			calIndividuales.put(id, calInd);
			emails.put(id, email);	
		} 
		//Se añade el evento al calendario
		calIndividuales.get(id).getComponents().add(event);
		
	}
	
}


