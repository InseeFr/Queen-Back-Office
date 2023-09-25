package fr.insee.queen.api.dto.input;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.controller.validation.IdValid;
import jakarta.validation.constraints.NotNull;

public record SurveyUnitInputDto(
		@IdValid
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
