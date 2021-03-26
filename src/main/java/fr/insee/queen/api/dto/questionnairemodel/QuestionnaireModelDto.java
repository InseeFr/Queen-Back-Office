package fr.insee.queen.api.dto.questionnairemodel;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.queen.api.domain.QuestionnaireModel;

public class QuestionnaireModelDto {

	JsonNode model;

	public QuestionnaireModelDto() {
		super();
	}
	
	public QuestionnaireModelDto(JsonNode model) {
		this.model = model;
	}
	
	public QuestionnaireModelDto(QuestionnaireModel model) {
		this.model = model.getModel();
	}

	public JsonNode getModel() {
		return model;
	}

	public void setModel(JsonNode model) {
		this.model = model;
	}
}
