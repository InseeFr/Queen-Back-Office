package fr.insee.queen.application.interrogation.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "InterrogationsOkNok")
public record InterrogationOkNokDto(
        List<InterrogationDto> interrogationOK,
        List<InterrogationDto> interrogationNOK) {
}
