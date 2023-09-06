package fr.insee.queen.api.dto.input;


import com.fasterxml.jackson.databind.JsonNode;

public record SurveyUnitInputDto(
	String id,
	String questionnaireId,
	JsonNode personalization,
	JsonNode data,
	JsonNode comment,
	StateDataInputDto stateData) {
}
