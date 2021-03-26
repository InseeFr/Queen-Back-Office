package fr.insee.queen.api.authKeycloak;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Container;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import fr.insee.queen.api.constants.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public abstract class TestAuthKeycloak {

	public static final String CLIENT_SECRET = "8951f422-44dd-45b4-a6ac-dde6748075d7";
	public static final String CLIENT = "client-web";

	public static ClientAndServer clientAndServer;
	public static MockServerClient mockServerClient;
	
	private ObjectMapper objectMapper = new ObjectMapper();

	@LocalServerPort
	protected int port;
	
	@Container
	public static KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile("realm.json");
	
	@BeforeAll
	public static void init() throws JSONException {
		
		String expectedBody = "{" + "\"habilitated\": true" + "}";
		clientAndServer = ClientAndServer.startClientAndServer(8081);
		mockServerClient = new MockServerClient("127.0.0.1", 8081);
		mockServerClient.when(request().withPath(Constants.API_HABILITATION))
				.respond(response().withStatusCode(200)
						.withHeaders(new Header("Content-Type", "application/json; charset=utf-8"),
								new Header("Cache-Control", "public, max-age=86400"))
						.withBody(expectedBody));
	
		expectedBody = "["
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
		mockServerClient.when(request()
		        .withPath(Constants.API_PEARLJAM_SURVEY_UNITS))
		    .respond(response()
	    		.withStatusCode(200)
	    		.withHeaders(
		            new Header("Content-Type", "application/json; charset=utf-8"),
		            new Header("Cache-Control", "public, max-age=86400"))
		        .withBody(expectedBody));
	}
	
	@BeforeEach
	public void setUp() throws JSONException, JsonMappingException, JsonProcessingException {
		RestAssured.port = port;
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().post("api/createDataSet");
	}

	/***
	 * This method retreive the access token of the keycloak client
	 * 
	 * @param clientId
	 * @param clientSecret
	 * @param username
	 * @param password
	 * @return
	 * @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public String resourceOwnerLogin(String clientId, String clientSecret, String username, String password)
			throws JSONException, JsonMappingException, JsonProcessingException {
		Response response = given().auth().preemptive().basic(clientId, clientSecret)
				.formParam("grant_type", "password").formParam("username", username).formParam("password", password)
				.when().post(keycloak.getAuthServerUrl() + "/realms/insee-realm/protocol/openid-connect/token");
		JsonNode JsonNode = objectMapper.readTree(response.getBody().asString());
		String accessToken = JsonNode.get("access_token").textValue();
		return accessToken;
	}

	/**
	 * Test that the GET endpoint "api/campaigns" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	void testFindCampaign() throws InterruptedException, JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaigns").then().statusCode(200).and().assertThat()
				.body("id", hasItem("simpsons2020x00"));

	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/questionnaire" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	void testFindQuestionnaireByCampaign() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/simpsons2020x00/questionnaire").then()
				.statusCode(200).and().assertThat().body("isEmpty()", Matchers.is(false));
	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/questionnaire" return 404 with
	 * wrong questionnaire Id
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	void testFindQuestionnaireByUnexistCampaign() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/test/questionnaire").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/survey-units" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	void testFindSurveyUnitsByOperation() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/simpsons2020x00/survey-units").then()
				.statusCode(200).and().assertThat().body("id", hasItem("11"));

	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/questionnaire" return 404 with
	 * wrong questionnaire Id
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	void testFindSurveyUnitsByUnexistCampaign() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/test/survey-units").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/nomenclature/{id}" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	void testFindNomenclatureById() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/nomenclature/cities2019").then().statusCode(200).and()
				.assertThat().body("isEmpty()", Matchers.is(false));
	}

	/**
	 * Test that the GET endpoint "api/nomenclature/{id}" return 404 with wrong
	 * nomenclature Id
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	void testFindUnexistNomenclatureById() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/nomenclature/test").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/comment" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindCommentBySurveyUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/22/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+",""), objectMapper.createObjectNode().toString());
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/comment" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	void testGetSurveyUnitComment() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/11/comment");
		response.then().statusCode(200);
		Assert.assertEquals("{\"COMMENT\":\"acomment\"}", response.getBody().asString().replaceAll("\\s+",""));
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/comment" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutCommentBySurveyUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		
		ObjectNode comment = objectMapper.createObjectNode();
		comment.put("comment", "value");

		with().contentType(ContentType.JSON).body(comment.toString()).given().auth().oauth2(accessToken).when()
				.put("api/survey-unit/21/comment").then().statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/21/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+",""), comment.toString());
	}

	/**
	 * Test that the PUT endpoint "api/reporting-unit/{id}/data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutDataByReportingUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		ObjectNode data = objectMapper.createObjectNode();
		data.put("data", "value");

		with().contentType(ContentType.JSON).body(data.toString()).given().auth().oauth2(accessToken).when()
				.put("api/survey-unit/21/data").then().statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/21/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+",""), data.toString());
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/comment" return 404 with
	 * wrong survey-unit Id
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindCommentByUnexistSurveyUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/survey-unit/test/comment").then().statusCode(404);
		given().auth().oauth2(accessToken).when().get("api/survey-unit/0/comment").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindDataBySurveyUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/22/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+",""), objectMapper.createObjectNode().toString());
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/data" return 404 with wrong
	 * survey-unit Id
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindDataByUnexistSurveyUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/survey-unit/test/data").then().statusCode(404);
		given().auth().oauth2(accessToken).when().get("api/survey-unit/0/data").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitData() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/11/data");
		response.then().statusCode(200);
		Assert.assertEquals("{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}}", response.getBody().asString().replaceAll("\\s+",""));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/required-nomenclature" return
	 * 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindRequiredNomenclatureByCampaign() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/vqs2021x00/required-nomenclatures").then()
				.statusCode(200).and().assertThat().body("$", hasItem("french cities 2019"));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/questionnaire-id" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindQuestionnaireIdByCampaign() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/campaign/simpsons2020x00/questionnaire-id");
		response.then().statusCode(200);
		Assert.assertEquals("[{\"questionnaireId\"" + ":" + "\"simpsons\"}]", response.getBody().asString().replaceAll("\\s+",""));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/questionnaire-id" return 404
	 * with wrong campaign Id
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindQuestionnaireIdByUnexistCampaign() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/campaign/test/questionnaire-id").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/personalization" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitPersonalization() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/11/personalization");
		response.then().statusCode(200);
		Assert.assertEquals("[{\"name\":\"whoAnswers1\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}]", 
				response.getBody().asString().replaceAll("\\s+",""));
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/personalization" return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitPersonalizationNotExists() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/survey-unit/99/personalization").then().statusCode(404);
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/personalization" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutPersonalizationBySurveyUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		with().contentType(ContentType.JSON).body("[{\"name\":\"whoAnswers1\",\"value\":\"MrDupond\"}]").given().auth()
				.oauth2(accessToken).when().put("api/survey-unit/21/personalization").then().statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/21/personalization");
		response.then().statusCode(200);
		Assert.assertEquals("[{\"name\":\"whoAnswers1\",\"value\":\"MrDupond\"}]", response.getBody().asString().replaceAll("\\s+",""));
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/personalization" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutPersonalizationBySurveyUnitNotExists() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		with().contentType(ContentType.JSON).body("[{\"name\":\"whoAnswers1\",\"value\":\"Mr Dupond\"}]").given().auth()
				.oauth2(accessToken).when().put("api/survey-unit/99/personalization").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/state-data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitStateData() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/11/state-data");
		response.then().statusCode(200);
		Assert.assertEquals("{\"state\":\"EXPORTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"}", response.getBody().asString().replaceAll("\\s+",""));
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/state-data" return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitStateDataNotExists() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/survey-unit/99/state-data").then().statusCode(404);
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state-data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutStateDataBySurveyUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		with().contentType(ContentType.JSON).body("{\"state\":\"INIT\",\"currentPage\":\"11\",\"date\":11111111111}")
				.given().auth().oauth2(accessToken).when().put("api/survey-unit/21/state-data").then().statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("api/survey-unit/21/state-data");
		response.then().statusCode(200);
		Assert.assertEquals("{\"state\":\"INIT\",\"date\":11111111111,\"currentPage\":\"11\"}",response.getBody().asString().replaceAll("\\s+",""));
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state-data" return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutStateDataBySurveyUnitNotExists() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		with().contentType(ContentType.JSON).body("{\"state\":\"INIT\",\"currentPage\":\"11\",\"date\":11111111111}")
				.given().auth().oauth2(accessToken).when().put("api/survey-unit/99/state-data").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/deposit-proof" return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitDepositProofNotExists() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/survey-unit/99/deposit-proof").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/deposit-proof" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitDepositProof() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
		given().auth().oauth2(accessToken).when().get("api/survey-unit/11/deposit-proof").then().statusCode(200)
				.header("Content-Type", "application/pdf");
	}
	
	/**
	 * Test that the POST endpoint "api/questionnaire-models"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	public void testPostQuestionnaire() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");

		with()
		.contentType(ContentType.JSON)
		.body("{\"idQuestionnaireModel\":\"testPostQuestionnaire\",\"label\":\"label for testing post questionnaire\", \"requiredNomenclaturesId\":[\"cities2019\"],\"value\":{\"idQuestionnaireModel\":\"testPostQuestionnaire\"}}")
		.given().auth().oauth2(accessToken).when()
		.post("api/questionnaire-models")
		.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("api/questionnaire/testPostQuestionnaire");
		response.then().statusCode(200);
    Assert.assertTrue(response.getBody().asString().replaceAll("\\s+","").contains("testPostQuestionnaire"));
}
	
	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	public void testPostCampaignSurveyUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");

		String postBody = "{\"id\":55,\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXPORTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"},\"questionnaireId\":\"vqs2021x00\"}";
		String respBodyExpected = "{\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXPORTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"}}";
		with()
		.contentType(ContentType.JSON)
		.body(postBody)
		.given().auth().oauth2(accessToken).when()
		.post("/api/campaign/vqs2021x00/survey-unit")
		.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("/api/survey-unit/55");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+",""), respBodyExpected.replaceAll("\\s+",""));
	}
	
	
	/**
	 * Test that the POST endpoint "api/nomenclature"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	public void testPostNomenclature() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");

		with()
		.contentType(ContentType.JSON)
		.body("{\"id\":\"testPostNomenclature\",\"label\":\"label for testing post nomnclature\", \"value\":{\"idNomenclature\":\"testPostNomenclature\"}}")
		.given().auth().oauth2(accessToken).when()
		.post("/api/nomenclature")
		.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("/api/nomenclature/testPostNomenclature");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+","").contains("testPostNomenclature"));
	}
	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@Test
	public void testPostCampaign() throws JSONException, JsonMappingException, JsonProcessingException {
		String accessToken = resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");

		with()
		.contentType(ContentType.JSON)
		.body("{\"id\":\"testPostCampaign\",\"label\":\"label for testing post campaign\",\"metadata\":{}, \"questionnaireModelsIds\":[\"QmWithoutCamp\"]}")
		.given().auth().oauth2(accessToken).when()
		.post("api/campaigns")
		.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken).when().get("api/campaigns");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+","").contains("{\"id\":\"testPostCampaign\",\"questionnaireIds\":[\"QmWithoutCamp\"]}".replaceAll("\\s+",""))); 
	}
	
}
