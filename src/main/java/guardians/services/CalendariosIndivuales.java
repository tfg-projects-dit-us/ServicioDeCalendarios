package guardians.services;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import guardians.model.entities.Doctor;
import guardians.model.entities.Schedule;
import guardians.model.entities.ScheduleDay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;

import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.validate.ValidationException;


/**
 * This Service will represent the information related to a {@link Schedule} in
 * a ics file so it can be export to a calendar service
 * 
 * @author Carcohcal
 * **/

@Service
public class CalendariosIndivuales {
	@Autowired
	private EmailService emailController;
	private Schedule horario; 
	@Value("${calendario.tipo.cycle}")
	private  String cycle;
	@Value("${calendario.tipo.consultation}")
	private  String consultation;
	@Value("${calendario.tipo.shifts}")
	private  String shifts;
	
	private HashMap<Integer, Calendar> calIndividuales;
	private HashMap<Integer, String> emails;
	private Calendar calendario;

	private Integer mes;
	private Integer anio;
	
	/** Metodo que crea los parámetros globales
	 * @param schedule es el horario del servicio para el mes indicado*/
	public void init(Integer mes,Integer anio){
		this.mes = mes;
		this.anio = anio;
		calIndividuales = new HashMap<Integer, Calendar>();
		
		emails = new HashMap<Integer, String> ();
	}
	/**Función encargada de componer el objeto calendario*/
	
	
	/**Funcición que crea los eventos para cada guardia y añade a los participantes, ademas llama a la función que gestiona los calendarios indivisuales
	 * @param dia del evento
	 * @param tipo tipo de guardia
	 * @param doctores implicados en dicha guardia*/
	
	
	/**Función que genera los ficheros ics de los calenarios individuales y llama a la función que los envía por correo */
	public void enviaCalendarios() {
		
		FileOutputStream fout;
		
		try {
			
			CalendarOutputter outputter = new CalendarOutputter();	
			outputter.setValidating(false);
			emailController.init();
			for (Integer i : calIndividuales.keySet()) {
				  String nomFich = "calendarioIndividual.ics";
				  fout = new FileOutputStream(nomFich);
				  outputter.output(calIndividuales.get(i), fout);
				  
				  //Se envia por email el calendario individual
				  emailController.enviarEmail(emails.get(i), nomFich);
				  
				}
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	/**Crea calendarios indiviuales para cada médico y añade sus eventos
	 * @param numDia dia que ocurre la guardia
	 * @param summary tipo de guardia
	 * @param doctor implicado en este calendario*/

	public void addEvent(Integer numDia, String summary, Doctor doctor) {
		
		int id = doctor.getId().intValue();
		
		//Si no existe la clave se crea un calendario individual para el doctor
		if (calIndividuales.containsKey(id)!=true) {
			
			Calendar calInd = new Calendar();
			String prodId = "-//Turnos Dr"+doctor.getLastNames()+"//iCal4j 1.0//EN";
			calInd.add(new ProdId(prodId));
			calInd.add(Version.VERSION_2_0);
			calInd.add(CalScale.GREGORIAN);
			calIndividuales.put(id, calInd);
			emails.put(id, doctor.getEmail());
			
		}
		
		java.util.Calendar cal = java.util.Calendar.getInstance();
		 cal.set(java.util.Calendar.MONTH, mes-1);
		 cal.set(java.util.Calendar.DAY_OF_MONTH, numDia);
		 cal.set(java.util.Calendar.YEAR, anio);
		

		// initialise as an all-day event..
		Date date = cal.getTime();   
		LocalDate fecha = date.toInstant().atZone(ZoneId.of("Europe/Madrid")).toLocalDate();
		
		//Generador de ids
		 RandomUidGenerator ug = new RandomUidGenerator();
		 
		//Evento Individual, necesario para que no haya corrupción de datos
       VEvent event=new VEvent(fecha,fecha,summary);
       event.add(ug.generateUid());
       
		//Se añade el evento al calendario
		calIndividuales.get(id).add(event);
	}


	
}


