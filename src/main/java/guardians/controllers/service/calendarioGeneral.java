package guardians.controllers.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Predicate;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.caldav4j.exceptions.CalDAV4JException;

import guardians.controllers.exceptions.DoctorNotFoundException;
import guardians.controllers.exceptions.EventNotFoundException;
import guardians.model.entities.Doctor;
import guardians.model.entities.Schedule;
import guardians.model.entities.ScheduleDay;
import guardians.model.repositories.DoctorRepository;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.predicate.PropertyMatchesRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.validate.ValidationException;
/**
 *  Clase que continee los métodos del calendario general del servicio
 * @author carcohcal
 * @date 12 feb. 2022
 * @version 1.0
 */
@SuppressWarnings("deprecation")
@Service
@Slf4j
public class calendarioGeneral {
	
	
	@Value("${calendario.tipo.cycle}")
	private  String cycle;
	@Value("${calendario.tipo.consultation}")
	private  String consultation;
	@Value("${calendario.tipo.shifts}")
	private  String shifts;
	private Schedule horario;
	
	private Integer anio;
	private Integer mes;
	@Autowired
	private CalendariosIndividuales calIndiv;
	@Autowired 
	private CalDav caldav;
	@Autowired
	private DoctorRepository doctorRepository;
	private String nombreFichero = "calendarioGeneral.ics";
	
	public String getNombreFichero() {
		return nombreFichero;
	}
	public void setNombreFichero(String nombreFichero) {
		this.nombreFichero = nombreFichero;
	}


	private HashMap<String, String> ids= new HashMap<String, String>();
	/**
	 *  Valor del Schedule para el qeu se va a generar el calendario
	 * @author carcohcal
	 * @date 12 feb. 2022
	 * @param schedule
	 * @throws MessagingException 
	 * @throws CalDAV4JException 
	 * @throws ParserException 
	 * @throws URISyntaxException 
	 * @throws InterruptedException 
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 * @throws ValidationException 
	 * @throws AddressException 
	 */
	public void setHorario(Schedule schedule) throws AddressException, ValidationException, IOException, GeneralSecurityException, InterruptedException, URISyntaxException, ParserException, CalDAV4JException, MessagingException {
		this.horario = schedule;
		creaCalendario();
	}

   
/**
 * Constyruye el objeto calendario {@link Calendar} del objeto Schedule pasado por {@link calendarioGeneral#setHorario(Schedule) setHorario}
 * @author carcohcal
 * @date 12 feb. 2022
 * @throws IOException
 * @throws GeneralSecurityException
 * @throws InterruptedException
 * @throws URISyntaxException
 * @throws ParserException
 * @throws CalDAV4JException
 * @throws AddressException
 * @throws ValidationException
 * @throws MessagingException
 */
private void creaCalendario() throws IOException, GeneralSecurityException, InterruptedException, URISyntaxException, ParserException, CalDAV4JException, AddressException, ValidationException, MessagingException {
    
	ids.put(cycle, "jc");
	ids.put(shifts,"ca");
	ids.put(consultation, "c");
    	
	Calendar calendario = caldav.getCalendarioServer();
	        
    mes = horario.getMonth(); 
    anio = horario.getYear();
    log.info("Calendario creado para: " +mes+anio);      
    
       
    SortedSet<ScheduleDay> dias = horario.getDays();
    Iterator<ScheduleDay> iterator = dias.iterator();
        
    while (iterator.hasNext()) {
        	ScheduleDay dia = iterator.next();
        	Integer numDia = dia.getDay();
        	Set<Doctor> ciclicasDr = dia.getCycle();
        	Set<Doctor> consultasDr = dia.getConsultations();
        	Set<Doctor> turnosDr = dia.getShifts();
        	
			if (ciclicasDr.size()>0) {
				VEvent evento = creaEvento(numDia,cycle,ciclicasDr);
				calendario.getComponents().add(evento); }
			
			if (turnosDr.size()>0) {
				VEvent evento=creaEvento( numDia,shifts,turnosDr);
				calendario.getComponents().add(evento);		}
			
			if (consultasDr.size()>0) {
				VEvent evento = creaEvento(numDia,consultation,consultasDr);
				calendario.getComponents().add(evento);	}
			}
        
       calIndiv.enviaCalendarios();
       generaFichero.generarFichero(calendario,getNombreFichero());
       caldav.publicarCalendario(calendario);
       
    }


/**
 *  Método que conffigura el evento (VEvent) con los parámtros prporcionados
 * @author carcohcal
 * @date 12 feb. 2022
 * @param numDia día del mes en el que ocurrirá el evento
 * @param summary descripción {@link net.fortuna.ical4j.model.property.Summary}  del evento, en este caso tipo de turno
 * @param doctores {@link Doctor} que participará en el turno
 * @return evento generado (VEvent) 
 * @throws URISyntaxException
 */
	private VEvent creaEvento(Integer numDia, String summary, Set<Doctor> doctores) throws URISyntaxException {  
       
        Iterator<Doctor> iterator = doctores.iterator();
        
     	//Evento Individual, necesario para que no haya corrupción de datos
     	   	
         VEvent event=new VEvent( caldav.conviertefecha(anio, mes, numDia),summary);
         String ID = numDia.toString()+mes.toString()+anio.toString()+ ids.get(summary);
         Uid   uid = new Uid(ID);
         event.getProperties().add(uid);
            
         VEvent event_indi = new VEvent( caldav.conviertefecha(anio, mes, numDia),summary);
         event_indi.getProperties().add(uid);
		while(iterator.hasNext()) {
				Doctor doctor = iterator.next();
				
				String nombre = doctor.getFirstName()+" "+doctor.getLastNames();
				String email =doctor.getEmail();
				Attendee asistente =new Attendee(URI.create("mailto:"+email));
				asistente.getParameters(Parameter.CUTYPE).add(CuType.INDIVIDUAL);
				asistente.getParameters(Parameter.CN).add(new Cn(nombre));
				calIndiv.addEvent(event_indi,asistente);
				event.getProperties().add(asistente);
			    }	
		return event;
	}

	/**
	 *  Método que contiene la lógica para modifcar el calendario generado en {@link calendarioGeneral#creaCalendario()}
	 * @author carcohcal
	 * @date 12 feb. 2022
	 * @param eventosModificado
	 * @return devuelve un mensaje de texto informativo
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParserException
	 * @throws URISyntaxException
	 * @throws EventNotFoundException
	 * @throws ParseException
	 * @throws AddressException
	 * @throws ValidationException
	 * @throws MessagingException
	 */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String modficarCalendario(Calendar eventosModificado) throws ClientProtocolException, IOException, ParserException, URISyntaxException, EventNotFoundException, ParseException, AddressException, ValidationException, MessagingException {
		Calendar calendarOriginal = caldav.getCalendarioServer();
		
		Calendar calendarioModif = eventosModificado;  
		String mensaje = "Calendario actualizado";
	    
	   for (int i = 0; i < calendarioModif.getComponents(Component.VEVENT).size(); i++) {
		
		  /* Creaccion de los filtros para compronar que el evento existe en el calendario original*/
		  Property id = calendarioModif.getComponents(Component.VEVENT).get(i).getProperty(Property.UID);
		  Property fecha = calendarioModif.getComponents(Component.VEVENT).get(i).getProperty(Property.DTSTART);
		  PropertyMatchesRule eventRuleMatch = new PropertyMatchesRule(id, id.getValue());
		  PropertyMatchesRule eventRuleMatch2 = new PropertyMatchesRule(fecha, fecha.getValue());
		  Filter filtro = new Filter<CalendarComponent>(new Predicate[] { eventRuleMatch2,eventRuleMatch}, Filter.MATCH_ALL);
						
			Collection eventos = filtro.filter(calendarOriginal.getComponents(Component.VEVENT));
			
			/* Comprobar si hay algún evento que cumple las características*/
			if (eventos.size()==0) {
				throw new EventNotFoundException(id.getValue(),fecha.getValue());
			}
			
			
			PropertyList<Property> dr_nuevos = calendarioModif.getComponents(Component.VEVENT).get(i).getProperties(Property.ATTENDEE);
			comprobarDoctores (dr_nuevos);
			log.info("Doctores nuevos: "+ dr_nuevos.toString());
			
			PropertyList<Property> dr_originales = getEvento( eventos).getProperties(Property.ATTENDEE);
			log.info("Doctores originales: "+dr_originales.toString());
			
			VEvent eventOri = getEvento(eventos);
			
			calendarOriginal.getComponents().remove(eventOri);
							
			
			//Añadimos los doctores nuevos
			PropertyList<Property> nuevos= comparaDoctor(dr_nuevos,dr_originales);
			addEventoIndividual(setEvento(eventOri),nuevos ,false);
			Iterator  iterador = nuevos.iterator();	
			while(iterador.hasNext()) {
				eventOri.getProperties().add((Attendee)iterador.next());	
			}
			
			
			//Eliminamos los doctores antiguos
			PropertyList<Property> antiguos = comparaDoctor(dr_originales, dr_nuevos);
			addEventoIndividual(setEvento(eventOri),antiguos ,true);
			Iterator<Property> iterator = antiguos.iterator();
			while(iterator.hasNext()) {
				eventOri.getProperties().remove((Attendee)iterator.next());
			}
								
			calendarOriginal.getComponents().add(eventOri);			
	   }
	   caldav.publicarCalendario(calendarOriginal);
	   calIndiv.enviaCalendarios();
	   return mensaje;
	   
	}
	
	/**
	 * Compara dos listas de doctores
	 * @author carcohcal
	 * @date 12 feb. 2022
	 * @param doctoresLista
	 * @param docotoresEliminar
	 * @return devuelve los doctores que no se encuentrán en ambas listas
	 * @throws ParseException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private PropertyList<Property> comparaDoctor(PropertyList<Property> doctoresLista, PropertyList<Property> docotoresEliminar) throws ParseException, IOException, URISyntaxException {
		
		PropertyList<Property> doctores= new PropertyList<Property> (doctoresLista);
		doctores.removeAll(docotoresEliminar);

		return doctores;
	}
	
	/**
	 * Comprueba si los {@link Doctor} existen en la base de datos
	 * @author carcohcal
	 * @date 12 feb. 2022
	 * @param doctores
	 */
	private void comprobarDoctores(PropertyList<Property> doctores) {
		
		
		Optional<Doctor> doctor = null;
		
		for (int i=0; i< doctores.size(); i++) {
		String email = ((Attendee) doctores.get(i)).getCalAddress().getSchemeSpecificPart();
		log.debug("Request received: check the doctor with email: " + email);
		doctor = doctorRepository.findByEmail(email);
		if (!doctor.isPresent()){
			log.info("The email could not be found. Thorwing DoctorNotFoundException");
			throw new DoctorNotFoundException(email);
			
		}}
	}
	
	/**
	 * Extrae un evento de una Collection<CalendarComponent>
	 * @author carcohcal
	 * @date 12 feb. 2022
	 * @param eventosDoctor
	 * @return evento VEvent
	 */
	private VEvent getEvento( Collection<CalendarComponent> eventosDoctor) {
		
			Iterator<CalendarComponent>  iterador = eventosDoctor.iterator();
			VEvent evento = null;
		while(iterador.hasNext()) {
			 evento = (VEvent) iterador.next();
			
		}
		return evento;
	}
	/**
	 * Añade los eventos individuales a los calendarios individuale sgestionados en {@link CalendariosIndividuales}
	 * @author carcohcal
	 * @date 12 feb. 2022
	 * @param evento
	 * @param doctores
	 * @param cancel
	 */
	private void addEventoIndividual(VEvent evento,PropertyList<Property> doctores ,boolean cancel) {
		
		if (cancel)
		{
			evento.getProperties().add(Status.VEVENT_CANCELLED);
		}
		Iterator<Property> iterator = doctores.iterator();
		while(iterator.hasNext()) {
			calIndiv.addEvent(evento, (Attendee)iterator.next());
		}
		
	}
	/**
	 * Método auxiliar que devuelve un evento nuevo creado con los mismo parámetros que el argumento
	 * @author carcohcal
	 * @date 12 feb. 2022
	 * @param event VEvent evento que se desea clonar
	 * @return evento VEvent clonado
	 */
	
	private VEvent setEvento(VEvent event) {
		VEvent eventInd = new VEvent();
		
		eventInd.getProperties().add(event.getProperty(Property.DTSTART));
		eventInd.getProperties().add(event.getProperty(Property.UID));
		eventInd.getProperties().add(event.getProperty(Property.SUMMARY));
		return eventInd;
	}
}