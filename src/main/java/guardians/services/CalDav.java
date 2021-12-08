package guardians.services;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpGetMethod;
import com.github.caldav4j.methods.HttpPutMethod;
import com.github.caldav4j.model.request.CalendarRequest;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.HasPropertyRule;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.validate.ValidationException;
/**
 * Esta clase se encarga de interactuar con el servidor CalDAV 
 * 
 * @author carcohcal
 */
@Slf4j
@Service
public class CalDav {
	@Value("${calendario.uri}")
	private String uri;
	@Autowired
	private EmailService emailController;

	/**
	 *  Este método se encarga de publicar el calendario en el servidor
	 * @param calendar
	 */
public void publicarCalendario(Calendar calendar) {

	CalendarRequest cr = new CalendarRequest(calendar);
	CalDAV4JMethodFactory factory = new CalDAV4JMethodFactory();
	HttpPutMethod method = factory.createPutMethod(uri, cr);

	
	CredentialsProvider provider = new BasicCredentialsProvider();
	provider.setCredentials(
	        AuthScope.ANY,
	        new UsernamePasswordCredentials("usuario", "usuario")
	);

	HttpClient client = HttpClientBuilder.create()
	.setDefaultCredentialsProvider(provider)
	.disableAuthCaching()
	.build();
	log.info("Cliente HTTP PUT "+client);
	
	HttpResponse response;
	try {
		
		response = client.execute(method); 
		log.info("Respuesta peticion PUT: "+response);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  
}

/**
 *  Este método permite recuperar el calendario de un doctor para un mes concreto através de su email
 * @param mesAnio
 * @param email
 */

@SuppressWarnings({ "deprecation", "rawtypes" })
public void recuperarCalendario(YearMonth mesAnio, String email) {


	Calendar calendar = null;
	CalDAV4JMethodFactory factory = new CalDAV4JMethodFactory();
	HttpGetMethod method = factory.createGetMethod(uri);
	CredentialsProvider provider = new BasicCredentialsProvider();
	provider.setCredentials(
	        AuthScope.ANY,
	        new UsernamePasswordCredentials("usuario", "usuario")
	);

	HttpClient client = HttpClientBuilder.create()
	.setDefaultCredentialsProvider(provider)
	.disableAuthCaching()
	.build();
	log.info("Cliente HTTP GET "+client);
	// Execute the method.
	HttpResponse response;
	try {
		response = client.execute(method);
		log.info("Respuesta peticion GET: "+response);
		// Retrieve the Calendar from the response.
		calendar = method.getResponseBodyAsCalendar(response);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParserException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (CalDAV4JException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	log.info("Fecha: "+mesAnio);
	LocalDateTime today = LocalDateTime.of(mesAnio.getYear(), mesAnio.getMonth(), 1, 0, 0);
	TemporalAmount duration=  Period.ofMonths(1);
	net.fortuna.ical4j.model.Period period = new net.fortuna.ical4j.model.Period(today,  duration);
	Filter filter = new Filter(new PeriodRule(period));
	Collection eventsToday = filter.filter(calendar.getComponents(Component.VEVENT));
	
	String nomFich = null;
	try {
		Attendee a1 = new Attendee(new URI("mailto:"+email));
		 Predicate<CalendarComponent> attendee1RuleMatch = new HasPropertyRule(a1);
		Filter filtro = new Filter<CalendarComponent>(new Predicate[] { attendee1RuleMatch}, Filter.MATCH_ALL);
		
		Collection eventosDoctor = filtro.filter(eventsToday);		
		
		CalendarOutputter outputter = new CalendarOutputter();	
		outputter.setValidating(false);
		emailController.init();
		nomFich = "calendario"+mesAnio.toString()+".ics";
		FileOutputStream fout = new FileOutputStream(nomFich);
		
		  
		 Calendar calendarioDoctor = new Calendar();
		Iterator  iterador = eventosDoctor.iterator();
		while(iterador.hasNext()) {
			VEvent evento = (VEvent) iterador.next();
			calendarioDoctor.add(evento );
		}
		  
		outputter.output(calendarioDoctor, fout);
		log.info("Fichero creaado con nombre "+nomFich);
		  //Se envia por email el calendario individual
		  emailController.enviarEmail(email, nomFich);
		  fout.close();
	} catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
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

public void getEvento() {
	// TODO Auto-generated method stub
	Calendar calendar = null;
	CalDAV4JMethodFactory factory = new CalDAV4JMethodFactory();
	HttpGetMethod method = factory.createGetMethod(uri);
	CredentialsProvider provider = new BasicCredentialsProvider();
	provider.setCredentials(
	        AuthScope.ANY,
	        new UsernamePasswordCredentials("usuario", "usuario")
	);

	HttpClient client = HttpClientBuilder.create()
	.setDefaultCredentialsProvider(provider)
	.disableAuthCaching()
	.build();
	log.info("Cliente HTTP GET "+client);
	// Execute the method.
	HttpResponse response;
	try {
		response = client.execute(method);
		log.info("Respuesta peticion GET: "+response);
		// Retrieve the Calendar from the response.
		calendar = method.getResponseBodyAsCalendar(response);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParserException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (CalDAV4JException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		
	Uid   uid = new Uid("1122021jc");
    
	Predicate<CalendarComponent> eventRuleMatch = new HasPropertyRule(uid);
	Filter filtro = new Filter<CalendarComponent>(new Predicate[] { eventRuleMatch}, Filter.MATCH_ALL);
	
	Collection evento = filtro.filter(calendar.getComponents(Component.VEVENT));
	
	System.out.println(evento);
	
}
}
