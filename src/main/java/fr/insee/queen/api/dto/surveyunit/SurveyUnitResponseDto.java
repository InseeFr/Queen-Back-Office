package fr.insee.queen.api.dto.surveyunit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.dto.stateData.StateDataDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitResponseDto {
	String id;
	String questionnaireId;
	private JSONArray personnalization;
	private JSONObject data;
	private StateDataDto stateData;
	
	
	public SurveyUnitResponseDto(String id, String questionnaireId, 
			JSONArray personnalization, JSONObject data, StateDataDto stateDataDto) {
		super();
		this.id = id;
		this.questionnaireId = questionnaireId;
		this.personnalization = personnalization;
		this.data = data;
		this.stateData = stateDataDto;
		
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
	
	public JSONArray getPersonnalization() {
		return personnalization;
	}

	public void setPersonnalization(JSONArray comment) {
		this.personnalization = comment;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public StateDataDto getStateData() {
		return stateData;
	}

	public void setStateData(StateDataDto stateData) {
		this.stateData = stateData;
	}
	


}
