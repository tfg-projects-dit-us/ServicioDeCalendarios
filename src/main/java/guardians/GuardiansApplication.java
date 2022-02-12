package guardians;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is the entry point of the application. It will load the context
 * 
 * @author miggoncan
 */
@ComponentScan
@SpringBootApplication
public class GuardiansApplication {
	public static void main(String[] args) {
		// The server will use the UTC timezone
		//TomcatURLStreamHandlerFactory.disable();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(GuardiansApplication.class, args);
	}
}
