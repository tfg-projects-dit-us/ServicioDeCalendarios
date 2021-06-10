package guardians.services;



import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarNotification;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import guardians.model.entities.Doctor;
import guardians.model.entities.Schedule;
import guardians.model.entities.ScheduleDay;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
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
	
	private HashMap<String, String> ids;
	private HashMap<String, String> colores;	
	
	
    private static final String APPLICATION_NAME = "Calendario General Guardias";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
	

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = calendarioGeneral.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

	

    public void init(Schedule schedule){
		this.horario = schedule;
		ids = new HashMap<String, String>();
		ids.put(cycle, "jc");
		ids.put(shifts,"ca");
		ids.put(consultation, "c");
		colores = new HashMap<String, String>();
		colores.put(cycle, "5");
		colores.put(shifts,"7");
		colores.put(consultation, "10");
	}
    
    public void creaCalendario() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar calendario = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();  
        
        
        mes = horario.getMonth(); 
    	
        anio = horario.getYear();
        calIndiv.init(mes, anio);
        
        String calendarId = "pmuq213v773gss2h5vc67vgqag@group.calendar.google.com";
        SortedSet<ScheduleDay> dias = horario.getDays();
        Iterator<ScheduleDay> iterator = dias.iterator();
        
        while (iterator.hasNext()) {
        	ScheduleDay dia = iterator.next();
        	Integer numDia = dia.getDay();
        	Set<Doctor> ciclicasDr = dia.getCycle();
        	Set<Doctor> consultasDr = dia.getConsultations();
        	Set<Doctor> turnosDr = dia.getShifts();
        	
			if (ciclicasDr.size()>0) {
				Event evento = creaEvento(numDia,cycle,ciclicasDr);
				calendario.events().insert(calendarId,evento ).setSendUpdates("none").execute();
			}
			
			
			if (turnosDr.size()>0) {
				Event evento=creaEvento( numDia,shifts,turnosDr);
				calendario.events().insert(calendarId, evento).setSendUpdates("none").execute();
			}
			
			if (consultasDr.size()>0) {
				Event evento = creaEvento(numDia,consultation,consultasDr);
				calendario.events().insert(calendarId, evento ).setSendUpdates("none").execute();
			}
        }
        
        calIndiv.enviaCalendarios();
        
    }



	private Event creaEvento(Integer numDia, String summary, Set<Doctor> doctores) {
		Event event = new Event()
        	    .setSummary(summary);
        	    
		String dia = numDia.toString();
		if (numDia < 10) {
			dia = "0"+numDia.toString();
		}
		
		String month = mes.toString();
		if (mes < 10) {
			month = "0"+mes.toString();
		}
        DateTime DateTime = new DateTime(anio.toString()+"-"+month+"-"+dia);
        
        
        EventDateTime Time = new EventDateTime()
            .setDate(DateTime)
            .setTimeZone("Europe/Madrid");
        event.setStart(Time);       
        event.setEnd(Time);
        event.setColorId(colores.get(summary));
       
        String ID = dia+month+anio.toString()+ ids.get(summary);
        event.setId(ID);
        Iterator<Doctor> iterator = doctores.iterator();
        List<EventAttendee> asistentes = new ArrayList <EventAttendee>();
        
		while(iterator.hasNext()) {
				Doctor doctor = iterator.next();
				calIndiv.addEvent(numDia, summary,doctor);
				String nombre = doctor.getFirstName()+" "+doctor.getLastNames();
				String email =doctor.getEmail();
				
			    EventAttendee attendee = new EventAttendee()
			    		.setEmail(email)
			    		.setDisplayName(nombre);
			    asistentes.add(attendee);
			    
			    //Llamada al creador de calendario individuales
			  
			    }	
        
      
        	event.setAttendees(asistentes);
		return event;
	}


}