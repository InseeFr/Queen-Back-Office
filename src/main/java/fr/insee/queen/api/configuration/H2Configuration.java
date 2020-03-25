package fr.insee.queen.api.configuration;


import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
* H2Configuration is the class using to configure the H2 console 
* 
* @author Claudel Benjamin
* 
*/
@Configuration
@Profile("h2")
@ConditionalOnWebApplication(type = Type.SERVLET)
public class H2Configuration {
	/**
	 * The name of Spring application
	 * Generate with the application property spring.application.name
	 */
    @Value("${spring.application.name}")
    private String name;
    
    /**
     * This method set all parameters needed for H2 console (Driver, schema and port) 
     * @return the ServletRegistrationBean configuration for H2 console
     * @see org.springframework.boot.web.servlet.ServletRegistrationBean
     * @since 1.0
     */
    @Bean
    public ServletRegistrationBean<WebServlet> h2Console() {
        String path = "/h2-console/*";
        ServletRegistrationBean<WebServlet> registration = new ServletRegistrationBean<WebServlet>(new WebServlet(), path);
        registration.addInitParameter("0", String.format("%s|org.h2.Driver|jdbc:h2:file:./target/h2db/db;SCHEMA=%s|%s", name, name, name));
        registration.addInitParameter("webAllowOthers", "");
        registration.addInitParameter("webPort", "8082");
        registration.addInitParameter("webAllowOthers", "");
        return registration;
    }
}
