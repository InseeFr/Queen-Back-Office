package fr.insee.queen.application.integration.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Schema(name = "QuestionnaireModelIntegration")
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
                questionnaire.value,
                nomenclatureIds,
                questionnaire.campaignId());
    }
}
