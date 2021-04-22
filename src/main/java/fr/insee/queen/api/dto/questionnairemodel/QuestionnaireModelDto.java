package fr.insee.queen.api.dto.questionnairemodel;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.domain.QuestionnaireModel;

public class QuestionnaireModelDto {

	JsonNode value;

	public QuestionnaireModelDto() {
		super();
	}
	
	public QuestionnaireModelDto(JsonNode value) {
		this.value = value;
	}
	
	public QuestionnaireModelDto(QuestionnaireModel qm) {
		this.value = qm.getValue();
	}

	public JsonNode getValue() {
		return value;
	}

	public void setValue(JsonNode value) {
		this.value = value;
	}
}
