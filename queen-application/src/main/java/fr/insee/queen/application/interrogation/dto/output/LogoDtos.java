package fr.insee.queen.application.interrogation.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "Logos")
public record LogoDtos(
        @NotNull
        LogoDto main,
        List<LogoDto> secondaries
) {

    public static LogoDtos createWithMainLogoOnly(LogoDto mainLogo) {
        return new LogoDtos(mainLogo, null);
    }

    public static LogoDtos create(LogoDto mainLogo, List<LogoDto> secondaryLogos) {
        return new LogoDtos(mainLogo, secondaryLogos);
    }
}
