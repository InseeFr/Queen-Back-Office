package fr.insee.queen.api.integration.controller.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;
import fr.insee.queen.api.web.validation.IdValid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

public record QuestionnaireModelIntegrationData(
        @IdValid
        String idQuestionnaireModel,
        @IdValid
        String campaignId,
        @NotEmpty
        String label,
        @NotNull
        ObjectNode value,
        Set<String> requiredNomenclatureIds) {

    public static QuestionnaireModel toModel(QuestionnaireModelIntegrationData questionnaire) {
        Set<String> nomenclatureIds = questionnaire.requiredNomenclatureIds();
        if (nomenclatureIds == null) {
            nomenclatureIds = new HashSet<>();
        }

        return QuestionnaireModel.createQuestionnaireWithCampaign(
                questionnaire.idQuestionnaireModel,
                questionnaire.label,
                questionnaire.value.toString(),
                nomenclatureIds,
                questionnaire.campaignId());
    }
}
