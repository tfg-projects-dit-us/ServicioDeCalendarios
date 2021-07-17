package guardians.services;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.caldav4j.methods.HttpPostMethod;
import com.github.caldav4j.methods.HttpPutMethod;
import com.github.caldav4j.model.request.CalendarRequest;

import guardians.controllers.CalendarController;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
@Slf4j
@Service
public class CalDav {
	@Value("${calendario.uri}")
	private String uri;
	public void publicarCalendario(Calendar calendar) {



CalendarRequest cr = new CalendarRequest();
cr.setCalendar(calendar);
cr.setAllEtags(true);
cr.setCharset(Charset.forName("UTF-8"));

cr.addEtag("\"E2\""); // Quoted
cr.setIfMatch(true);
cr.setIfNoneMatch(true);



CalendarOutputter outputter = new CalendarOutputter();
outputter.setValidating(false);

HttpPutMethod method = new HttpPutMethod(uri, cr, outputter);


CredentialsProvider provider = new BasicCredentialsProvider();
provider.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials("usuario", "usuario")
);
CloseableHttpClient client = HttpClientBuilder.create()
.setDefaultCredentialsProvider(provider)
.build();
HttpResponse response;
try {
	
	response = client.execute(method);
	log.info("Respuesta PUT calendario: "+response.getEntity().toString());
	log.info(response.getStatusLine().toString()); 
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}  



	}

}
