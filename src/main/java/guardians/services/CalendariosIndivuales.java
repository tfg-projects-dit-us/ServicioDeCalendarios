package guardians.services;


import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import guardians.MetodosCalendario;
import guardians.model.entities.Doctor;
import guardians.model.entities.Schedule;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.RandomUidGenerator;
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
	private HashMap<Integer, Calendar> calIndividuales;
	private HashMap<Integer, String> emails;
	

	private Integer mes;
	private Integer anio;


	@Autowired
	private MetodosCalendario metodos;
	
	/** Metodo que crea los parámetros globales
	 * @param schedule es el horario del servicio para el mes indicado*/
	public void init(Integer mes,Integer anio){
		this.mes = mes;
		this.anio = anio;
		calIndividuales = new HashMap<Integer, Calendar>();
		metodos.init(); 
		emails = new HashMap<Integer, String> ();
		log.info("Iniciado parámetros calendario individuales");
	}
	
	/**Función que genera los ficheros ics de los calenarios individuales y llama a la función que los envía por correo 
	 * @throws IOException 
	 * @throws ValidationException */
	public void enviaCalendarios() throws ValidationException, IOException {
		
			emailController.init();
			for (Integer i : calIndividuales.keySet()) {
				 String nomFich = "calendario"+i+".ics";
				 metodos.generaFichero(calIndividuales.get(i), nomFich);
				 //Se envia por email el calendario individual
				 emailController.enviarEmail(emails.get(i), nomFich);
				  
				}
		log.info("calendarios individuales mandados a enviar");
	}

	
	/**Crea calendarios indiviuales para cada médico y añade sus eventos
	 * @param numDia dia que ocurre la guardia
	 * @param summary tipo de guardia
	 * @param uid 
	 * @param doctor implicado en este calendario*/

	public void addEvent(Integer numDia, String summary, String uid, Doctor doctor) {
		
		int id = doctor.getId().intValue();
		
		
		//Si no existe la clave se crea un calendario individual para el doctor
		if (calIndividuales.containsKey(id)!=true) {
			
			Calendar calInd = new Calendar();
			String prodId = "-//Turnos Dr"+doctor.getLastNames()+"//iCal4j 1.0//EN";
			calInd.getProperties(Property.PRODID).add(new ProdId(prodId));
			calInd.getProperties(Property.VERSION).add(Version.VERSION_2_0);
			calInd.getProperties(Property.CALSCALE).add(CalScale.GREGORIAN);
			calIndividuales.put(id, calInd);
			emails.put(id, doctor.getEmail());
			
		}
	 
		//Evento Individual, necesario para que no haya corrupción de datos
		 
       VEvent event=new VEvent(metodos.fecha(anio, mes, numDia),summary);
       event.getProperties().add(new Uid(uid));
       
		//Se añade el evento al calendario
		calIndividuales.get(id).getComponents().add(event);
		
	}

	
}


