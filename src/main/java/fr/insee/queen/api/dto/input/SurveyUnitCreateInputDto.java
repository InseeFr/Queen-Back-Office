package fr.insee.queen.api.dto.input;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.controller.validation.IdValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SurveyUnitCreateInputDto(
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
		@Valid
		StateDataInputDto stateData) {

	public static SurveyUnitUpdateInputDto toUpdateDto(SurveyUnitCreateInputDto surveyUnit) {
		return new SurveyUnitUpdateInputDto(surveyUnit.personalization(), surveyUnit.data(), surveyUnit.comment(), surveyUnit.stateData());
	}
}
