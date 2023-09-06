package fr.insee.queen.api;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;


@SpringBootApplication
@EnableTransactionManagement
@ConfigurationPropertiesScan
@Slf4j
public class QueenApplication {

	public static void main(String[] args) {
		configure(new SpringApplicationBuilder()).build().run(args);
	}

	protected static SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(QueenApplication.class).listeners(new PropertiesLogger());
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

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@EventListener
	public void handleApplicationReady(ApplicationReadyEvent event) {
		log.info("=============== Queen Back-Office has successfully started. ===============");
	}
}
