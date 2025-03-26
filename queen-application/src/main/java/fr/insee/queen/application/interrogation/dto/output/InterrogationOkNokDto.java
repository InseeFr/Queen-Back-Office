package fr.insee.queen.application.interrogation.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "InterrogationsOkNok")
public record InterrogationOkNokDto(
        @JsonProperty("surveyUnitOK")
        List<InterrogationDto> interrogationOK,
        @JsonProperty("surveyUnitNOK")
        List<InterrogationDto> interrogationNOK) {
}
