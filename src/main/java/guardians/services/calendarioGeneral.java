package guardians.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.caldav4j.exceptions.CalDAV4JException;

import guardians.MetodosCalendario;
import guardians.model.entities.Doctor;
import guardians.model.entities.Schedule;
import guardians.model.entities.ScheduleDay;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Uid;

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
	private CalendariosIndivuales calIndiv;
	@Autowired 
	private CalDav caldav;
	
	private String nombreFichero = "calendarioGeneral.ics";
	
	public String getNombreFichero() {
		return nombreFichero;
	}
	public void setNombreFichero(String nombreFichero) {
		this.nombreFichero = nombreFichero;
	}


	private HashMap<String, String> ids;
	@Autowired
	private MetodosCalendario metodos;

	

    public void init(Schedule schedule){
		this.horario = schedule;
		ids = new HashMap<String, String>();
		ids.put(cycle, "jc");
		ids.put(shifts,"ca");
		ids.put(consultation, "c");
		metodos.init();
		log.info("Funcion init calendarioGeneral completada");
		
		}
  /**
   * Construye el objeto calendario
   * @throws IOException
   * @throws GeneralSecurityException
   * @throws InterruptedException
   * @throws URISyntaxException
   * @throws ParserException
   * @throws CalDAV4JException
   */
public void creaCalendario() throws IOException, GeneralSecurityException, InterruptedException, URISyntaxException, ParserException, CalDAV4JException {
    	
    	
	Calendar calendario = metodos.getCalendario();
	        
    mes = horario.getMonth(); 
    anio = horario.getYear();
    log.info("Calendario creado para: " +mes+anio);      
    
    calIndiv.init(mes, anio);
       
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
       metodos.generaFichero(calendario,getNombreFichero());
       caldav.publicarCalendario(calendario);
       
    }



	private VEvent creaEvento(Integer numDia, String summary, Set<Doctor> doctores) throws URISyntaxException {
		 
       
        
       
        Iterator<Doctor> iterator = doctores.iterator();
        
     	//Evento Individual, necesario para que no haya corrupción de datos
     	   	
         VEvent event=new VEvent( metodos.fecha(anio, mes, numDia),summary);
         String ID = numDia.toString()+mes.toString()+anio.toString()+ ids.get(summary);
         Uid   uid = new Uid(ID);
            event.getProperties().add(uid);
            
            
		while(iterator.hasNext()) {
				Doctor doctor = iterator.next();
				calIndiv.addEvent(numDia, summary,ID,doctor);
				String nombre = doctor.getFirstName()+" "+doctor.getLastNames();
				String email =doctor.getEmail();
				Attendee asistente =new Attendee(URI.create("mailto:"+email));
				asistente.getParameters(Parameter.CUTYPE).add(CuType.INDIVIDUAL);
				asistente.getParameters(Parameter.CN).add(new Cn(nombre));
				event.getProperties().add(asistente);
			    }	
		return event;
	}

	
	
	/*public void actualizarCalendario(Schedule horarioModificado) throws ClientProtocolException, IOException, ParserException, URISyntaxException {
		Calendar calendarOriginal = metodos.getCalendario();
		
		Calendar calendarioModif = null;
		
		 
		
		mes = horario.getMonth(); 
	    anio = horario.getYear();
	    log.info("Calendario creado para: " +mes+anio);      
	    
	    calIndiv.init(mes, anio);
	       
	    SortedSet<ScheduleDay> dias = horarioModificado.getDays();
	    Iterator<ScheduleDay> iterator = dias.iterator();
	        
	    while (iterator.hasNext()) {
	        	ScheduleDay dia = iterator.next();
	        	Integer numDia = dia.getDay();
	        	Set<Doctor> ciclicasDr = dia.getCycle();
	        	Set<Doctor> consultasDr = dia.getConsultations();
	        	Set<Doctor> turnosDr = dia.getShifts();
	        	
				if (ciclicasDr.size()>0) {
					VEvent evento = creaEvento(numDia,cycle,ciclicasDr);
					calendarioModif.getComponents().add(evento); }
				
				if (turnosDr.size()>0) {
					VEvent evento=creaEvento( numDia,shifts,turnosDr);
					calendarioModif.getComponents().add(evento);		}
				
				if (consultasDr.size()>0) {
					VEvent evento = creaEvento(numDia,consultation,consultasDr);
					calendarioModif.getComponents().add(evento);	}
				}
	}*/
	
	
}