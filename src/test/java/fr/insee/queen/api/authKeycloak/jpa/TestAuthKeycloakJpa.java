package fr.insee.queen.api.authKeycloak.jpa;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import fr.insee.queen.api.authKeycloak.TestAuthKeycloak;
import liquibase.Liquibase;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"fr.insee.queen.application.mode = keycloak", "fr.insee.queen.application.persistenceType=JPA" })
@ActiveProfiles({ "test", "dev" })
@ContextConfiguration(initializers = { TestAuthKeycloakJpa.Initializer.class })
@Testcontainers
class TestAuthKeycloakJpa extends TestAuthKeycloak {

	public Liquibase liquibase;

	@SuppressWarnings("rawtypes")
	@Container
	@ClassRule
	public static PostgreSQLContainer postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
			.withDatabaseName("queen").withUsername("queen").withPassword("queen");

	public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues
					.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
							"spring.datasource.username=" + postgreSQLContainer.getUsername(),
							"spring.datasource.password=" + postgreSQLContainer.getPassword(),
							"keycloak.auth-server-url=" + keycloak.getAuthServerUrl())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
	
	@AfterAll
	public static void  cleanUp() {
		if(postgreSQLContainer!=null) {
			postgreSQLContainer.close();
		}
		if(keycloak!=null) {
			keycloak.close();
		}
		if(mockServerClient!=null) {
			mockServerClient.close();
		}
		if(clientAndServer!=null) {
			clientAndServer.close();
		}
	}
}
