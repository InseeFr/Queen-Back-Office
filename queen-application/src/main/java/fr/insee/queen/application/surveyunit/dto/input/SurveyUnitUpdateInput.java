package fr.insee.queen.application.surveyunit.dto.input;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.json.JsonValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@Schema(name = "SurveyUnitUpdate")
public record SurveyUnitUpdateInput(
        @Schema(ref = SchemaType.Names.PERSONALIZATION)
        @JsonValid(SchemaType.PERSONALIZATION)
        ArrayNode personalization,
        @Schema(ref = SchemaType.Names.DATA)
        @JsonValid(SchemaType.DATA)
        ObjectNode data,
        ObjectNode comment,
        @Valid
        StateDataForSurveyUnitUpdateInput stateData) {
    public static SurveyUnit toModel(String surveyUnitId, SurveyUnitUpdateInput surveyUnit) {
        ArrayNode personalization = surveyUnit.personalization();
        ObjectNode comment = surveyUnit.comment();
        ObjectNode data = surveyUnit.data();
        return SurveyUnit.createForUpdate(surveyUnitId, personalization, comment, data,
                StateDataForSurveyUnitUpdateInput.toModel(surveyUnit.stateData()));
    }
}
