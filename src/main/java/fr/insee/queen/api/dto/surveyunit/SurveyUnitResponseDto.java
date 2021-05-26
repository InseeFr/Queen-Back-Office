package fr.insee.queen.api.dto.surveyunit;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.dto.statedata.StateDataDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitResponseDto {
	String id;
	String questionnaireId;
	private JsonNode personalization;
	private JsonNode data;
	private JsonNode comment;
	private StateDataDto stateData;
	
	
	
	public SurveyUnitResponseDto() {
		super();
	}
	

	public SurveyUnitResponseDto(String id, String questionnaireId, JsonNode personalization, JsonNode data,
  JsonNode comment, StateDataDto stateData) {
		super();
		this.id = id;
		this.questionnaireId = questionnaireId;
		this.personalization = personalization;
		this.data = data;
		this.comment = comment;
		this.stateData = stateData;
	}





	public SurveyUnitResponseDto(String id) {
		super();
		this.id = id;
	}


	public JsonNode getComment() {
		return comment;
	}


	public void setComment(JsonNode comment) {
		this.comment = comment;
	}


	public String getQuestionnaireId() {
		return questionnaireId;
	}
	public void setQuestionnaireId(String questionnaireId) {
		this.questionnaireId = questionnaireId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public JsonNode getPersonalization() {
		return personalization;
	}

	public void setPersonalization(JsonNode personalization) {
		this.personalization = personalization;
	}

	public JsonNode getData() {
		return data;
	}

	public void setData(JsonNode data) {
		this.data = data;
	}

	public StateDataDto getStateData() {
		return stateData;
	}

	public void setStateData(StateDataDto stateData) {
		this.stateData = stateData;
	}
	


}
