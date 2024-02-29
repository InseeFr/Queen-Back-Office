package fr.insee.queen.application.surveyunit.dto.input;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import jakarta.validation.Valid;

public record SurveyUnitUpdateInput(
        ArrayNode personalization,
        ObjectNode data,
        ObjectNode comment,
        @Valid
        StateDataInput stateData) {
    public static SurveyUnit toModel(String surveyUnitId, SurveyUnitUpdateInput surveyUnit) {
        String personalization = surveyUnit.personalization() == null ? null : surveyUnit.personalization().toString();
        String comment = surveyUnit.comment() == null ? null : surveyUnit.comment().toString();
        String data = surveyUnit.data() == null ? null : surveyUnit.data().toString();
        return SurveyUnit.createForUpdate(surveyUnitId, personalization, comment, data, StateDataInput.toModel(surveyUnit.stateData()));
    }
}
