package fr.insee.queen.api.authKeycloak;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasItem;

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
	 * Test that the GET endpoint "api/operations"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindOperation() throws InterruptedException, JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/operations").then()
			.statusCode(200).and()
			.assertThat().body("id", hasItem("simpsons2020x00"));
		
	}

	/**
	 * Test that the GET endpoint "api/operations/{id}/questionnaire"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindQuestionnaireByOperation() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/operation/simpsons2020x00/questionnaire").then()
		.statusCode(200).and()
		.assertThat().body("isEmpty()", Matchers.is(false));
	}
	
	/**
	 * Test that the GET endpoint "api/operations/{id}/questionnaire"
	 * return 404 with wrong questionnaire Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindQuestionnaireByUnexistOperation() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/operation/toto/questionnaire").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/operations/{id}/reporting-units"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindReportUnitsByOperation() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/operation/simpsons2020x00/reporting-units").then()
		.statusCode(200).and()
		.assertThat().body("id", hasItem("11"));
	}
	
	/**
	 * Test that the GET endpoint "api/operations/{id}/questionnaire"
	 * return 404 with wrong questionnaire Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindReportUnitsByUnexistOperation() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/operation/toto/reporting-units").then().statusCode(404);
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
	 * Test that the GET endpoint "api/reporting-unit/{id}/comment"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindCommentByReportingUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/reporting-unit/22/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), new JSONObject().toString());
	}
	
	/**
	 * Test that the PUT endpoint "api/reporting-unit/{id}/comment"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutCommentByReportingUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Map<String,String> putComment = new HashMap<>();
		putComment.put("comment", "value");
		JSONObject comment = new JSONObject(putComment);

		with()
			.contentType(ContentType.JSON)
			.body(comment.toString())
			.given().auth().oauth2(accessToken).when()
		.put("api/reporting-unit/21/comment")
			.then()
			.statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("api/reporting-unit/21/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), comment.toString());
	}
	
	/**
	 * Test that the GET endpoint "api/reporting-unit/{id}/comment"
	 * return 404 with wrong reporting-unit Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindCommentByUnexistReportingUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/reporting-unit/toto/comment").then().statusCode(404);
		given().auth().oauth2(accessToken).when().get("api/reporting-unit/0/comment").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/reporting-unit/{id}/data"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindDataByReportingUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/reporting-unit/22/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), new JSONObject().toString());
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
		.put("api/reporting-unit/21/data")
			.then()
			.statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("api/reporting-unit/21/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), data.toString());
	}
	
	/**
	 * Test that the GET endpoint "api/reporting-unit/{id}/data"
	 * return 404 with wrong reporting-unit Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindDataByUnexistReportingUnit() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/reporting-unit/toto/data").then().statusCode(404);
		given().auth().oauth2(accessToken).when().get("api/reporting-unit/0/data").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/operation/{id}/required-nomenclature"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindRequiredNomenclatureByOperation() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/operation/vqs2021x00/required-nomenclatures").then()
		.statusCode(200).and()
		.assertThat().body("$", hasItem("cities2019"));
	}
	
	/**
	 * Test that the GET endpoint "api/operation/{id}/required-nomenclature"
	 * return 404 with wrong operation Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindRequiredNomenclatureByUnexistOperation() throws JSONException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/operation/toto/required-nomenclatures").then().statusCode(404);
	}

}
