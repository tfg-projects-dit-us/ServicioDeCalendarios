package guardians.controllers;

import java.io.IOException;
import java.time.YearMonth;

import org.apache.http.client.ClientProtocolException;
import org.mnode.ical4j.serializer.JCalSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.caldav4j.exceptions.CalDAV4JException;

import guardians.services.CalDav;
import guardians.services.CalendariosIndivuales;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ConstraintViolationException;


/**
 * The EventController will handle all requests related to  events
 * 
 * @author carmcohe
 */
@RestController
@RequestMapping("/event")
@Slf4j

public class EventControlles {
	
	@Autowired
	private CalDav servicioCalendario;
	/**
	 * This method will handle requests to update an existing {@link CalendariosIndivuales}
	 * 
	 * @param yearMonth the year and month of the calendar to be updated
	 * @throws CalDAV4JException 
	 * @throws ParserException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws ConstraintViolationException 
	 */
	@PutMapping("/{yearMonth}")
	public void updateEvent(@PathVariable YearMonth yearMonth
			) throws ClientProtocolException, IOException, ParserException, CalDAV4JException, ConstraintViolationException {
		log.info("Método PUT evento");
		Calendar calendar = servicioCalendario.getEvento();
		JSON2ical(calendar);
				
		
		}
	
	private void JSON2ical(Calendar calendar) throws JsonProcessingException {
		
		
		SimpleModule module = new SimpleModule();
		module.addSerializer(new JCalSerializer(Calendar.class));
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);

		String serialized = mapper.writeValueAsString(calendar);
		
		System.out.println(serialized);
		log.info("CONTROL");
	}
}
