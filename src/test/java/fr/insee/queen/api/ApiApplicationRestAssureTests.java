package fr.insee.queen.api;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasItem;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.Assert;
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
import io.restassured.http.ContentType;
import io.restassured.response.Response;

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
		get("api/operations").then()
			.statusCode(200).and()
			.assertThat().body("id", hasItem("simpsons2020x00"));
		
	}

	@Test
	public void testFindQuestionnaireByOperation() {
		get("api/operation/simpsons2020x00/questionnaire").then()
		.statusCode(200).and()
		.assertThat().body("isEmpty()", Matchers.is(false));
	}
	
	@Test
	public void testFindQuestionnaireByUnexistOperation() {
		get("api/operation/toto/questionnaire").then().statusCode(404);
	}

	@Test
	public void testFindReportUnitsByOperation() {
		get("api/operation/simpsons2020x00/reporting-units").then()
		.statusCode(200).and()
		.assertThat().body("id", hasItem(11));
	}
	
	@Test
	public void testFindReportUnitsByUnexistOperation() {
		get("api/operation/toto/reporting-units").then().statusCode(404);
	}

	
	@Test
	public void testFindNomenclatureById() {
		get("api/nomenclature/cities2019").then()
		.statusCode(200).and()
		.assertThat().body("isEmpty()", Matchers.is(false));
	}
	
	@Test
	public void testFindUnexistNomenclatureById() {
		get("api/nomenclature/toto").then().statusCode(404);
	}
	
	@Test
	public void testFindCommentByReportingUnit() {
		Response response = get("api/reporting-unit/22/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), new JSONObject().toJSONString());
	}
	
	@Test
	public void testPutCommentByReportingUnit() {
		Map<String,String> putComment = new HashMap<>();
		putComment.put("comment", "value");
		JSONObject comment = new JSONObject(putComment);

		with()
			.contentType(ContentType.JSON)
			.body(comment.toJSONString())
		.put("api/reporting-unit/21/comment")
			.then()
			.statusCode(200);
		Response response = get("api/reporting-unit/21/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), comment.toJSONString());
	}
	
	@Test
	public void testFindCommentByUnexistReportingUnit() {
		get("api/reporting-unit/toto/comment").then().statusCode(400);
		get("api/reporting-unit/0/comment").then().statusCode(404);
	}

	@Test
	public void testFindDataByReportingUnit() {
		Response response = get("api/reporting-unit/22/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), new JSONObject().toJSONString());
	}
	
	@Test
	public void testPutDataByReportingUnit() {
		Map<String,String> putData = new HashMap<>();
		putData.put("data", "value");
		JSONObject data = new JSONObject(putData);

		with()
			.contentType(ContentType.JSON)
			.body(data.toJSONString())
		.put("api/reporting-unit/21/data")
			.then()
			.statusCode(200);
		Response response = get("api/reporting-unit/21/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), data.toJSONString());
	}
	
	@Test
	public void testFindDataByUnexistReportingUnit() {
		get("api/reporting-unit/toto/data").then().statusCode(400);
		get("api/reporting-unit/0/data").then().statusCode(404);
	}

	@Test
	public void testFindRequiredNomenclatureByOperation() {
		get("api/operation/vqs2021x00/required-nomenclatures").then()
		.statusCode(200).and()
		.assertThat().body("$", hasItem("cities2019"));
	}
	
	@Test
	public void testFindRequiredNomenclatureByUnexistOperation() {
		get("api/operation/toto/required-nomenclatures").then().statusCode(404);
	}

}
