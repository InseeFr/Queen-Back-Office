package fr.insee.queen.api.noAuth;

import static io.restassured.RestAssured.post;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.web.server.LocalServerPort;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	public void setUp() {
		RestAssured.port = port;
		post("api/createDataSet");
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
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+",""), objectMapper.createObjectNode().toString());
  }
  
  /**
	 * Test that the GET endpoint "api/survey-unit/{id}/comment"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testGetSurveyUnitComment() throws JSONException {
		Response response = get("api/survey-unit/11/comment");
		response.then().statusCode(200);
		Assert.assertEquals("{\"COMMENT\":\"acomment\"}", response.getBody().asString().replaceAll("\\s+",""));
  }
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/comment"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutCommentBySurveyUnit() {
		ObjectNode comment = objectMapper.createObjectNode();
		comment.put("comment", "value");

		with()
			.contentType(ContentType.JSON)
			.body(comment.toString())
		.put("api/survey-unit/21/comment")
			.then()
			.statusCode(200);
		Response response = get("api/survey-unit/21/comment");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+",""), comment.toString());
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
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+",""), objectMapper.createObjectNode().toString());
	}
	
	/**
	 * Test that the PUT endpoint "api/survey-unit/{id}/data"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutDataBySurveyUnit() {
		ObjectNode data = objectMapper.createObjectNode();
		data.put("data", "value");
		with()
			.contentType(ContentType.JSON)
			.body(data.toString())
		.put("api/survey-unit/21/data")
			.then()
			.statusCode(200);
		Response response = get("api/survey-unit/21/data");
		response.then().statusCode(200);
		Assert.assertEquals(response.getBody().asString().replaceAll("\\s+",""), data.toString());
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
	 * Test that the GET endpoint "api/survey-unit/{id}/data"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testGetSurveyUnitData() throws JSONException {
		Response response = get("api/survey-unit/11/data");
		response.then().statusCode(200);
		Assert.assertEquals("{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}}", response.getBody().asString().replaceAll("\\s+",""));
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
		.assertThat().body("$", hasItem("french cities 2019"));
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
	
	/**
	 * Test that the GET endpoint "api/campaign/{id}/questionnaire-id"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindQuestionnaireIdByCampaign() throws JSONException {
		Response response = get("api/campaign/simpsons2020x00/questionnaire-id");
		response.then().statusCode(200);
		Assert.assertEquals("[{\"questionnaireId\"" + ":" + "\"simpsons\"}]", response.getBody().asString().replaceAll("\\s+",""));
	}

	/**
	 * Test that the GET endpoint "api/campaign/{id}/questionnaire-id"
	 * return 404 with wrong campaign Id
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testFindQuestionnaireIdByUnexistCampaign() throws JSONException {
		get("api/campaign/test/questionnaire-id")
		.then().statusCode(404);
  }

  /**
	 * Test that the GET endpoint "api/survey-unit/{id}/personalization"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testGetSurveyUnitPersonalization() throws JSONException {
		Response response = get("api/survey-unit/11/personalization");
		response.then().statusCode(200);
		Assert.assertEquals("[{\"name\":\"whoAnswers1\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}]",
				response.getBody().asString().replaceAll("\\s+",""));
  }

    /**
	 * Test that the GET endpoint "api/survey-unit/{id}/personalization"
	 * return 404
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testGetSurveyUnitPersonalizationNotExists() throws JSONException {
		get("api/survey-unit/99/personalization")
    .then().statusCode(404);
    }

  /**
	 * Test that the PUT endpoint "api/survey-unit/{id}/personalization"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutPersonalizationBySurveyUnit() {
		with()
			.contentType(ContentType.JSON)
			.body("[{\"name\":\"whoAnswers1\",\"value\":\"MrDupond\"}]")
		.put("api/survey-unit/21/personalization")
			.then()
			.statusCode(200);
		Response response = get("api/survey-unit/21/personalization");
		response.then().statusCode(200);
		Assert.assertEquals("[{\"name\":\"whoAnswers1\",\"value\":\"MrDupond\"}]", response.getBody().asString().replaceAll("\\s+",""));
	}
  
  /**
	 * Test that the PUT endpoint "api/survey-unit/{id}/personalization"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutPersonalizationBySurveyUnitNotExists() {
		with()
			.contentType(ContentType.JSON)
			.body("[{\"name\":\"whoAnswers1\",\"value\":\"Mr Dupond\"}]")
		.put("api/survey-unit/99/personalization")
			.then()
			.statusCode(404);
  }
  
  /**
	 * Test that the GET endpoint "api/survey-unit/{id}/state-data"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testGetSurveyUnitStateData() throws JSONException {
		Response response = get("api/survey-unit/11/state-data");
		response.then().statusCode(200);
		Assert.assertEquals("{\"state\":\"EXPORTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"}",
				response.getBody().asString().replaceAll("\\s+",""));
  }

    /**
	 * Test that the GET endpoint "api/survey-unit/{id}/state-data"
	 * return 404
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testGetSurveyUnitStateDataNotExists() throws JSONException {
		get("api/survey-unit/99/state-data")
    .then().statusCode(404);
  }
  

    /**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state-data"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutStateDataBySurveyUnit() {
		with()
			.contentType(ContentType.JSON)
			.body("{\"state\":\"INIT\",\"currentPage\":\"11\",\"date\":11111111111}")
		.put("api/survey-unit/21/state-data")
			.then()
			.statusCode(200);
		Response response = get("api/survey-unit/21/state-data");
		response.then().statusCode(200);
		Assert.assertEquals("{\"state\":\"INIT\",\"date\":11111111111,\"currentPage\":\"11\"}", response.getBody().asString().replaceAll("\\s+",""));
  }
  
  /**
	 * Test that the PUT endpoint "api/survey-unit/{id}/state-data"
	 * return 404
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPutStateDataBySurveyUnitNotExists() {
		with()
			.contentType(ContentType.JSON)
			.body("{\"state\":\"INIT\",\"currentPage\":\"11\",\"date\":11111111111}")
		.put("api/survey-unit/99/state-data")
			.then()
			.statusCode(404);
  }
  
  /**
	 * Test that the GET endpoint "api/survey-unit/{id}/deposit-proof"
	 * return 404
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testGetSurveyUnitDepositProofNotExists() throws JSONException {
		get("api/survey-unit/99/deposit-proof")
    .then().statusCode(404);
  }

  /**
	 * Test that the GET endpoint "api/survey-unit/{id}/deposit-proof"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testGetSurveyUnitDepositProof() throws JSONException {
		get("api/survey-unit/11/deposit-proof")
    .then().statusCode(200).header("Content-Type", "application/pdf");
  }
	
	/**
	 * Test that the POST endpoint "api/questionnaire-models"
	 * return 200
	 * @throws InterruptedException
	 * @throws JSONException 
	 */
	@Test
	public void testPostQuestionnaire() throws JSONException {
		with()
		.contentType(ContentType.JSON)
		.body("{\"idQuestionnaireModel\":\"testPostQuestionnaire\",\"label\":\"label for testing post questionnaire\", \"requiredNomenclaturesId\":[\"cities2019\"],\"value\":{\"idQuestionnaireModel\":\"testPostQuestionnaire\"}}")
		.post("api/questionnaire-models")
		.then().statusCode(200);
		Response response = get("api/questionnaire/testPostQuestionnaire");
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
	public void testPostCampaignSurveyUnit() throws JSONException {
		
		String postBody = "{\"id\":55,\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXPORTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"},\"questionnaireId\":\"vqs2021x00\"}";
		String respBodyExpected = "{\"personalization\":[{\"name\":\"whoAnswers34\",\"value\":\"MrDupond\"},{\"name\":\"whoAnswers2\",\"value\":\"\"}],\"data\":{\"EXTERNAL\":{\"LAST_BROADCAST\":\"12/07/1998\"}},\"comment\":{\"COMMENT\":\"acomment\"},\"stateData\":{\"state\":\"EXPORTED\",\"date\":1111111111,\"currentPage\":\"2.3#5\"}}";
		with()
		.contentType(ContentType.JSON)
		.body(postBody)
		.post("/api/campaign/vqs2021x00/survey-unit")
		.then().statusCode(200);
		Response response = get("/api/survey-unit/55");
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
	public void testPostNomenclature() throws JSONException {
		with()
		.contentType(ContentType.JSON)
		.body("{\"id\":\"testPostNomenclature\",\"label\":\"label for testing post nomnclature\", \"value\":{\"idNomenclature\":\"testPostNomenclature\"}}")
		.post("/api/nomenclature")
		.then().statusCode(200);
		Response response = get("/api/nomenclature/testPostNomenclature");
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
	public void testPostCampaign() throws JSONException {
		with()
		.contentType(ContentType.JSON)
		.body("{\"id\":\"testPostCampaign\",\"label\":\"label for testing post campaign\",\"metadata\":{}, \"questionnaireModelsIds\":[\"QmWithoutCamp\"]}")
		.post("api/campaigns")
		.then().statusCode(200);
		Response response = get("api/campaigns");
		response.then().statusCode(200);
		Assert.assertTrue(response.getBody().asString().replaceAll("\\s+","").contains("{\"id\":\"testPostCampaign\",\"questionnaireIds\":[\"QmWithoutCamp\"]}".replaceAll("\\s+",""))); 
	}
	

}
