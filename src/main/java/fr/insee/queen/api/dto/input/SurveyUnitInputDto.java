package fr.insee.queen.api.dto.input;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SurveyUnitInputDto(
		@NotBlank
		String id,
		@NotNull
		String questionnaireId,
		@NotNull
		ArrayNode personalization,
		@NotNull
		ObjectNode data,
		@NotNull
		ObjectNode comment,
		@NotNull
		StateDataInputDto stateData) {
}
