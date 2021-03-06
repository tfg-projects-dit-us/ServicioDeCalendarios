package guardians.services;





import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import org.apache.http.HttpEntity;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpGetMethod;

import guardians.model.entities.Doctor;
import guardians.model.entities.Schedule;
import guardians.model.entities.ScheduleDay;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.Email;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.ValidationException;
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
	@Value("${calendario.uri}")
	private String uri;
	
	private Integer anio;
	private Integer mes;
	@Autowired
	private CalendariosIndivuales calIndiv;
	@Autowired 
	private CalDav caldav;
	
	private HashMap<String, String> ids;

	

    public void init(Schedule schedule){
		this.horario = schedule;
		ids = new HashMap<String, String>();
		ids.put(cycle, "jc");
		ids.put(shifts,"ca");
		ids.put(consultation, "c");
	
		
	}
    
    public void creaCalendario() throws IOException, GeneralSecurityException, InterruptedException, URISyntaxException, ParserException, CalDAV4JException {
    	
    	

CalDAV4JMethodFactory factory = new CalDAV4JMethodFactory();
HttpGetMethod method = factory.createGetMethod(uri);

 

final String username = "usuario";
// Customer secret
final String password = "usuario";


CredentialsProvider provider = new BasicCredentialsProvider();
provider.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials("usuario", "usuario")
);

HttpClient client = HttpClientBuilder.create()
.setDefaultCredentialsProvider(provider)
.disableAuthCaching()
.build();


//GetMethod getMethod = new GetMethod(uri);

// Execute the method.
HttpResponse response = client.execute(method);

// Retrieve the Calendar from the response.

HttpEntity entity = response.getEntity();


// Read the contents of an entity and return it as a String.
String content = EntityUtils.toString(entity);


StringReader stream = new StringReader(content) ;

CalendarBuilder builder = new CalendarBuilder();

Calendar calendario = builder.build(stream);
 
        
        mes = horario.getMonth(); 
    	
        anio = horario.getYear();
       // String prodId = "-//Calendario Guardias"+mes.toString()+anio.toString()+"//iCal4j 1.0//EN";
		//calendario.add(new ProdId(prodId));
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
				calendario.add(evento); 
			}
			
			
			if (turnosDr.size()>0) {
				VEvent evento=creaEvento( numDia,shifts,turnosDr);
				calendario.add(evento);
			}
			
			if (consultasDr.size()>0) {
				VEvent evento = creaEvento(numDia,consultation,consultasDr);
				calendario.add(evento);
			}
			}
        
       calIndiv.enviaCalendarios();
        generaFichero(calendario);
    }



	private VEvent creaEvento(Integer numDia, String summary, Set<Doctor> doctores) throws URISyntaxException {
		 
       
        String ID = numDia.toString()+mes.toString()+anio.toString()+ ids.get(summary);
       
        Iterator<Doctor> iterator = doctores.iterator();
        
        // initialise as an all-day event..
     	LocalDateTime fecha = LocalDateTime.of(anio, mes, numDia, 0, 0);
     		
     	//Evento Individual, necesario para que no haya corrupción de datos
            VEvent event=new VEvent(fecha,fecha.plusHours(24),summary);
         Uid   uid = new Uid(ID);
            event.add(uid);
            
            
		while(iterator.hasNext()) {
				Doctor doctor = iterator.next();
				calIndiv.addEvent(numDia, summary,doctor);
				String nombre = doctor.getFirstName()+" "+doctor.getLastNames();
				String email =doctor.getEmail();
				Attendee asistente =new Attendee();
				asistente.add(CuType.INDIVIDUAL);
				asistente.add(new Cn(nombre));
				
				Email mail = new Email(email);
				
				asistente.add(mail);
				event.add(asistente);
			    }	
		return event;
	}

	
	private void generaFichero(Calendar calendario) {
		
		FileOutputStream fout;
		
		try {
			
			CalendarOutputter outputter = new CalendarOutputter();	
			outputter.setValidating(false);
				  String nomFich = "calendarioGeneral.ics";
				  fout = new FileOutputStream(nomFich);
				  outputter.output(calendario, fout);	
				  
				  caldav.publicarCalendario(calendario);
			
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

}