package fr.insee.queen.api.dto.input;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SurveyUnitInputDto(
		@NotBlank
		String id,
		@NotNull
		String questionnaireId,
		@NotNull
		JsonNode personalization,
		@NotNull
		JsonNode data,
		@NotNull
		JsonNode comment,
		@NotNull
		StateDataInputDto stateData) {
}
