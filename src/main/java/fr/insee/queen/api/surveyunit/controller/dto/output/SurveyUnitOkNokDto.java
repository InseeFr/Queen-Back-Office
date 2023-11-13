package fr.insee.queen.api.surveyunit.controller.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SurveyUnitOkNokDto(
        List<SurveyUnitDto> surveyUnitOK,
        List<SurveyUnitDto> surveyUnitNOK) {
}
