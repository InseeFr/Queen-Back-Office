package fr.insee.queen.api.authKeycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.domain.ParadataEvent;
import fr.insee.queen.api.repository.ParadataEventRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public abstract class TestAuthKeycloak {

	public static final String CLIENT_SECRET = "8951f422-44dd-45b4-a6ac-dde6748075d7";
	public static final String CLIENT = "client-web";

	public static ClientAndServer clientAndServer;
	public static MockServerClient mockServerClient;
	
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	ParadataEventRepository paradataEventRepository;

	@LocalServerPort
	protected int port;
	
	@Container
	public static KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile("realm.json");
	
	@BeforeAll
	static void init() throws JSONException {
		
		String expectedBody = "{" + "\"habilitated\": true" + "}";
		clientAndServer = ClientAndServer.startClientAndServer(8081);
		mockServerClient = new MockServerClient("127.0.0.1", 8081);
		mockServerClient.when(request().withPath(Constants.API_HABILITATION))
				.respond(response().withStatusCode(200)
						.withHeaders(new Header("Content-Type", "application/json; charset=utf-8"),
								new Header("Cache-Control", "public, max-age=86400"))
						.withBody(expectedBody));

		expectedBody = "{" + "\"ongoing\": true" + "}";
		mockServerClient.when(request().withPath("/campaigns/SIMPSONS2020X00/ongoing"))
				.respond(response().withStatusCode(200)
						.withHeaders(new Header("Content-Type", "application/json; charset=utf-8"),
								new Header("Cache-Control", "public, max-age=86400"))
						.withBody(expectedBody));
	
		expectedBody = "["
				+ "{"
					+ "\"id\":\"11\", "
					+ "\"campaign\":\"SIMPSONS2020X00\", "
					+ "\"campaignLabel\":\"Survey on the Simpsons tv show 2020\", "
					+ "\"collectionStartDate\":\"1577836800000\", "
					+ "\"collectionEndDate\":\"1622035845000\""
				+ "}, "
				+ "{"
					+ "\"id\":\"12\", "
					+ "\"campaign\":\"SIMPSONS2020X00\", "
					+ "\"campaignLabel\":\"Survey on the Simpsons tv show 2020\", "
					+ "\"collectionStartDate\":\"1577836800000\", "
					+ "\"collectionEndDate\":\"1622035845000\""
				+ "}, "
				+ "{"
					+ "\"id\":\"20\", "
					+ "\"campaign\":\"VQS2021X00\", "
					+ "\"campaignLabel\":\"Everyday life and health survey 2021\", "
					+ "\"collectionStartDate\":\"1577836800000\", "
					+ "\"collectionEndDate\":\"1622035845000\""
				+ "}]";
		mockServerClient.when(request()
		        .withPath(Constants.API_PEARLJAM_SURVEYUNITS))
		    .respond(response()
	    		.withStatusCode(200)
	    		.withHeaders(
		            new Header("Content-Type", "application/json; charset=utf-8"),
		            new Header("Cache-Control", "public, max-age=86400"))
		        .withBody(expectedBody));
	}
	
	@BeforeEach
	void setUp() throws JSONException, JsonMappingException, JsonProcessingException {
		RestAssured.port = port;
		given().auth().oauth2(accessToken()).when().post("api/create-dataset");
	}
	
	String accessToken() throws JsonMappingException, JSONException, JsonProcessingException {
		return resourceOwnerLogin(CLIENT, CLIENT_SECRET, "INTW1", "a");
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
	String resourceOwnerLogin(String clientId, String clientSecret, String username, String password)
			throws JSONException, JsonMappingException, JsonProcessingException {
		Response response = given().auth().preemptive().basic(clientId, clientSecret)
				.formParam("grant_type", "password").formParam("username", username).formParam("password", password)
				.when().post(keycloak.getAuthServerUrl() + "/realms/insee-realm/protocol/openid-connect/token");
		JsonNode JsonNode = objectMapper.readTree(response.getBody().asString());
		String accessToken = JsonNode.get("access_token").textValue();
		return accessToken;
	}

	//////////////////////////API_CAMPAIGNS ///////////////////////
	/**
	* Test that the GET endpoint "api/campaigns" return 200
	* 
	* @throws InterruptedException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	* @throws JSONException
	*/
	@Test
	void testFindCampaign() throws InterruptedException, JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/admin/campaigns")
				.then().statusCode(200)
				.and().assertThat().body("id", hasItem("SIMPSONS2020X00"));
	}
	
	/**
	* Test that the POST endpoint "api/campaigns" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	* @throws JsonProcessingException 
	* @throws JsonMappingException 
	*/
	@Test
	void testPostCampaign() throws JSONException, JsonMappingException, JsonProcessingException {
		with().contentType(ContentType.JSON)
				.body("{\"id\":\"testPostCampaign\",\"label\":\"label for testing post campaign\",\"metadata\":{}, \"questionnaireIds\":[\"QmWithoutCamp\"]}")
				.given().auth().oauth2(accessToken())
				.post("api/campaigns").then().statusCode(200);
		Response response = given().auth().oauth2(accessToken())
				.get("api/admin/campaigns");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+", "").contains(
		"{\"id\":\"TESTPOSTCAMPAIGN\",\"questionnaireIds\":[\"QmWithoutCamp\"]}".replaceAll("\\s+", "")));
	}
	
	/**
	* Test that the DELETE endpoint "api/campaign/{id}" return 200
	* 
	* @throws InterruptedException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	* @throws JSONException
	*/
	@Test
	void testDeleteOpenedCampaignForcingById() throws InterruptedException, JsonMappingException, JSONException, JsonProcessingException {

		ArrayNode pValue = objectMapper.createArrayNode();
		ObjectNode jsonObject = objectMapper.createObjectNode();
		jsonObject.put("idSU", "11");
		pValue.add(jsonObject);
		ParadataEvent pe = new ParadataEvent(UUID.randomUUID(),jsonObject);
		paradataEventRepository.save(pe);
		UUID id = pe.getId();
		Assert.assertTrue(paradataEventRepository.existsById(id));

		given().auth().oauth2(accessToken())
				.when().delete("api/campaign/SIMPSONS2020X00?force=true")
				.then().statusCode(200);

		Assert.assertFalse(paradataEventRepository.existsById(id));

		given().auth().oauth2(accessToken())
				.when().get("api/admin/campaigns")
				.then().statusCode(200)
				.and().assertThat().body("id", not(hasItem("SIMPSONS2020X00")));


	}

	@Test
	void testDeleteOpenedCampaignUnForcingById() throws InterruptedException, JsonMappingException, JSONException, JsonProcessingException {

		given().auth().oauth2(accessToken())
				.when().delete("api/campaign/SIMPSONS2020X00?force=false")
				.then().statusCode(422);

	}
	
	/**
	* Test that the DELETE endpoint "api/campaign/{id}" return 200
	* 
	* @throws InterruptedException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	* @throws JSONException
	*/
	@Test
	void testDeleteCampaignByUnexistingId() throws InterruptedException, JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().delete("api/campaign/unexistingcampaign?force=false")
				.then().statusCode(404);
	}
	//////////////////////////API_CAMPAIGNS ///////////////////////
	
	
	//////////////////////////	API_CAMPAIGN_CONTEXT //////////////////////////
	/**
	* Test that the POST endpoint "api/campaign/context" return 200
	* 
	* @throws JSONException
	* @throws JsonProcessingException 
	* @throws JsonMappingException 
	*/
	@Test
	void testIntegrateContextCase1() throws JSONException, JsonMappingException, JsonProcessingException {
		File zip = new File(getClass().getClassLoader()
				.getResource("integration//integration_test_case_1.zip").getFile());
		ObjectNode expected = objectMapper.createObjectNode();
		
		ObjectNode campaign = objectMapper.createObjectNode();
		campaign.put("id", "SIMPSONS2020X00");
		campaign.put("status", "UPDATED");
		
		ArrayNode nomenclatures = objectMapper.createArrayNode();
		ObjectNode nomenclature1 = objectMapper.createObjectNode();
		nomenclature1.put("id", "cities20199");
		nomenclature1.put("status", "CREATED");
		nomenclatures.add(nomenclature1);
		ObjectNode nomenclature2 = objectMapper.createObjectNode();
		nomenclature2.put("id", "regions2019");
		nomenclature2.put("status", "ERROR");
		nomenclature2.put("cause", "A nomenclature with this id already exists");
		nomenclatures.add(nomenclature2);
		
		ArrayNode questionnaireModels = objectMapper.createArrayNode();
		ObjectNode questionnaireModel1 = objectMapper.createObjectNode();
		questionnaireModel1.put("id", "simpsons-v1");
		questionnaireModel1.put("status", "CREATED");
		questionnaireModels.add(questionnaireModel1);
		ObjectNode questionnaireModel2 = objectMapper.createObjectNode();
		questionnaireModel2.put("id", "simpson-v2");
		questionnaireModel2.put("status", "ERROR");
		questionnaireModel2.put("cause", "The campaign 'simpsons' does not exist");
		questionnaireModels.add(questionnaireModel2);
		
		expected.set("campaign", campaign);
		expected.set("nomenclatures", nomenclatures);
		expected.set("questionnaireModels", questionnaireModels);
		
		Response resp = with()
				.multiPart("file", zip)
				.given().auth().oauth2(accessToken())
				.post("api/campaign/context");
		resp.then().statusCode(200);
		
		// Response is as expected
		String responseString = objectMapper.readTree(resp.getBody().asString()).toString();
		Assert.assertEquals(expected.toString(), responseString);
		
		// Questionnaire model "simpsons-v1" has been created
		Response resp2 = given().auth().oauth2(accessToken())
				.get("api/campaign/SIMPSONS2020X00/questionnaire-id");
		resp2.then().statusCode(200);
		Assert.assertTrue(resp2.getBody().asString().contains("simpsons-v1"));
		
		// Nomenclature "cities20199" has been created
		given().auth().oauth2(accessToken())
				.when().get("api/nomenclature/cities20199").then().statusCode(200);
	}
	
	/**
	* Test that the POST endpoint "api/campaign/context" return 200
	* 
	* @throws JSONException
	* @throws JsonProcessingException 
	* @throws JsonMappingException 
	*/
	@Test
	void testIntegrateContextCase2() throws JSONException, JsonMappingException, JsonProcessingException {
		File zip = new File(getClass().getClassLoader()
				.getResource("integration//integration_test_case_2.zip").getFile());
		ObjectNode expected = objectMapper.createObjectNode();
		
		ObjectNode campaign = objectMapper.createObjectNode();
		campaign.put("id", "ANOTHERCAMPAIGN");
		campaign.put("status", "CREATED");
		
		ArrayNode nomenclatures = objectMapper.createArrayNode();
		ObjectNode nomenclature1 = objectMapper.createObjectNode();
		nomenclature1.put("id", "nomenclatures.xml");
		nomenclature1.put("status", "ERROR");
		nomenclature1.put("cause", "File nomenclatures.xml does not fit the required template (cvc-elt.1.a: Cannot find the declaration of element 'NomenclatureWithTypo'.)");
		nomenclatures.add(nomenclature1);
		
		ArrayNode questionnaireModels = objectMapper.createArrayNode();
		ObjectNode questionnaireModel1 = objectMapper.createObjectNode();
		questionnaireModel1.put("id", "simpsons-v1");
		questionnaireModel1.put("status", "ERROR");
		questionnaireModel1.put("cause", "Questionnaire model file 'file_that does not exist.json' could not be found in input zip");
		questionnaireModels.add(questionnaireModel1);
		
		expected.set("campaign", campaign);
		expected.set("nomenclatures", nomenclatures);
		expected.set("questionnaireModels", questionnaireModels);
		
		Response resp = with()
				.multiPart("file", zip)
				.given().auth().oauth2(accessToken())
				.post("api/campaign/context");
		resp.then().statusCode(200);
		
		// Response is as expected
		String responseString = objectMapper.readTree(resp.getBody().asString()).toString();
		Assert.assertEquals(expected.toString(), responseString);
		
		// Campaign "ANOTHERCAMPAIGN" has been created
		Response resp2 = given().auth().oauth2(accessToken())
				.get("api/admin/campaigns");
		resp2.then().statusCode(200);
		Assert.assertTrue(resp2.getBody().asString().contains("ANOTHERCAMPAIGN"));	
	}
	//////////////////////////	API_CAMPAIGN_CONTEXT //////////////////////////
	
	
	//////////////////////////	API_CAMPAIGN_ID_SURVEY_UNITS //////////////////////////
	/**
	* Test that the GET endpoint "api/campaigns/{id}/survey-units" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindSurveyUnitsByCampaign() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/campaign/SIMPSONS2020X00/survey-units")
				.then().statusCode(200)
				.and().assertThat().body("id", hasItem("11"));
	}
	/**
	* Test that the GET endpoint "api/campaigns/{id}/survey-units" return 404 with
	* wrong questionnaire Id
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindSurveyUnitsByUnexistCampaign() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/campaign/toto/survey-units")
				.then().statusCode(404);
	}
	//////////////////////////	API_CAMPAIGN_ID_SURVEY_UNITS //////////////////////////
	
	
	//////////////////////////	API_CAMPAIGN_ID_SURVEY_UNIT //////////////////////////
	/**
	* Test that the POST endpoint "/api/campaign/{id}/survey-unit" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	*/
	@Test
	void testPostCampaignSurveyUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		String postBody = "{\"id\":55,\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXTRACTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"},\"questionnaireId\":\"VQS2021X00\"}";
		String respBodyExpected = "{\"questionnaireId\":\"VQS2021X00\",\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXTRACTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"}}";
		with().contentType(ContentType.JSON).body(postBody)
				.given().auth().oauth2(accessToken())
				.post("/api/campaign/VQS2021X00/survey-unit")
				.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken())
				.when().get("/api/survey-unit/55");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+", ""),
		respBodyExpected.replaceAll("\\s+", ""));
	}
/**
	* Test that the POST endpoint "/api/campaign/{id}/survey-unit" with empty StateData return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	*/

	@Test
	void testPostCampaignSurveyUnitWithEmptyStateData() throws JSONException, JsonMappingException, JsonProcessingException {
		String postBody = "{\"id\":73,\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":null,\"date\":null,\"currentPage\":null},\"questionnaireId\":\"VQS2021X00\"}";
		String respBodyExpected = "{\"questionnaireId\":\"VQS2021X00\",\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":null,\"date\":null,\"currentPage\":null}}";
		with().contentType(ContentType.JSON).body(postBody)
				.given().auth().oauth2(accessToken())
				.post("/api/campaign/VQS2021X00/survey-unit")
				.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken())
				.when().get("/api/survey-unit/73");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+", ""),
		respBodyExpected.replaceAll("\\s+", ""));
	}
	

	//////////////////////////API_CAMPAIGN_ID_SURVEY_UNIT //////////////////////////
	
	
	//////////////////////////	API_CAMPAIGN_ID_METADATA //////////////////////////
	/**
	* Test that the GET endpoint "api/campaigns/{id}/questionnaires" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindMetadataByCampaign() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/campaign/VQS2021X00/metadata")
				.then().statusCode(200);
	}
	/**
	* Test that the GET endpoint "api/campaigns/{id}/questionnaires" return 404 with
	* wrong questionnaire Id
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindMetadataByUnexistCampaign() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/campaign/toto/metadata")
				.then().statusCode(404);
	}
	//////////////////////////	API_CAMPAIGN_ID_METADATA //////////////////////////
	
	
	//////////////////////////	API_CAMPAIGN_ID_QUESTIONAIRES //////////////////////////
	/**
	* Test that the GET endpoint "api/campaigns/{id}/questionnaires" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindQuestionnaireByCampaign() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/campaign/SIMPSONS2020X00/questionnaires")
				.then().statusCode(200)
				.and().assertThat().body("isEmpty()",Matchers.is(false));
	}
	/**
	* Test that the GET endpoint "api/campaigns/{id}/questionnaires" return 404 with
	* wrong questionnaire Id
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindQuestionnaireByUnexistCampaign() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/campaign/toto/questionnaires")
				.then().statusCode(404);
	}
	//////////////////////////	API_CAMPAIGN_ID_QUESTIONAIRES //////////////////////////
	
	
	//////////////////////////	API_CAMPAIGN_ID_QUESTIONAIREID //////////////////////////
	/**
	* Test that the GET endpoint "api/campaign/{id}/questionnaire-id" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	*/
	@Test
	void testFindQuestionnaireIdByCampaign() throws JSONException, JsonMappingException, JsonProcessingException {
		Response response = given().auth().oauth2(accessToken())
				.get("api/campaign/SIMPSONS2020X00/questionnaire-id");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+","").contains("{\"questionnaireId\"" + ":" + "\"simpsons\"}"));
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+","").contains("{\"questionnaireId\":\"simpsonsV2\"}"));
	}
	
	/**
	* Test that the GET endpoint "api/campaign/{id}/questionnaire-id" return 404
	* with wrong campaign Id
	* 
	* @throws InterruptedException
	* @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	*/
	@Test
	void testFindQuestionnaireIdByUnexistCampaign() throws JSONException, JsonMappingException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/campaign/test/questionnaire-id")
				.then().statusCode(404);
	}
	//////////////////////////	API_CAMPAIGN_ID_QUESTIONAIREID //////////////////////////
	
	
	//////////////////////////	API_CAMPAIGN_ID_REQUIREDNOMENCLATURES //////////////////////////
	/**
	* Test that the GET endpoint "api/campaign/{id}/required-nomenclature" return
	* 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindRequiredNomenclatureByCampaign() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/campaign/SIMPSONS2020X00/required-nomenclatures")
				.then().statusCode(200)
				.and().assertThat().body("$",hasItem("cities2019"));
	}
	
	/**
	* Test that the GET endpoint "api/campaign/{id}/required-nomenclature" return
	* 404 with wrong campaign Id
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindRequiredNomenclatureByUnexistCampaign() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/campaign/toto/required-nomenclatures")
				.then().statusCode(404);
	}
	//////////////////////////API_CAMPAIGN_ID_REQUIREDNOMENCLATURES //////////////////////////

	//////////////////////////	API_SURVEYUNITS//////////////////////////

	@Test
	void testFindSurveyUnitsIds() throws JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/survey-units")
				.then().statusCode(200)
				.and().assertThat().body("$",hasItem("11"));
	}
	
	//////////////////////////	API_SURVEYUNIT_ID //////////////////////////
	/**
	* Test that the GET endpoint "api/survey-unit/{id}" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindSurveyUnitById() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/11")
				.then().statusCode(200);
	}
	
	/**
	* Test that the GET endpoint "api/survey-unit/{id}" return 404
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindSurveyUnitByUnexistId() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/test")
				.then().statusCode(404);
	}
	
	/**
	* Test that the DELETE endpoint "api/survey-unit/{id}" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testDeleteSurveyUnitById() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().delete("api/survey-unit/21")
				.then().statusCode(200);
	}
	
	/**
	* Test that the DELETE endpoint "api/survey-unit/{id}" return 404
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testDeleteSurveyUnitByUnexistId() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().delete("api/survey-unit/test")
				.then().statusCode(404);
	}
	//////////////////////////	API_SURVEYUNIT_ID //////////////////////////
	
	
	//////////////////////////	API_SURVEYUNIT_ID_DATA //////////////////////////
	/**
	* Test that the GET endpoint "api/survey-unit/{id}/data" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	*/
	@Test
	void testFindDataBySurveyUnit() throws JSONException, JsonMappingException, JsonProcessingException {
		Response response = given().auth().oauth2(accessToken())
				.get("api/survey-unit/11/data");
		response.then().statusCode(200);
		Assert.assertEquals("{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}}",
		response.getBody().asString().replaceAll("\\s+", ""));
	}
	
	/**
	* Test that the PUT endpoint "api/survey-unit/{id}/data" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testPutDataBySurveyUnit() throws JsonMappingException, JSONException, JsonProcessingException {
		ObjectNode data = objectMapper.createObjectNode();
		data.put("data", "value");
		with().contentType(ContentType.JSON)
				.body(data.toString())
				.given().auth().oauth2(accessToken())
				.put("api/survey-unit/21/data")
				.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/21/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+", ""), data.toString());
	}
	
	/**
	* Test that the GET endpoint "api/survey-unit/{id}/data" return 404 with wrong
	* survey-unit Id
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindDataByUnexistSurveyUnit() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/toto/data")
				.then().statusCode(404);
		given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/0/data")
				.then().statusCode(404);
	}
	//////////////////////////	API_SURVEYUNIT_ID_DATA //////////////////////////
	
	
	//////////////////////////	API_SURVEYUNIT_ID_COMMENT //////////////////////////
	/**
	* Test that the GET endpoint "api/survey-unit/{id}/comment" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindCommentBySurveyUnit() throws JsonMappingException, JSONException, JsonProcessingException {
		Response response = given().auth().oauth2(accessToken())
				.get("api/survey-unit/11/comment");
		response.then().statusCode(200);
		Assert.assertEquals("{\"COMMENT\":\"acomment\"}", response.getBody().asString().replaceAll("\\s+", ""));
	}
	
	/**
	* Test that the PUT endpoint "api/survey-unit/{id}/comment" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testPutCommentBySurveyUnit() throws JsonMappingException, JSONException, JsonProcessingException {
		ObjectNode comment = objectMapper.createObjectNode();
		comment.put("comment", "value");
	
		with().contentType(ContentType.JSON)
				.body(comment.toString())
				.given().auth().oauth2(accessToken())
				.put("api/survey-unit/21/comment")
				.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken())
				.get("api/survey-unit/21/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+", ""), comment.toString());
	}
	
	/**
	* Test that the GET endpoint "api/survey-unit/{id}/comment" return 404 with
	* wrong survey-unit Id
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindCommentByUnexistSurveyUnit() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/toto/comment")
				.then().statusCode(404);
		given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/0/comment")
				.then().statusCode(404);
	}
	//////////////////////////	API_SURVEYUNIT_ID_COMMENT //////////////////////////
	
	
	//////////////////////////	API_SURVEYUNIT_ID_STATEDATA //////////////////////////
	/**
	* Test that the GET endpoint "api/survey-unit/{id}/state-data" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testGetSurveyUnitStateData() throws JsonMappingException, JSONException, JsonProcessingException {
		Response response = given().auth().oauth2(accessToken())
				.get("api/survey-unit/11/state-data");
		response.then().statusCode(200);
		Assert.assertEquals("{\"state\":\"EXTRACTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"}",
		response.getBody().asString().replaceAll("\\s+", ""));
	}
	
	/**
	* Test that the GET endpoint "api/survey-unit/{id}/state-data" return 404
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testGetSurveyUnitStateDataNotExists() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/99/state-data")
				.then().statusCode(404);
	}
	
	/**
	* Test that the PUT endpoint "api/survey-unit/{id}/state-data" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testPutStateDataBySurveyUnit() throws JsonMappingException, JSONException, JsonProcessingException {
		with()
				.contentType(ContentType.JSON)
				.body("{\"state\":\"INIT\",\"currentPage\":\"11\",\"date\":11111111111}")
				.given().auth().oauth2(accessToken())
				.put("api/survey-unit/21/state-data")
				.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken())
				.get("api/survey-unit/21/state-data");
		response.then().statusCode(200);
		Assert.assertEquals("{\"state\":\"INIT\",\"date\":11111111111,\"currentPage\":\"11\"}",
		response.getBody().asString().replaceAll("\\s+", ""));
	}
	

	//////////////////////////API_SURVEYUNIT_ID_STATEDATA //////////////////////////
	
	
	//////////////////////////	API_SURVEYUNIT_ID_DEPOSITPROOF //////////////////////////
	/**
	* Test that the GET endpoint "api/survey-unit/{id}/deposit-proof" return 404
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testGetSurveyUnitDepositProofNotExists() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/99/deposit-proof")
				.then().statusCode(404);
	}
	
	/**
	* Test that the GET endpoint "api/survey-unit/{id}/deposit-proof" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testGetSurveyUnitDepositProof() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/11/deposit-proof")
				.then().statusCode(200)
				.header("Content-Type", "application/pdf");
	}
	//////////////////////////	API_SURVEYUNIT_ID_DEPOSITPROOF //////////////////////////
	
	
	//////////////////////////	API_SURVEYUNIT_ID_PERSONALIZATION //////////////////////////
	/**
	* Test that the GET endpoint "api/survey-unit/{id}/personalization" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testGetSurveyUnitPersonalization() throws JsonMappingException, JSONException, JsonProcessingException {
		Response response = given().auth().oauth2(accessToken())
				.get("api/survey-unit/11/personalization");
		response.then().statusCode(200);
		Assert.assertEquals(
				"[{\"name\":\"whoAnswers1\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}]",
		response.getBody().asString().replaceAll("\\s+", ""));
	}
	
	/**
	* Test that the GET endpoint "api/survey-unit/{id}/personalization" return 404
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testGetSurveyUnitPersonalizationNotExists() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/survey-unit/99/personalization")
				.then().statusCode(404);
	}
	
	/**
	* Test that the PUT endpoint "api/survey-unit/{id}/personalization" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testPutPersonalizationBySurveyUnit() throws JsonMappingException, JSONException, JsonProcessingException {
		with().contentType(ContentType.JSON)
				.body("[{\"name\":\"whoAnswers1\",\"value\":\"MrDupond\"}]")
				.given().auth().oauth2(accessToken())
				.put("api/survey-unit/21/personalization")
				.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken())
				.get("api/survey-unit/21/personalization");
		response.then().statusCode(200);
		Assert.assertEquals("[{\"name\":\"whoAnswers1\",\"value\":\"MrDupond\"}]",
		response.getBody().asString().replaceAll("\\s+", ""));
	}
	
	/**
	* Test that the PUT endpoint "api/survey-unit/{id}/personalization" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testPutPersonalizationBySurveyUnitNotExists() throws JsonMappingException, JSONException, JsonProcessingException {
		with().contentType(ContentType.JSON)
				.body("[{\"name\":\"whoAnswers1\",\"value\":\"Mr Dupond\"}]")
				.given().auth().oauth2(accessToken())
				.put("api/survey-unit/99/personalization")
				.then().statusCode(404);
	}
	//////////////////////////	API_SURVEYUNIT_ID_PERSONALIZATION //////////////////////////
	
	
	//////////////////////////	API_NOMENCLATURE //////////////////////////
	/**
	* Test that the POST endpoint "api/nomenclature" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testPostNomenclature() throws JsonMappingException, JSONException, JsonProcessingException {
		with().contentType(ContentType.JSON)
				.body("{\"id\":\"testPostNomenclature\",\"label\":\"label for testing post nomnclature\", \"value\":{\"idNomenclature\":\"testPostNomenclature\"}}")
				.given().auth().oauth2(accessToken())
				.post("/api/nomenclature")
				.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken())
				.get("/api/nomenclature/testPostNomenclature");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+", "").contains("testPostNomenclature"));
	}
	//////////////////////////	API_NOMENCLATURE //////////////////////////

	//////////////////////////	API_NOMENCLATURES //////////////////////////

	@Test
	void testFindNomenclaturesIds() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/nomenclatures")
				.then().statusCode(200)
				.and().assertThat().body("$",hasItem("cities2019"));
	}
	
	//////////////////////////	API_NOMENCLATURE_ID //////////////////////////
	/**
	* Test that the GET endpoint "api/nomenclature/{id}" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindNomenclatureById() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/nomenclature/cities2019")
				.then().statusCode(200)
				.and().assertThat().body("isEmpty()",Matchers.is(false));
	}
	/**
	* Test that the GET endpoint "api/nomenclature/{id}" return 404 with wrong
	* nomenclature Id
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindUnexistNomenclatureById() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/nomenclature/toto")
				.then().statusCode(404);
	}
	//////////////////////////	API_NOMENCLATURE_ID //////////////////////////
	
	
	//////////////////////////	API_QUESTIONNAIRE_ID //////////////////////////
	@Test
	void testFindQuestionnaireById() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/questionnaire/simpsons")
				.then().statusCode(200);
	}
	
	@Test
	void testFindQuestionnaireByUnexistId() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/questionnaire/test")
				.then().statusCode(404);
	}
	//////////////////////////	API_QUESTIONNAIRE_ID //////////////////////////
	
	
	//////////////////////////	API_QUESTIONNAIRE_ID_METADATA //////////////////////////
	@Test
	void testFindMetadataByQuestionnaireId() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/questionnaire/simpsons/metadata")
				.then().statusCode(200);
	}
	
	@Test
	void testFindMetadataByUnexistQuestionnaireId() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/questionnaire/test/metadata")
				.then().statusCode(404);
	}
	//////////////////////////	API_QUESTIONNAIRE_ID_METADATA //////////////////////////
	
	
	//////////////////////////	API_QUESTIONNAIRE_ID_REQUIREDNOMENCLATURE //////////////////////////
	@Test
	void testFindRequiredNomenclatureByQuestionnaireId() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/questionnaire/simpsons/required-nomenclatures")
				.then().statusCode(200);
	}
	
	@Test
	void testFindRequiredNomenclatureByUnexistQuestionnaireId() throws JsonMappingException, JSONException, JsonProcessingException {
		given().auth().oauth2(accessToken())
				.when().get("api/questionnaire/test/required-nomenclatures")
				.then().statusCode(404);
	}
	//////////////////////////	API_QUESTIONNAIRE_ID_REQUIREDNOMENCLATURE //////////////////////////
	
	
	//////////////////////////	API_QUESTIONNAIREMODELS //////////////////////////
	/**
	* Test that the POST endpoint "api/questionnaire-models" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	*/
	@Test
	void testPostQuestionnaire() throws JSONException, JsonMappingException, JsonProcessingException {
		with().contentType(ContentType.JSON)
				.body("{\"idQuestionnaireModel\":\"testPostQuestionnaire\",\"label\":\"label for testing post questionnaire\", \"requiredNomenclatureIds\":[\"cities2019\"],\"value\":{\"idQuestionnaireModel\":\"testPostQuestionnaire\"}}")
				.given().auth().oauth2(accessToken())
				.post("api/questionnaire-models")
				.then().statusCode(200);
		Response response = given().auth().oauth2(accessToken())
				.get("api/questionnaire/testPostQuestionnaire");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+", "").contains("testPostQuestionnaire"));
	}
	//////////////////////////	API_QUESTIONNAIREMODELS //////////////////////////
}
