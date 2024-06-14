package fr.insee.queen.application.surveyunit.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SurveyUnitsOkNok")
public record SurveyUnitOkNokDto(
        List<SurveyUnitDto> surveyUnitOK,
        List<SurveyUnitDto> surveyUnitNOK) {
}
