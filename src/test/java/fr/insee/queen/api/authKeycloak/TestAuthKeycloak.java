package fr.insee.queen.api.authKeycloak;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasItem;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
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

import dasniko.testcontainers.keycloak.KeycloakContainer;
import fr.insee.queen.api.constants.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import liquibase.Liquibase;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties= {"fr.insee.queen.application.mode = KeyCloak"})
@ActiveProfiles({ "test" })
@ContextConfiguration(initializers = { TestAuthKeycloak.Initializer.class})
@Testcontainers
class TestAuthKeycloak {
	
	public Liquibase liquibase;
	public static final String CLIENT_SECRET = "8951f422-44dd-45b4-a6ac-dde6748075d7";
	public static final String CLIENT = "client-web";
	
	@Container
	public static KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile("realm.json");
	
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
							"spring.datasource.password=" + postgreSQLContainer.getPassword(),
							"keycloak.auth-server-url=" + keycloak.getAuthServerUrl())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
	
	/***
	 * This method retreive the access token of the keycloak client
	 * @param clientId
	 * @param clientSecret
	 * @param username
	 * @param password
	 * @return
	 * @throws JSONException
	 */
	public String resourceOwnerLogin(String clientId, String clientSecret, String username, String password) throws JSONException {
	      Response response =
	              given().auth().preemptive().basic(clientId, clientSecret)   
	                      .formParam("grant_type", "password")
	                      .formParam("username", username)
	                      .formParam("password", password)
	                      .when()
	                      .post( keycloak.getAuthServerUrl() + "/realms/insee-realm/protocol/openid-connect/token");
	      JSONObject jsonObject = new JSONObject(response.getBody().asString());
	      String accessToken = jsonObject.get("access_token").toString();
	      return accessToken;
	   }

	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindCampaign() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaigns").then()
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
	public void testFindQuestionnaireByCampaign() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/simpsons2020x00/questionnaire").then()
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
	public void testFindQuestionnaireByUnexistCampaign() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/toto/questionnaire").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/survey-units"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindSurveyUnitsByOperation() throws JSONException {
		String expectedBody = "["
				+ "{"
					+ "\"id\":\"11\", "
					+ "\"campaign\":\"simpsons2020x00\", "
					+ "\"campaignLabel\":\"Survey on the Simpsons tv show 2020\", "
					+ "\"collectionStartDate\":\"1577836800000\", "
					+ "\"collectionEndDate\":\"1622035845000\""
				+ "}, "
				+ "{"
					+ "\"id\":\"12\", "
					+ "\"campaign\":\"simpsons2020x00\", "
					+ "\"campaignLabel\":\"Survey on the Simpsons tv show 2020\", "
					+ "\"collectionStartDate\":\"1577836800000\", "
					+ "\"collectionEndDate\":\"1622035845000\""
				+ "}, "
				+ "{"
					+ "\"id\":\"20\", "
					+ "\"campaign\":\"vqs2021x00\", "
					+ "\"campaignLabel\":\"Everyday life and health survey 2021\", "
					+ "\"collectionStartDate\":\"1577836800000\", "
					+ "\"collectionEndDate\":\"1622035845000\""
				+ "}]";
		ClientAndServer clientAndServer = ClientAndServer.startClientAndServer(8081);
		MockServerClient mockServerClient = new MockServerClient("127.0.0.1", 8081);
		mockServerClient.when(request()
	        .withPath(Constants.API_PEARLJAM_SURVEY_UNITS))
	    .respond(response()
    		.withStatusCode(200)
    		.withHeaders(
	            new Header("Content-Type", "application/json; charset=utf-8"),
	            new Header("Cache-Control", "public, max-age=86400"))
	        .withBody(expectedBody));
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/simpsons2020x00/survey-units").then()
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
	public void testFindSurveyUnitsByUnexistCampaign() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/toto/survey-units").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/nomenclature/{id}"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindNomenclatureById() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/nomenclature/cities2019").then()
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
	public void testFindUnexistNomenclatureById() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/nomenclature/toto").then().statusCode(404);
	}
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/comment"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindCommentBySurveyUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/22/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), new JSONObject().toString());
	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/comment"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutCommentBySurveyUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Map<String,String> putComment = new HashMap<>();
		putComment.put("comment", "value");
		JSONObject comment = new JSONObject(putComment);

		with()
			.contentType(ContentType.JSON)
			.body(comment.toString())
			.given().auth().oauth2(accessToken).when()
		.put("api/survey-unit/21/comment")
			.then()
			.statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/21/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), comment.toString());
	}
	
	/**
	 * Test that the PUT endpoint "api/reporting-unit/{id}/data"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutDataByReportingUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Map<String,String> putData = new HashMap<>();
		putData.put("data", "value");
		JSONObject data = new JSONObject(putData);

		with()
			.contentType(ContentType.JSON)
			.body(data.toString()).
			given().auth().oauth2(accessToken).when()
		.put("api/survey-unit/21/data")
			.then()
			.statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/21/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), data.toString());
	}
	
	/**
	 * Test that the GET endpoint "api/reporting-unit/{id}/comment"
	 * return 404 with wrong reporting-unit Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindCommentByUnexistSurveyUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/survey-unit/toto/comment").then().statusCode(404);
		given().auth().oauth2(accessToken).when().get("api/survey-unit/0/comment").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/data"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindDataBySurveyUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/22/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), new JSONObject().toString());
	}
	
	
	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/data"
	 * return 404 with wrong survey-unit Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindDataByUnexistSurveyUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/survey-unit/toto/data").then().statusCode(404);
		given().auth().oauth2(accessToken).when().get("api/survey-unit/0/data").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/required-nomenclature"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindRequiredNomenclatureByCampaign() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/vqs2021x00/required-nomenclatures").then()
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
	public void testFindRequiredNomenclatureByUnexistCampaign() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/toto/required-nomenclatures").then().statusCode(404);
	}

}
