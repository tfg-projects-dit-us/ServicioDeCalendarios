package guardians.services;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import com.google.api.services.gmail.model.Message;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
/**
 * Esta clase gestiona el envío por email de los archivos
 * 
 * @author carcohcal
 */

@SuppressWarnings("deprecation")
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
	
	    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
	    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	    private static final String TOKENS_DIRECTORY_PATH = "tokensGmail";
	    private Gmail service;

	    /**
	     * Global instance of the scopes required by this quickstart.
	     * If modifying these scopes, delete your previously saved tokens/ folder.
	     */
	    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
	    private static final String CREDENTIALS_FILE_PATH = "/credenciales.json";

	    /**
	     * Creates an authorized Credential object.
	     * @param HTTP_TRANSPORT The network HTTP Transport.
	     * @return An authorized Credential object.
	     * @throws IOException If the credentials.json file cannot be found.
	     */
	    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
	        // Load client secrets.
	        InputStream in = EmailService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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
	        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8889).build();
	        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	    }
	    
	    public void init() {
	    	// Build a new authorized API client service.
	        NetHttpTransport HTTP_TRANSPORT;
			try {
				HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
				  service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
			                .setApplicationName(APPLICATION_NAME)
			                .build();
			} catch (GeneralSecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      
	    }

	/** Método que envía los emails de forma asíncrona 
	 * @param emailTo direccion de email del destinatario
	 * @param nombre del fichero a enviar como archivo adjunto 
	 * @throws MessagingException 
	 * @throws IOException 
	 * @throws GeneralSecurityException */
	@Async
 public void enviarEmail(String emailTo, String nomFich) throws MessagingException, GeneralSecurityException, IOException{

		
	    // Print the labels in the user's account.
        String user = "me";
       
        
        File file = new File (nomFich);
		MimeMessage email = createEmailWithAttachment(emailTo, username,asunto, mensaje, file);
	
		Message message =	sendMessage( service, user, email);
		
		System.out.println("MENSAJE ENVIADO: "+message);
		
   }
	

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to Email address of the receiver.
     * @param from Email address of the sender, the mailbox account.
     * @param subject Subject of the email.
     * @param bodyText Body text of the email.
     * @param file Path to the file to be attached.
     * @return MimeMessage to be used to send email.
     * @throws MessagingException
     */
    public static MimeMessage createEmailWithAttachment(String to,
                                                        String from,
                                                        String subject,
                                                        String bodyText,
                                                        File file)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);

        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(file.getName());

        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        return email;
    }
    
    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }


    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    public static Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }

}