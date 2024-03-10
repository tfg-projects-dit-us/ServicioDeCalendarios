package guardians;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;


@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
	@Value("${auth.username}")
	private String username;
	@Value("${auth.password}")
	private String password;
	
	
	@Bean
	UserDetailsService userDetailsService(BCryptPasswordEncoder encoder) {

		// codifico las password en https://bcrypt-generator.com/, uso nombre como
		// password
		// $2a$12$Pa3IIDS5JhAJpiLt5/lT4O5KVw1pyU.dVGpz/q7kEGUAH.JL85tRC
		UserDetails user = User.withUsername("user").password(encoder.encode("user")).roles("kie-server").build();
		// $2a$12$irR0VcP4SdtvAn7cbnXXQ.Cnfk/NlLWZa4mnx0J8EeXFum8Pt1pfm
		UserDetails wbadmin = User.withUsername("wbadmin").password(encoder.encode("wbadmin")).roles("admin").build();
		//Este usuario se va a utilizar para el acceso al servidor
		UserDetails consentimientos = User.withUsername("consentimientos").password(encoder.encode("consentimientos")).roles("kie-server").build();
		// $2a$12$1T7IYm0PmxpWyJFjqTSlm.489.s65TvHJbW4R7d1SG0giNHb5bqAm
		UserDetails kieserver = User.withUsername(username).password(encoder.encode(password)).roles("USER").build();
		UserDetails jbpm = User.withUsername("jbpm").password(encoder.encode("jbpm.2.DDBB*")).roles("jbpm").build();

		return new InMemoryUserDetailsManager(wbadmin, user, kieserver, consentimientos);
	}

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO allow the automatic CSRF configuration
        // This implies that access to PUT, POST and DELETE methods has to be granted
    	/*
    	http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
    			.requestMatchers("*")
    			.authenticated());				
		return http.build(); 	
		*/
		http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				.requestMatchers("/*").authenticated())
				
				.csrf((csrf) -> csrf.disable()).httpBasic(withDefaults()).cors(withDefaults())
				.formLogin(withDefaults());
		return http.build();
    	
    	/*
        http.csrf(csrf -> csrf.disable());
        http
//			.requiresChannel()
//				.anyRequest().requiresSecure()
//				.and()
                .authorizeRequests(requests -> requests
//				.anyRequest().permitAll()
                        .anyRequest().authenticated());
        return http.build();
        */
	}
    @Bean
	CorsConfigurationSource corsConfigurationSource() {
		 UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        CorsConfiguration corsConfiguration = new CorsConfiguration();
	        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
	        corsConfiguration.setAllowCredentials(true);
	        corsConfiguration.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.HEAD.name(),
	                                                          HttpMethod.POST.name(), HttpMethod.DELETE.name(), HttpMethod.PUT.name()));
	        corsConfiguration.applyPermitDefaultValues();
	        source.registerCorsConfiguration("/**", corsConfiguration);
	        return source;
	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

