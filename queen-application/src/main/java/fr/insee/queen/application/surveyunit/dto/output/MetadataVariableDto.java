package fr.insee.queen.application.surveyunit.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MetadataVariable")
public record MetadataVariableDto(
        String name,
        @Schema(description = "Variable value", oneOf = {String.class, Boolean.class})
        Object value
) {
}