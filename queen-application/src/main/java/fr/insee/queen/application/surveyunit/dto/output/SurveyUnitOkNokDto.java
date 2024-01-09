package fr.insee.queen.application.surveyunit.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SurveyUnitOkNokDto(
        List<SurveyUnitDto> surveyUnitOK,
        List<SurveyUnitDto> surveyUnitNOK) {
}
