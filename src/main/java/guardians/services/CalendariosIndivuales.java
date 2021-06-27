package guardians.services;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.HashMap;
import javax.mail.MessagingException;
import guardians.model.entities.Doctor;
import guardians.model.entities.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
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
	

	@Value("${calendario.tipo.cycle}")
	private  String cycle;
	@Value("${calendario.tipo.consultation}")
	private  String consultation;
	@Value("${calendario.tipo.shifts}")
	private  String shifts;
	
	private HashMap<Integer, Calendar> calIndividuales;
	private HashMap<Integer, String> emails;
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
				  try {
					emailController.enviarEmail(emails.get(i), nomFich);
				} catch (MessagingException | GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
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

		// initialise as an all-day event..
		LocalDateTime fecha = LocalDateTime.of(anio, mes, numDia, 0, 0);
		
		//Generador de ids
		 RandomUidGenerator ug = new RandomUidGenerator();
		 
		//Evento Individual, necesario para que no haya corrupción de datos
       VEvent event=new VEvent(fecha,fecha.plusHours(24),summary);
       event.add(ug.generateUid());
       
		//Se añade el evento al calendario
		calIndividuales.get(id).add(event);
	}


	
}


