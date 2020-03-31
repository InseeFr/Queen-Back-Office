package fr.insee.queen.api;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.hasItem;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.restassured.RestAssured;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "test" })
@ContextConfiguration(initializers = { ApiApplicationRestAssureTests.Initializer.class })
@Testcontainers
class ApiApplicationRestAssureTests {
	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
	}

	@Container
	public static PostgreSQLContainer postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
			.withDatabaseName("queen").withUsername("queen").withPassword("queen");

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues
					.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
							"spring.datasource.username=" + postgreSQLContainer.getUsername(),
							"spring.datasource.password=" + postgreSQLContainer.getPassword())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}

	@Test
	public void testFindOperation() throws InterruptedException {
		get("/operations").then().assertThat().body("id", hasItem("simpsons2020x00"));
	}

	@Test
	public void testFindQuestionnaireByOperation() {
		get("/operation/simpsons2020x00/questionnaire").then().assertThat().body("isEmpty()", Matchers.is(false));
	}

	@Test
	public void testFindReportUnitsByOperation() {
		get("/operation/simpsons2020x00/reporting-units").then().assertThat().body("id", hasItem(11));
	}

	
	@Test
	public void testFindNomenclatureById() {
		get("/nomenclature/cities2019").then().assertThat().body("isEmpty()", Matchers.is(false));
	}
	
	@Test
	public void testFindCommentByReportingUnit() {
		get("/reporting-unit/22/comment").then().assertThat().body("isEmpty()", Matchers.is(false));
	}

	@Test
	public void testFindDataByReportingUnit() {
		get("/reporting-unit/22/data").then().assertThat().body("isEmpty()", Matchers.is(false));
	}

	@Test
	public void testFindRequiredNomenclatureByOperation() {
		get("/operation/vqs2021x00/required-nomenclatures").then().assertThat().body("$", hasItem("cities2019"));
	}

}
