package fr.insee.queen.api.basicAuth;

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

		expectedBody = "[" + "{" + "\"id\":\"11\", " + "\"campaign\":\"simpsons2020x00\", "
				+ "\"campaignLabel\":\"Survey on the Simpsons tv show 2020\", "
				+ "\"collectionStartDate\":\"1577836800000\", " + "\"collectionEndDate\":\"1622035845000\"" + "}, "
				+ "{" + "\"id\":\"12\", " + "\"campaign\":\"simpsons2020x00\", "
				+ "\"campaignLabel\":\"Survey on the Simpsons tv show 2020\", "
				+ "\"collectionStartDate\":\"1577836800000\", " + "\"collectionEndDate\":\"1622035845000\"" + "}, "
				+ "{" + "\"id\":\"20\", " + "\"campaign\":\"vqs2021x00\", "
				+ "\"campaignLabel\":\"Everyday life and health survey 2021\", "
				+ "\"collectionStartDate\":\"1577836800000\", " + "\"collectionEndDate\":\"1622035845000\"" + "}]";
		mockServerClient.when(request().withPath(Constants.API_PEARLJAM_SURVEY_UNITS))
				.respond(response().withStatusCode(200)
						.withHeaders(new Header("Content-Type", "application/json; charset=utf-8"),
								new Header("Cache-Control", "public, max-age=86400"))
						.withBody(expectedBody));
	}

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
		given().auth().preemptive().basic("INTW1", "a").when().post("api/createDataSet");
	}

	/**
	 * Test that the GET endpoint "api/campaigns" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindCampaign() throws InterruptedException {
		given().auth().preemptive().basic("INTW1", "a").when().get("api/campaigns").then().statusCode(200).and()
				.assertThat().body("id", hasItem("simpsons2020x00"));

	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/questionnaire" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindQuestionnaireByCampaign() {
		given().auth().preemptive().basic("INTW1", "a").get("api/campaign/simpsons2020x00/questionnaire").then()
				.statusCode(200).and().assertThat().body("isEmpty()", Matchers.is(false));
	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/questionnaire" return 404 with
	 * wrong questionnaire Id
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindQuestionnaireByUnexistCampaign() {
		given().auth().preemptive().basic("INTW1", "a").get("api/campaign/toto/questionnaire").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/survey-units" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindSurveyUnitsByCampaign() {
		given().auth().preemptive().basic("INTW1", "a").get("api/campaign/simpsons2020x00/survey-units").then()
				.statusCode(200).and().assertThat().body("id", hasItem("11"));
	}

	/**
	 * Test that the GET endpoint "api/campaigns/{id}/questionnaire" return 404 with
	 * wrong questionnaire Id
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindSurveyUnitsByUnexistCampaign() {
		given().auth().preemptive().basic("INTW1", "a").get("api/campaign/toto/survey-units").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/nomenclature/{id}" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindNomenclatureById() {
		given().auth().preemptive().basic("INTW1", "a").get("api/nomenclature/cities2019").then().statusCode(200).and()
				.assertThat().body("isEmpty()", Matchers.is(false));
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
		given().auth().preemptive().basic("INTW1", "a").get("api/nomenclature/toto").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/comment" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindCommentBySurveyUnit() {
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/22/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), objectMapper.createObjectNode().toPrettyString());
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/comment" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitComment() throws JSONException {
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/11/comment");
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
	void testPutCommentBySurveyUnit() {

		ObjectNode comment = objectMapper.createObjectNode();
		comment.put("comment", "value");

		with().contentType(ContentType.JSON).body(comment.toString()).given().auth().preemptive().basic("INTW1", "a")
				.put("api/survey-unit/21/comment").then().statusCode(200);
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/21/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), comment.toPrettyString());
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
		given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/toto/comment").then().statusCode(404);
		given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/0/comment").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindDataBySurveyUnit() {
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/22/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), objectMapper.createObjectNode().toPrettyString());
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutDataBySurveyUnit() {
		ObjectNode data = objectMapper.createObjectNode();// new JsonNode(putData);
		data.put("data", "value");

		with().contentType(ContentType.JSON).body(data.toString()).given().auth().preemptive().basic("INTW1", "a")
				.put("api/survey-unit/21/data").then().statusCode(200);
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/21/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString(), data.toPrettyString());
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
		given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/toto/data").then().statusCode(404);
		given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/0/data").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitData() throws JSONException {
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/11/data");
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
	void testFindRequiredNomenclatureByCampaign() {
		given().auth().preemptive().basic("INTW1", "a").get("api/campaign/vqs2021x00/required-nomenclatures").then()
				.statusCode(200).and().assertThat().body("$", hasItem("french cities 2019"));
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
		given().auth().preemptive().basic("INTW1", "a").get("api/campaign/toto/required-nomenclatures").then()
				.statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/questionnaire-id" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testFindQuestionnaireIdByCampaign() throws JSONException {
		Response response = given().auth().preemptive().basic("INTW1", "a").when().get("api/campaign/simpsons2020x00/questionnaire-id");
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
	void testFindQuestionnaireIdByUnexistCampaign() throws JSONException {
		given().auth().preemptive().basic("INTW1", "a").when().get("api/campaign/test/questionnaire-id").then()
				.statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/personalization" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitPersonalization() throws JSONException {
		ArrayNode personalization = objectMapper.createArrayNode();
		ObjectNode obj = objectMapper.createObjectNode();
		obj.put("name", "whoAnswers1");
		obj.put("value", "Mr Dupond");
		personalization.add(obj);
		obj = objectMapper.createObjectNode();
		obj.put("name", "whoAnswers2");
		obj.put("value", "");
		personalization.add(obj);
		given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/11/personalization").then().statusCode(200)
				.and().assertThat().assertThat().body(containsString(personalization.toPrettyString()));
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/personalization" return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitPersonalizationNotExists() throws JSONException {
		given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/99/personalization").then()
				.statusCode(404);
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/personalization" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutPersonalizationBySurveyUnit() {
		ArrayNode personalization = objectMapper.createArrayNode();
		ObjectNode obj = objectMapper.createObjectNode();
		obj.put("name", "whoAnswers1");
		obj.put("value", "Mr Dupond");
		personalization.add(obj);
		with().contentType(ContentType.JSON).body("[{\"name\":\"whoAnswers1\",\"value\":\"Mr Dupond\"}]").given().auth()
				.preemptive().basic("INTW1", "a").put("api/survey-unit/21/personalization").then().statusCode(200);
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/21/personalization");
		response.then().statusCode(200);
		Assert.assertEquals(personalization.toPrettyString(), response.getBody().asString());
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/personalization" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutPersonalizationBySurveyUnitNotExists() {
		with().contentType(ContentType.JSON).body("[{\"name\":\"whoAnswers1\",\"value\":\"Mr Dupond\"}]").given().auth()
				.preemptive().basic("INTW1", "a").put("api/survey-unit/99/personalization").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/state-data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitStateData() throws JSONException {
		ObjectNode obj = objectMapper.createObjectNode();
		obj.put("state", "EXPORTED");
		obj.put("date", 1111111111L);
		obj.put("currentPage", "2.3#5");
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/11/state-data");
		response.then().statusCode(200);
		Assert.assertEquals(obj.toString(), response.getBody().asString().replaceAll("\\s+",""));
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/state-data" return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitStateDataNotExists() throws JSONException {
		given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/99/state-data").then().statusCode(404);
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state-data" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutStateDataBySurveyUnit() {
		ObjectNode obj = objectMapper.createObjectNode();
		obj.put("state", "EXPORTED");
		obj.put("date", 1111111111L);
		obj.put("currentPage", "2.3#5");
		with().contentType(ContentType.JSON).body(obj.toString())
				.given().auth().preemptive().basic("INTW1", "a").put("api/survey-unit/21/state-data").then()
				.statusCode(200);
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/21/state-data");
		response.then().statusCode(200);
		Assert.assertEquals(obj.toString().replaceAll("\\s+",""),
				response.getBody().asString().replaceAll("\\s+",""));
	}

	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state-data" return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testPutStateDataBySurveyUnitNotExists() {
		with().contentType(ContentType.JSON).body("{\"state\":\"INIT\",\"currentPage\":\"11\",\"date\":11111111111}")
				.given().auth().preemptive().basic("INTW1", "a").put("api/survey-unit/99/state-data").then()
				.statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/deposit-proof" return 404
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitDepositProofNotExists() throws JSONException {
		given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/99/deposit-proof").then().statusCode(404);
	}

	/**
	 * Test that the GET endpoint "api/survey-unit/{id}/deposit-proof" return 200
	 * 
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	@Test
	void testGetSurveyUnitDepositProof() throws JSONException {
		given().auth().preemptive().basic("INTW1", "a").get("api/survey-unit/11/deposit-proof").then().statusCode(200)
				.header("Content-Type", "application/pdf");
	}
	
	/**
	 * Test that the POST endpoint "api/questionnaire-models"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	void testPostQuestionnaire() throws JSONException {
		with()
		.contentType(ContentType.JSON)
		.body("{\"idQuestionnaireModel\":\"testPostQuestionnaire\",\"label\":\"label for testing post questionnaire\", \"requiredNomenclaturesId\":[\"cities2019\"],\"value\":{\"idQuestionnaireModel\":\"testPostQuestionnaire\"}}")
		.given().auth().preemptive().basic("INTW1", "a")
		.post("api/questionnaire-models")
		.then().statusCode(200);
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/questionnaire/testPostQuestionnaire");
		response.then().statusCode(200);
    Assert.assertTrue(response.getBody().asString().replaceAll("\\s+","").contains("testPostQuestionnaire"));
}
	
	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	void testPostCampaignSurveyUnit() throws JSONException {
		
		String postBody = "{\"id\":55,\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXPORTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"},\"questionnaireId\":\"vqs2021x00\"}";
		String respBodyExpected = "{\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXPORTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"}}";
		with()
		.contentType(ContentType.JSON)
		.body(postBody)
		.given().auth().preemptive().basic("INTW1", "a")
		.post("/api/campaign/vqs2021x00/survey-unit")
		.then().statusCode(200);
		Response response = given().auth().preemptive().basic("INTW1", "a").get("/api/survey-unit/55");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+",""), respBodyExpected.replaceAll("\\s+",""));
	}
	
	/**
	 * Test that the POST endpoint "api/nomenclature"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	void testPostNomenclature() throws JSONException {
		with()
		.contentType(ContentType.JSON)
		.body("{\"id\":\"testPostNomenclature\",\"label\":\"label for testing post nomnclature\", \"value\":{\"idNomenclature\":\"testPostNomenclature\"}}")
		.given().auth().preemptive().basic("INTW1", "a")
		.post("/api/nomenclature")
		.then().statusCode(200);
		Response response = given().auth().preemptive().basic("INTW1", "a").get("/api/nomenclature/testPostNomenclature");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+","").contains("testPostNomenclature"));
	}
	
	/**
	 * Test that the GET endpoint "api/campaigns"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	void testPostCampaign() throws JSONException {
		with()
		.contentType(ContentType.JSON)
		.body("{\"id\":\"testPostCampaign\",\"label\":\"label for testing post campaign\",\"metadata\":{}, \"questionnaireModelsIds\":[\"QmWithoutCamp\"]}")
		.given().auth().preemptive().basic("INTW1", "a")
		.post("api/campaigns")
		.then().statusCode(200);
		Response response = given().auth().preemptive().basic("INTW1", "a").get("api/campaigns");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+","").contains("{\"id\":\"testPostCampaign\",\"questionnaireIds\":[\"QmWithoutCamp\"]}".replaceAll("\\s+",""))); 
	}

}
