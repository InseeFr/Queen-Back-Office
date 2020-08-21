package fr.insee.queen.api.noAuth;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasItem;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.ClassRule;
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
import liquibase.Liquibase;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties= {"fr.insee.queen.application.mode = NoAuth"})
@ActiveProfiles({ "test" })
@ContextConfiguration(initializers = { TestNoAuth.Initializer.class})
@Testcontainers
class TestNoAuth {
	
	public Liquibase liquibase;
	
	@LocalServerPort
	int port;

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
	}

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
							"spring.datasource.password=" + postgreSQLContainer.getPassword())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindCampaign() throws InterruptedException {
		get("api/campaigns").then()
			.statusCode(200).and()
			.assertThat().body("id", hasItem("simpsons2020x00"));
		
	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/questionnaire"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindQuestionnaireByCampaign() {
		get("api/campaign/simpsons2020x00/questionnaire").then()
		.statusCode(200).and()
		.assertThat().body("isEmpty()", Matchers.is(false));
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns/{id}/questionnaire"
	 * return 404 with wrong questionnaire Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindQuestionnaireByUnexistCampaign() {
		get("api/campaign/toto/questionnaire").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/survey-units"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindSurveyUnitsByCampaign() {
		get("api/campaign/simpsons2020x00/survey-units").then()
		.statusCode(200).and()
		.assertThat().body("id", hasItem("11"));
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns/{id}/questionnaire"
	 * return 404 with wrong questionnaire Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindSurveyUnitsByUnexistCampaign() {
		get("api/campaign/toto/survey-units").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/nomenclature/{id}"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindNomenclatureById() {
		get("api/nomenclature/cities2019").then()
		.statusCode(200).and()
		.assertThat().body("isEmpty()", Matchers.is(false));
	}
	
	/**
	 * Test that the GET endpoint "api/nomenclature/{id}"
	 * return 404 with wrong nomenclature Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindUnexistNomenclatureById() {
		get("api/nomenclature/toto").then().statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/comment"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindCommentBySurveyUnit() {
		Response response = get("api/survey-unit/22/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), new JSONObject().toJSONString());
	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/comment"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutCommentBySurveyUnit() {
		Map<String,String> putComment = new HashMap<>();
		putComment.put("comment", "value");
		JSONObject comment = new JSONObject(putComment);

		with()
			.contentType(ContentType.JSON)
			.body(comment.toJSONString())
		.put("api/survey-unit/21/comment")
			.then()
			.statusCode(200);
		Response response = get("api/survey-unit/21/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), comment.toJSONString());
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/comment"
	 * return 404 with wrong survey-unit Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindCommentByUnexistSurveyUnit() {
		get("api/survey-unit/toto/comment").then().statusCode(404);
		get("api/survey-unit/0/comment").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/data"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindDataBySurveyUnit() {
		Response response = get("api/survey-unit/22/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), new JSONObject().toJSONString());
	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/data"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutDataBySurveyUnit() {
		Map<String,String> putData = new HashMap<>();
		putData.put("data", "value");
		JSONObject data = new JSONObject(putData);

		with()
			.contentType(ContentType.JSON)
			.body(data.toJSONString())
		.put("api/survey-unit/21/data")
			.then()
			.statusCode(200);
		Response response = get("api/survey-unit/21/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), data.toJSONString());
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/data"
	 * return 404 with wrong survey-unit Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindDataByUnexistSurveyUnit() {
		get("api/survey-unit/toto/data").then().statusCode(404);
		get("api/survey-unit/0/data").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/required-nomenclature"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindRequiredNomenclatureByCampaign() {
		get("api/campaign/vqs2021x00/required-nomenclatures").then()
		.statusCode(200).and()
		.assertThat().body("$", hasItem("cities2019"));
	}
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/required-nomenclature"
	 * return 404 with wrong campaign Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindRequiredNomenclatureByUnexistCampaign() {
		get("api/campaign/toto/required-nomenclatures").then().statusCode(404);
	}

}
