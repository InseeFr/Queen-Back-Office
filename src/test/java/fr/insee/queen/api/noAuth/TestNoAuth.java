package fr.insee.queen.api.noAuth;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.post;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasItem;

import java.io.File;

import org.hamcrest.Matchers;
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

	/**
	 * Test that the GET endpoint "api/campaigns" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindCampaign() throws InterruptedException {
		get("api/campaigns")
		.then().statusCode(200)
		.and().assertThat().body("id", hasItem("simpsons2020x00"));

	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/questionnaires" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindQuestionnaireByCampaign() {
		get("api/campaign/simpsons2020x00/questionnaires")
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
	void testFindQuestionnaireByUnexistCampaign() {
		get("api/campaign/toto/questionnaires")
		.then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/survey-units" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindSurveyUnitsByCampaign() {
		get("api/campaign/simpsons2020x00/survey-units")
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

	/**
	 * Test that the GET endpoint "api/nomenclature/{id}" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindNomenclatureById() {
		get("api/nomenclature/cities2019")
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
	void testFindUnexistNomenclatureById() {
		get("api/nomenclature/toto")
		.then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/comment" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindCommentBySurveyUnit() throws JSONException {
		Response response = get("api/survey-unit/11/comment");
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
	void testPutCommentBySurveyUnit() {
		ObjectNode comment = objectMapper.createObjectNode();
		comment.put("comment", "value");

		with()
		.contentType(ContentType.JSON)
		.body(comment.toString())
		.put("api/survey-unit/21/comment")
		.then().statusCode(200);
		Response response = get("api/survey-unit/21/comment");
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
	void testFindCommentByUnexistSurveyUnit() {
		get("api/survey-unit/toto/comment")
		.then().statusCode(404);
		get("api/survey-unit/0/comment")
		.then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindDataBySurveyUnit() throws JSONException {
		Response response = get("api/survey-unit/11/data");
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
	void testPutDataBySurveyUnit() {
		ObjectNode data = objectMapper.createObjectNode();
		data.put("data", "value");
		with()
		.contentType(ContentType.JSON)
		.body(data.toString())
		.put("api/survey-unit/21/data")
		.then().statusCode(200);
		Response response = get("api/survey-unit/21/data");
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
	void testFindDataByUnexistSurveyUnit() {
		get("api/survey-unit/toto/data")
		.then().statusCode(404);
		get("api/survey-unit/0/data")
		.then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/required-nomenclature" return
	 * 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindRequiredNomenclatureByCampaign() {
		get("api/campaign/vqs2021x00/required-nomenclatures")
		.then().statusCode(200)
		.and().assertThat().body("$",hasItem("french cities 2019"));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/required-nomenclature" return
	 * 404 with wrong campaign Id
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindRequiredNomenclatureByUnexistCampaign() {
		get("api/campaign/toto/required-nomenclatures")
		.then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/questionnaire-id" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindQuestionnaireIdByCampaign() throws JSONException {
		Response response = get("api/campaign/simpsons2020x00/questionnaire-id");
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
	 */
	@Test
	void testFindQuestionnaireIdByUnexistCampaign() throws JSONException {
		get("api/campaign/test/questionnaire-id")
		.then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/personalization" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitPersonalization() throws JSONException {
		Response response = get("api/survey-unit/11/personalization");
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
	void testGetSurveyUnitPersonalizationNotExists() throws JSONException {
		get("api/survey-unit/99/personalization")
		.then().statusCode(404);
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/personalization" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutPersonalizationBySurveyUnit() {
		with()
		.contentType(ContentType.JSON)
		.body("[{\"name\":\"whoAnswers1\",\"value\":\"MrDupond\"}]")
		.put("api/survey-unit/21/personalization")
		.then().statusCode(200);
		Response response = get("api/survey-unit/21/personalization");
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
	void testPutPersonalizationBySurveyUnitNotExists() {
		with()
		.contentType(ContentType.JSON)
		.body("[{\"name\":\"whoAnswers1\",\"value\":\"Mr Dupond\"}]")
		.put("api/survey-unit/99/personalization")
		.then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/state-data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitStateData() throws JSONException {
		Response response = get("api/survey-unit/11/state-data");
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
	void testGetSurveyUnitStateDataNotExists() throws JSONException {
		get("api/survey-unit/99/state-data")
		.then().statusCode(404);
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state-data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutStateDataBySurveyUnit() {
		with()
		.contentType(ContentType.JSON)
		.body("{\"state\":\"INIT\",\"currentPage\":\"11\",\"date\":11111111111}")
		.put("api/survey-unit/21/state-data")
		.then().statusCode(200);
		Response response = get("api/survey-unit/21/state-data");
		response.then().statusCode(200);
		Assert.assertEquals("{\"state\":\"INIT\",\"date\":11111111111,\"currentPage\":\"11\"}",
				response.getBody().asString().replaceAll("\\s+", ""));
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state-data" return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutStateDataBySurveyUnitNotExists() {
		with()
		.contentType(ContentType.JSON)
		.body("{\"state\":\"INIT\",\"currentPage\":\"11\",\"date\":11111111111}")
		.put("api/survey-unit/99/state-data")
		.then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/deposit-proof" return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitDepositProofNotExists() throws JSONException {
		get("api/survey-unit/99/deposit-proof")
		.then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/deposit-proof" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitDepositProof() throws JSONException {
		get("api/survey-unit/11/deposit-proof")
		.then().statusCode(200)
		.header("Content-Type", "application/pdf");
	}

	/**
	 * Test that the POST endpoint "api/questionnaire-models" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPostQuestionnaire() throws JSONException {
		with().contentType(ContentType.JSON)
		.body("{\"idQuestionnaireModel\":\"testPostQuestionnaire\",\"label\":\"label for testing post questionnaire\", \"requiredNomenclatureIds\":[\"cities2019\"],\"value\":{\"idQuestionnaireModel\":\"testPostQuestionnaire\"}}")
		.post("api/questionnaire-models")
		.then().statusCode(200);
		Response response = get("api/questionnaire/testPostQuestionnaire");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+", "").contains("testPostQuestionnaire"));
	}

	/**
	 * Test that thePOST endpoint "/api/campaign/{id}/survey-unit" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPostCampaignSurveyUnit() throws JSONException {

		String postBody = "{\"id\":55,\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXTRACTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"},\"questionnaireId\":\"vqs2021x00\"}";
		String respBodyExpected = "{\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXTRACTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"}}";
		with()
		.contentType(ContentType.JSON).body(postBody)
		.post("/api/campaign/vqs2021x00/survey-unit")
		.then().statusCode(200);
		Response response = get("/api/survey-unit/55");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+", ""),
				respBodyExpected.replaceAll("\\s+", ""));
	}
	
	/**
	 * Test that the POST endpoint "/api/campaign/{id}/survey-unit" return 400 if su already exist
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPostCampaignSurveyUnitAlreadyExist() throws JSONException {
		String postBody = "{\"id\":22,\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXTRACTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"},\"questionnaireId\":\"vqs2021x00\"}";
		with()
		.contentType(ContentType.JSON)
		.body(postBody)
		.post("/api/campaign/vqs2021x00/survey-unit")
		.then().statusCode(400);
	}

	/**
	 * Test that the POST endpoint "api/nomenclature" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPostNomenclature() throws JSONException {
		with()
		.contentType(ContentType.JSON)
		.body("{\"id\":\"testPostNomenclature\",\"label\":\"label for testing post nomnclature\", \"value\":{\"idNomenclature\":\"testPostNomenclature\"}}")
		.post("/api/nomenclature")
		.then().statusCode(200);
		Response response = get("/api/nomenclature/testPostNomenclature");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+", "").contains("testPostNomenclature"));
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
		Response response = get("api/campaigns");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+", "").contains(
				"{\"id\":\"testPostCampaign\",\"questionnaireIds\":[\"QmWithoutCamp\"]}".replaceAll("\\s+", "")));
	}
	
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
		campaign.put("id", "simpsons2020x00");
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
	    Response resp2 = get("api/campaign/simpsons2020x00/questionnaire-id");
	    resp2.then().statusCode(200);
		Assert.assertTrue(resp2.getBody().asString().contains("simpsons-v1"));
		
		// Nomenclature "cities20199" has been created
		get("api/nomenclature/cities20199").then().statusCode(200);
		
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
		campaign.put("id", "anotherCampaign");
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
		.post("api/campaign/context");
		resp.then().statusCode(200);

		// Response is as expected
		String responseString = objectMapper.readTree(resp.getBody().asString()).toString();
	    Assert.assertEquals(expected.toString(), responseString);
	    
	    // Campaign "anotherCampaign" has been created
	 	Response resp2 = get("api/campaigns");
	    resp2.then().statusCode(200);
		Assert.assertFalse(resp2.getBody().asString().contains("anotherCampaigns"));
		
	    
		
		
	}

}
