package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelData;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

public record QuestionnaireModelIntegrationInputDto(
		@IdValid
		String idQuestionnaireModel,
		@IdValid
		String campaignId,
		@NotEmpty
		String label,
		@NotNull
		ObjectNode value,
		Set<String> requiredNomenclatureIds) {

	public static QuestionnaireModelData toModel(QuestionnaireModelIntegrationInputDto questionnaire) {
		Set<String> nomenclatureIds = questionnaire.requiredNomenclatureIds();
		if( nomenclatureIds == null) {
			nomenclatureIds = new HashSet<>();
		}

		return QuestionnaireModelData.createQuestionnaireWithCampaign(
				questionnaire.idQuestionnaireModel,
				questionnaire.label,
				questionnaire.value.toString(),
				nomenclatureIds,
				questionnaire.campaignId());
	}
}
