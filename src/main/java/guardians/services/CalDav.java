package guardians.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpPutMethod;
import com.github.caldav4j.model.request.CalendarRequest;

import guardians.MetodosCalendario;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.predicate.PeriodRule;
import net.fortuna.ical4j.filter.predicate.PropertyEqualToRule;
import net.fortuna.ical4j.filter.predicate.PropertyExistsRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Uid;

/**
 * Esta clase se encarga de interactuar con el servidor CalDAV 
 * 
 * @author carcohcal
 */
@SuppressWarnings("deprecation")
@Slf4j
@Service
public class CalDav {
	@Value("${calendario.uri}")
	private String uri;
	@Autowired
	private EmailService emailController;
	@Autowired
	private MetodosCalendario metodos;
	

	/**
	 *  Este método se encarga de publicar el calendario en el servidor
	 * @param calendar
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
public void publicarCalendario(Calendar calendar) throws ClientProtocolException, IOException {

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
	log.debug("Cliente HTTP PUT "+client);
	
	HttpResponse response = client.execute(method); 
		log.info("Respuesta peticion PUT: "+response);
	
}

/**
 *  Este método permite recuperar el calendario de un doctor para un mes concreto através de su email
 * @param mesAnio
 * @param email
 * @throws IOException 
 * @throws URISyntaxException 
 * @throws CalDAV4JException 
 * @throws ParserException 
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public boolean recuperarCalendario(YearMonth mesAnio, String email) throws IOException, URISyntaxException, ParserException, CalDAV4JException {


	
	// Retrieve the Calendar from the response.
	metodos.init();
	Calendar calendar = metodos.getCalendario();
	boolean existe = false;
	
	log.info("Fecha: "+mesAnio);
	DateTime start = new DateTime();
	start.setTime(metodos.fecha(mesAnio.getYear(), mesAnio.getMonthValue(), 1).getTime());
	YearMonth yearMonthObject = YearMonth.of(mesAnio.getYear(), mesAnio.getMonthValue());
	int daysInMonth = yearMonthObject.lengthOfMonth();
	DateTime duration=  new DateTime();
	duration.setTime(metodos.fecha(mesAnio.getYear(), mesAnio.getMonthValue(), daysInMonth+1).getTime());
	net.fortuna.ical4j.model.Period period = new net.fortuna.ical4j.model.Period(start,  duration);
	Filter filter = new Filter(new PeriodRule(period));
	Collection eventsToday = filter.filter(calendar.getComponents(Component.VEVENT));
	
	 	
	Attendee a1 = new Attendee(new URI("mailto:"+email));
	 Predicate<Component> attendee1RuleMatch = new PropertyEqualToRule<>(a1);
	Filter<CalendarComponent> filtro = new Filter<CalendarComponent>(new Predicate[] { attendee1RuleMatch}, Filter.MATCH_ALL);
	
	Collection eventosDoctor = filtro.filter(eventsToday);		
	if(eventosDoctor.size()!=0) {
		
		emailController.init();
		String	nomFich = "calendario"+mesAnio.toString()+".ics";
		
		Calendar calendarioDoctor = new Calendar();
		Iterator  iterador = eventosDoctor.iterator();
		while(iterador.hasNext()) {
			VEvent evento = (VEvent) iterador.next();
			PropertyList<Property> asistentes = evento.getProperties(Property.ATTENDEE);
			
			for(int i=0; i<asistentes.size(); i++) {
				evento.getProperties().remove(asistentes.get(i));
				log.info("control");
			}
			calendarioDoctor.getComponents().add(evento );
		}
		metodos.generaFichero(calendarioDoctor, nomFich);
		
		log.info("Fichero creaado con nombre "+nomFich);
		
		//Se envia por email el calendario individual
		emailController.enviarEmail(email, nomFich);
		existe=true;
	}
	return existe;
}


/**
 * Esta función recupera el evento a modificar
 *  @author carcohcal
 *  
 * @return
 * @throws IOException 
 * @throws ClientProtocolException 
 * @throws CalDAV4JException 
 * @throws ParserException 
 * @throws ConstraintViolationException 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public Calendar getEvento() throws ClientProtocolException, IOException, ParserException, CalDAV4JException, ConstraintViolationException {
	
	
	// Retrieve the Calendar from the response.
	metodos.init();
		Calendar calendar = metodos.getCalendario();
	
		
	Uid   uid = new Uid("1052022jc");
    
	PropertyExistsRule eventRuleMatch = new PropertyExistsRule(uid);
	Filter filtro = new Filter<CalendarComponent>(new Predicate[] { eventRuleMatch}, Filter.MATCH_ALL);
	
	
	Collection eventosDoctor = filtro.filter(calendar.getComponents(Component.VEVENT));
	
	 Calendar calendarioDoctor = calendar;
	
	 
	// calendarioDoctor.add(id);
	/*	Iterator  iterador = eventosDoctor.iterator();
		while(iterador.hasNext()) {
			VEvent evento = (VEvent) iterador.next();
			calendarioDoctor.add(evento );
		}*/
	
	
	return calendarioDoctor;
}
}
