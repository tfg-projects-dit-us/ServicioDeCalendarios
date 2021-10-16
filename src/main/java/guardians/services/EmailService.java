package guardians.services;


import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.mail.*;
import javax.mail.internet.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import javax.activation.*;
/**
 * Esta clase gestiona el envío por email de los archivos
 * 
 * @author carcohcal
 */
@Slf4j
@Service
public class EmailService
{

	@Value("${email.loggin}")
	private  String username;
	@Value("${email.password}")
	private  String password;
	@Value("${email.host}")
	 private  String host;
	@Value("${email.asunto}")
	 private  String asunto;
	@Value("${email.mensaje}")
	 private  String mensaje;
	private final  Properties properties = new Properties();
	private  Session session;
	
	/**Método que configura las propiedades del servicio*/
	public  void init() {
		properties.put("mail.transport.protocol", "smtp");
	    properties.put("mail.smtp.host", host);
	    properties.put("mail.smtp.port", "587");
	    properties.put("mail.smtp.auth", "true");
	    properties.put("mail.smtp.user", username);
	    properties.put("mail.smtp.password", password);
	    properties.put("mail.smtp.starttls.enable", "true");	
		
		log.info("Configuracion servicio email completad");
	}
	
	/** Método que envía los emails de forma asíncrona 
	 * @param emailTo direccion de email del destinatario
	 * @param nombre del fichero a enviar como archivo adjunto */
	@Async
 public void enviarEmail(String emailTo, String nomFich){
	  session = Session.getInstance(properties, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(username, password);
		    }
		});
	try {  
	 
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		message.setRecipients(
	    Message.RecipientType.TO, InternetAddress.parse(emailTo));
		message.setSubject(asunto);
		
		//Texto del mensaje
		MimeBodyPart mimeBodyPart1 = new MimeBodyPart();
		mimeBodyPart1.setContent(mensaje, "text/html");
	  
		//Fichero adjunto
		DataSource source = new FileDataSource(nomFich);  
		MimeBodyPart messageBodyPart2 = new MimeBodyPart(); 
	    messageBodyPart2.setDataHandler(new DataHandler(source));  
	    messageBodyPart2.setFileName(nomFich);  
	    
	    //Envio de ambas partes
	    Multipart multipart = new MimeMultipart();
	    multipart.addBodyPart(mimeBodyPart1);
	    multipart.addBodyPart(messageBodyPart2);  

	    message.setContent(multipart);
	    Transport transport = session.getTransport("smtp");
	    transport.connect();
		transport.sendMessage(message, message.getAllRecipients());
	    transport.close();
	  log.info("Email enviado a: "+emailTo);
	  } catch (AddressException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}