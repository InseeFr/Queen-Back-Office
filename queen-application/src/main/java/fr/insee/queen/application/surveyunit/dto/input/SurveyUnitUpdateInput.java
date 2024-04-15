package fr.insee.queen.application.surveyunit.dto.input;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@Schema(name = "SurveyUnitUpdate")
public record SurveyUnitUpdateInput(
        ArrayNode personalization,
        ObjectNode data,
        ObjectNode comment,
        @Valid
        StateDataInput stateData) {
    public static SurveyUnit toModel(String surveyUnitId, SurveyUnitUpdateInput surveyUnit) {
        ArrayNode personalization = surveyUnit.personalization();
        ObjectNode comment = surveyUnit.comment();
        ObjectNode data = surveyUnit.data();
        return SurveyUnit.createForUpdate(surveyUnitId, personalization, comment, data, StateDataInput.toModel(surveyUnit.stateData()));
    }
}
