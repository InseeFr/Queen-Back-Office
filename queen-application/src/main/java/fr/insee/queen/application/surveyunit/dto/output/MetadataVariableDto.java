package fr.insee.queen.application.surveyunit.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "MetadataVariable")
public record MetadataVariableDto(
        @NotNull
        String name,
        // TODO: Bug with openapi 3.1 and springdoc. Uncomment this when problem solved
        // https://github.com/springdoc/springdoc-openapi/issues/2608
        //@Schema(description = "Variable value", oneOf = {String.class, Boolean.class})
        @NotNull
        Object value
) {
}