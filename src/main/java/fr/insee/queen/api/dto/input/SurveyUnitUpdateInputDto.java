package fr.insee.queen.api.dto.input;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;

public record SurveyUnitUpdateInputDto(
		ArrayNode personalization,
		ObjectNode data,
		ObjectNode comment,
		@Valid
		StateDataInputDto stateData) {
}
