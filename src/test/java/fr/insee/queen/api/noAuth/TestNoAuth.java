package fr.insee.queen.api.noAuth;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.post;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasItem;

import java.io.File;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.web.server.LocalServerPort;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public abstract class TestNoAuth {

	public ClientAndServer clientAndServer;
	public MockServerClient mockServerClient;

	private ObjectMapper objectMapper = new ObjectMapper();

	@LocalServerPort
	protected int port;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
		post("api/create-dataset");
	}

	//////////////////////////	API_CAMPAIGNS ///////////////////////
	/**
	 * Test that the GET endpoint "api/campaigns" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindCampaign() throws InterruptedException {
		get("api/admin/campaigns")
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
		.post("api/campaigns").then().statusCode(200);
		Response response = get("api/admin/campaigns");
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
		.post("api/campaign/context");
		resp.then().statusCode(200);

		// Response is as expected
		String responseString = objectMapper.readTree(resp.getBody().asString()).toString();
	    Assert.assertEquals(expected.toString(), responseString);
	    
	    // Questionnaire model "simpsons-v1" has been created
	    Response resp2 = get("api/campaign/SIMPSONS2020X00/questionnaire-id");
	    resp2.then().statusCode(200);
		Assert.assertTrue(resp2.getBody().asString().contains("simpsons-v1"));
		
		// Nomenclature "cities20199" has been created
		get("api/nomenclature/cities20199").then().statusCode(200);
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
	void testFindSurveyUnitsByCampaign() {
		get("api/campaign/SIMPSONS2020X00/survey-units")
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
		get("api/campaign/toto/survey-units")
		.then().statusCode(404);
	}
	//////////////////////////	API_CAMPAIGN_ID_SURVEY_UNITS //////////////////////////
}
