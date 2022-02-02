package guardians;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpGetMethod;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.ValidationException;
/**
 * 
 * @author carcohcal
 *
 */
@Service
@Slf4j
public class MetodosCalendario {
	
	@Value("${calendario.user}")
	private String username;
	@Value("${calendario.uri}")
	private String uri;
	@Value("${calendario.psw}")
	private String password;
	@Value("${calendario.tipo.cycle}")
	private  String cycle;
	@Value("${calendario.tipo.consultation}")
	private  String consultation;
	@Value("${calendario.tipo.shifts}")
	private  String shifts;
	


	/**
	 * metodo que devuelve la fecha en formate Date de ical4j
	 * @author carcohcal
	 * @param anio
	 * @param mes
	 * @param numDia
	 * @return
	 */
	public Date fecha (Integer anio, Integer mes, Integer numDia)
	{
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(anio, mes-1, numDia);
		
		return new Date(calendar.getTime());
	}
	/**
	 * 
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParserException
	 */
	public Calendar  getCalendario () throws ClientProtocolException, IOException, ParserException {
		CalDAV4JMethodFactory factory = new CalDAV4JMethodFactory();
		HttpGetMethod method = factory.createGetMethod(uri);
		
		CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(
		        AuthScope.ANY,
		        new UsernamePasswordCredentials(username, password)
		);
		
		HttpClient client = HttpClientBuilder.create()
		.setDefaultCredentialsProvider(provider)
		.disableAuthCaching()
		.build();

		log.info("Cliente HTTP creado: "+client);
		
		// Execute the method.
		HttpResponse response = client.execute(method);
		Calendar calendario = null;
		if (response.getStatusLine().getStatusCode() == 404)
		{
			calendario = new Calendar();
			calendario.getProperties().add(new ProdId("-//Calendario Guardias//"));
			calendario.getProperties().add(Version.VERSION_2_0);
			calendario.getProperties().add(CalScale.GREGORIAN);
			
			TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();

			
			VTimeZone tz = registry.getTimeZone("Europe/Madrid").getVTimeZone();
			calendario.getComponents().add(tz);


		}else {
			// Retrieve the Calendar from the response.

			HttpEntity entity = response.getEntity();
			
			
			// Read the contents of an entity and return it as a String.
			String content = EntityUtils.toString(entity);
			
			
			StringReader stream = new StringReader(content) ;
			
			CalendarBuilder builder = new CalendarBuilder();
			
			 calendario = builder.build(stream);
		}
		log.info("Calendario recuperado");
		return calendario;
	}
	/**
	 * 
	 * @param calendario
	 * @param nomFich
	 * @throws ValidationException
	 * @throws IOException
	 */
	public void generaFichero(Calendar calendario, String nomFich) throws ValidationException, IOException {
		
		FileOutputStream fout;
		CalendarOutputter outputter = new CalendarOutputter();	
		outputter.setValidating(false);
		fout = new FileOutputStream(nomFich);
		outputter.output(calendario, fout);	
		log.info("Fichero "+nomFich+" creado"); 
		fout.close();		
		 
		
		
	}
	
	
}
