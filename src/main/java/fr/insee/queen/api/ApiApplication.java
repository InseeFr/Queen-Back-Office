package fr.insee.queen.api;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.client.RestTemplate;

import fr.insee.queen.api.service.DataSetInjectorService;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication(scanBasePackages = "fr.insee.queen.api")
@EnableJpaRepositories(basePackages = "fr.insee.queen.api.repository")
public class ApiApplication extends SpringBootServletInitializer{
	private static final Logger LOG = LoggerFactory.getLogger(ApiApplication.class);

	@Autowired
    private DataSetInjectorService injector;
	
	@Value("${spring.profiles.active}")
    private String profile;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ApiApplication.class);
		app.run(args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		setProperties(); 
		return application.sources(ApiApplication.class);
	}
	
	public static void setProperties() {
		System.setProperty("spring.config.location",
				"classpath:/,"
        + "file:///${catalina.base}/webapps/queenbo.properties");

    System.setProperty("spring.config.additional-location",
				"classpath:/,"
        + "file:///${catalina.base}/webapps/colmcolb.properties");

	}

	@EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final Environment env = event.getApplicationContext().getEnvironment();
        LOG.info("================================ Properties =================================");
        final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
        StreamSupport.stream(sources.spliterator(), false)
                .filter(EnumerablePropertySource.class::isInstance)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
                .filter(prop -> prop.startsWith("fr.insee") || prop.startsWith("logging") || prop.startsWith("keycloak") || prop.startsWith("spring") || prop.startsWith("application"))
                .sorted()
                .forEach(prop -> LOG.info("{}: {}", prop, env.getProperty(prop)));
        LOG.info("============================================================================");
    }
	
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
	    if(profile.contains("dev") && !profile.contains("test")) {
	    	injector.createDataSet();
	    }
	}



	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setResultsMapCaseInsensitive(true);
		return jdbcTemplate;
	}

	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
