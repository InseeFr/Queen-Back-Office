package fr.insee.queen.application.surveyunit.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "Logo")
public record LogoDto(
        @NotNull
        String url,
        @NotNull
        String label
) {
}
