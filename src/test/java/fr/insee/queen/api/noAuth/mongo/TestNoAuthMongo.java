package fr.insee.queen.api.noAuth.mongo;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import fr.insee.queen.api.noAuth.TestNoAuth;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties= {"fr.insee.queen.application.mode = NoAuth", 
		"fr.insee.queen.application.persistenceType=MONGODB"})
@ActiveProfiles({ "dev", "test"})
@ContextConfiguration(initializers = { TestNoAuthMongo.Initializer.class})
@Testcontainers
class TestNoAuthMongo extends TestNoAuth{
	
	@LocalServerPort
	int port;
		
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
			mongoDBContainer.stop();
		}
	}
}
