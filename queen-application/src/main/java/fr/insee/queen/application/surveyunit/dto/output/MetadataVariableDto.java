package fr.insee.queen.application.surveyunit.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MetadataVariable")
public record MetadataVariableDto(
        String name,
        // TODO: Bug with openapi 3.1 and springdoc. Uncomment this when problem solved
        // https://github.com/springdoc/springdoc-openapi/issues/2608
        //@Schema(description = "Variable value", oneOf = {String.class, Boolean.class})
        Object value
) {
}