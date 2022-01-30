package guardians.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.mnode.ical4j.serializer.JCalMapper;
import org.mnode.ical4j.serializer.JCalSerializer;
import org.mnode.ical4j.serializer.jscalendar.JSEventBuilder;
import org.mnode.ical4j.serializer.jscalendar.JSEventSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.caldav4j.exceptions.CalDAV4JException;

import antlr.debug.Event;
import guardians.MetodosCalendario;
import guardians.controllers.exceptions.EventNotFoundException;
import guardians.model.dtos.general.CalendarPublicDTO;
import guardians.services.CalDav;
import guardians.services.CalendariosIndivuales;
import guardians.services.calendarioGeneral;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;


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
	private calendarioGeneral servicioCalendario;
	@Autowired
	private MetodosCalendario metodos;
/**
 * 
 * @param yearMonth
 * @param json
 * @return 
 * @throws ClientProtocolException
 * @throws IOException
 * @throws ParserException
 * @throws CalDAV4JException
 * @throws ConstraintViolationException
 * @throws URISyntaxException 
 * @throws EventNotFoundException 
 */
	@PostMapping("/update")
	public String updateEvent(@RequestBody String eventos	) throws ClientProtocolException, IOException, ParserException, CalDAV4JException, ConstraintViolationException, URISyntaxException, EventNotFoundException {
		log.info("Método PUT evento");
		
		Calendar calendario = StringaCal(eventos);
		log.debug("Calendario creado ok");
		return servicioCalendario.modficarCalendario(calendario);
		
		}
	
 /**
  * Método para convertir un calendario ical4j a json	
  * @param calendar
  * @return
  * @throws JsonProcessingException
  */
	
	private String ICALaJSON(Calendar calendar) throws JsonProcessingException {
		
		SimpleModule module = new SimpleModule();
		module.addSerializer(Calendar.class, new JCalSerializer(Calendar.class));
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		
		String serialized = mapper.writeValueAsString(calendar);	
		return serialized;
		
	}
	/**
	 * 
	 * @param json del calendario
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	private Calendar JSONaIcal(String json) throws JsonMappingException, JsonProcessingException {
		
		JSONArray array = new JSONArray(json);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Calendar.class, new JCalMapper(Calendar.class));
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		Calendar calendar = mapper.readValue(json, Calendar.class);
		return calendar;
	}
	
	/**
	 * 
	 * @param content es un String que contiene los eventos/calendario en formato ical
	 * @return
	 * @throws IOException
	 * @throws ParserException
	 */
	
	 private Calendar StringaCal(String content) throws IOException, ParserException {
		
			
			StringReader stream = new StringReader(content) ;
			
			CalendarBuilder builder = new CalendarBuilder();
			
			 Calendar calendario = builder.build(stream);
		 return calendario;
	 }
}
