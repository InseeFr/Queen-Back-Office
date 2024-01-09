package fr.insee.queen.application.surveyunit.dto.input;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SurveyUnitCreationData(
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
        StateDataInputData stateData) {

    public static SurveyUnit toModel(SurveyUnitCreationData surveyUnit, String campaignId) {
        return new SurveyUnit(surveyUnit.id,
                campaignId,
                surveyUnit.questionnaireId(),
                surveyUnit.personalization().toString(),
                surveyUnit.data().toString(),
                surveyUnit.comment().toString(),
                StateDataInputData.toModel(surveyUnit.stateData()));
    }
}
