package fr.insee.queen.api.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.queen.api.dto.statedata.StateDataDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SurveyUnitWithStateDto(
        String id,
        StateDataDto stateData) {
}
