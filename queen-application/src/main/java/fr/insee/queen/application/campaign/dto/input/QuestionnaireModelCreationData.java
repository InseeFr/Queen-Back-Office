package fr.insee.queen.application.campaign.dto.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Questionnaire data used to create questionnaire
 * @param idQuestionnaireModel questionnaire id
 * @param label questionnaire label
 * @param value json data structure of the questionnaire
 * @param requiredNomenclatureIds nomenclature ids linked to the questionnaire
 */
@Schema(name = "QuestionnaireModelCreation")
public record QuestionnaireModelCreationData(

        @IdValid
        String idQuestionnaireModel,
        @NotEmpty
        String label,
        @NotNull
        ObjectNode value,
        Set<String> requiredNomenclatureIds) {

    public static QuestionnaireModel toModel(QuestionnaireModelCreationData questionnaire) {
        Set<String> nomenclatureIds = questionnaire.requiredNomenclatureIds();
        if (nomenclatureIds == null) {
            nomenclatureIds = new HashSet<>();
        }

        return QuestionnaireModel.createQuestionnaireWithoutCampaign(
                questionnaire.idQuestionnaireModel,
                questionnaire.label,
                questionnaire.value,
                nomenclatureIds);
    }
}
