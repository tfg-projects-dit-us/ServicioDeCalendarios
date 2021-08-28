package guardians.services;


import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import javax.activation.*;
/**
 * Esta clase gestiona el envío por email de los archivos
 * 
 * @author carcohcal
 */

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
	private final  Properties prop = new Properties();
	private  Session session;
	
	/**Método que configura las propiedades del servicio*/
	public  void init() {
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", host);
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.ssl.trust", host);		
		
	}
	
	/** Método que envía los emails de forma asíncrona 
	 * @param emailTo direccion de email del destinatario
	 * @param nombre del fichero a enviar como archivo adjunto */
	@Async
 public void enviarEmail(String emailTo, String nomFich){
	  session = Session.getInstance(prop, new Authenticator() {
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

	  Transport.send(message);} catch (AddressException e) {
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