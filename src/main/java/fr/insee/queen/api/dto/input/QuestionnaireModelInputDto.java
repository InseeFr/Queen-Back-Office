package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelData;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record QuestionnaireModelInputDto(

		@IdValid
		String idQuestionnaireModel,
		@NotEmpty
		String label,
		@NotNull
		ObjectNode value,
		Set<String> requiredNomenclatureIds) {

	public static QuestionnaireModelData toModel(QuestionnaireModelInputDto questionnaire) {
		return QuestionnaireModelData.createQuestionnaireWithoutCampaign(
				questionnaire.idQuestionnaireModel,
				questionnaire.label,
				questionnaire.value.toString(),
				questionnaire.requiredNomenclatureIds());
	}
}
