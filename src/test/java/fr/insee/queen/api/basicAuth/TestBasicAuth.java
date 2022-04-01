package fr.insee.queen.api.basicAuth;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasItem;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.boot.web.server.LocalServerPort;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.insee.queen.api.constants.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public abstract class TestBasicAuth {

	public static ClientAndServer clientAndServer;
	public static MockServerClient mockServerClient;

	private ObjectMapper objectMapper = new ObjectMapper();

	@LocalServerPort
	protected int port;

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

		expectedBody = "[" + "{" + "\"id\":\"11\", " + "\"campaign\":\"SIMPSONS2020X00\", "
				+ "\"campaignLabel\":\"Survey on the Simpsons tv show 2020\", "
				+ "\"collectionStartDate\":\"1577836800000\", " + "\"collectionEndDate\":\"1622035845000\"" + "}, "
				+ "{" + "\"id\":\"12\", " + "\"campaign\":\"SIMPSONS2020X00\", "
				+ "\"campaignLabel\":\"Survey on the Simpsons tv show 2020\", "
				+ "\"collectionStartDate\":\"1577836800000\", " + "\"collectionEndDate\":\"1622035845000\"" + "}, "
				+ "{" + "\"id\":\"20\", " + "\"campaign\":\"VQS2021X00\", "
				+ "\"campaignLabel\":\"Everyday life and health survey 2021\", "
				+ "\"collectionStartDate\":\"1577836800000\", " + "\"collectionEndDate\":\"1622035845000\"" + "}]";
		mockServerClient.when(request().withPath(Constants.API_PEARLJAM_SURVEYUNITS))
				.respond(response().withStatusCode(200)
						.withHeaders(new Header("Content-Type", "application/json; charset=utf-8"),
								new Header("Cache-Control", "public, max-age=86400"))
						.withBody(expectedBody));
	}

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
		given().auth().preemptive().basic("INTW1", "a")
		.when().post("api/create-dataset");
	}

	
	//////////////////////////API_CAMPAIGNS ///////////////////////
	/**
	* Test that the GET endpoint "api/campaigns" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindCampaign() throws InterruptedException {
		given().auth().preemptive().basic("INTW1", "a")
		.when().get("api/admin/campaigns")
		.then().statusCode(200)
		.and().assertThat().body("id", hasItem("SIMPSONS2020X00"));
	}
	
	/**
	* Test that the POST endpoint "api/campaigns" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testPostCampaign() throws JSONException {
		with()
		.contentType(ContentType.JSON)
		.body("{\"id\":\"testPostCampaign\",\"label\":\"label for testing post campaign\",\"metadata\":{}, \"questionnaireIds\":[\"QmWithoutCamp\"]}")
		.given().auth().preemptive().basic("INTW1", "a")
		.post("api/campaigns").then().statusCode(200);
		Response response = given().auth().preemptive().basic("INTW1", "a")
				.get("api/admin/campaigns");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+", "").contains(
		"{\"id\":\"TESTPOSTCAMPAIGN\",\"questionnaireIds\":[\"QmWithoutCamp\"]}".replaceAll("\\s+", "")));
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
		.given().auth().preemptive().basic("INTW1", "a")
		.post("api/campaign/context");
		resp.then().statusCode(200);
		
		// Response is as expected
		String responseString = objectMapper.readTree(resp.getBody().asString()).toString();
		Assert.assertEquals(expected.toString(), responseString);
		
		// Questionnaire model "simpsons-v1" has been created
		Response resp2 = given().auth().preemptive().basic("INTW1", "a")
				.get("api/campaign/SIMPSONS2020X00/questionnaire-id");
		resp2.then().statusCode(200);
		Assert.assertTrue(resp2.getBody().asString().contains("simpsons-v1"));
		
		// Nomenclature "cities20199" has been created
		given().auth().preemptive().basic("INTW1", "a")
		.when().get("api/nomenclature/cities20199").then().statusCode(200);
	}
	
	//////////////////////////	API_CAMPAIGN_ID_SURVEY_UNITS //////////////////////////
	/**
	* Test that the GET endpoint "api/campaigns/{id}/survey-units" return 200
	* 
	* @throws InterruptedException
	* @throws JSONException
	*/
	@Test
	void testFindSurveyUnitsByCampaign() {
		given().auth().preemptive().basic("INTW1", "a")
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
	void testFindSurveyUnitsByUnexistCampaign() {
		given().auth().preemptive().basic("INTW1", "a")
		.when().get("api/campaign/toto/survey-units")
		.then().statusCode(404);
	}
}
