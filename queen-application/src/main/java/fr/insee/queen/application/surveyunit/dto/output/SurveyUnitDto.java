package fr.insee.queen.application.surveyunit.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SurveyUnit")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SurveyUnitDto(
        String id,
        String questionnaireId,
        ArrayNode personalization,
        ObjectNode data,
        ObjectNode comment,
        StateDataDto stateData) {

    public static SurveyUnitDto createSurveyUnitNOKDto(String id) {
        return new SurveyUnitDto(id, null, null, null, null, null);
    }

    public static SurveyUnitDto createSurveyUnitOKDtoWithStateData(String id, StateData stateData) {
        return new SurveyUnitDto(id, null, null, null, null, StateDataDto.fromModel(stateData));
    }

    public static SurveyUnitDto createSurveyUnitOKDtoWithQuestionnaireModel(String id, String questionnaireModelId) {
        return new SurveyUnitDto(id, questionnaireModelId, null, null, null, null);
    }

    public static SurveyUnitDto fromModel(SurveyUnit surveyUnit) {
        return new SurveyUnitDto(surveyUnit.id(), surveyUnit.questionnaireId(),
                surveyUnit.personalization(),
                surveyUnit.data(),
                surveyUnit.comment(),
                StateDataDto.fromModel(surveyUnit.stateData()));
    }
}
