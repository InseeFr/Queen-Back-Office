package fr.insee.queen.api.basicAuth.mongo;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import fr.insee.queen.api.basicAuth.TestBasicAuth;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties= {"fr.insee.queen.application.mode = Basic",
		"fr.insee.queen.application.persistenceType=MONGODB"})
@ActiveProfiles({ "test", "dev" })
@ContextConfiguration(initializers = { TestBasicAuthMongo.Initializer.class})
@Testcontainers
class TestBasicAuthMongo extends TestBasicAuth {
		
	@Container
	@ClassRule
	public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10");

	public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues
			.of("spring.data.mongodb.uri=" + mongoDBContainer.getReplicaSetUrl())
			.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
	
	@AfterAll
	public static void  cleanUp() {
		if(mongoDBContainer!=null) {
			mongoDBContainer.close();
		}
		if(mockServerClient!=null) {
			mockServerClient.close();
		}
		if(clientAndServer!=null) {
			clientAndServer.close();
		}
	}
}
