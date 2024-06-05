package fr.insee.queen.application.surveyunit.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Logo")
public record LogoDto(
        String url,
        String label
) {
}
