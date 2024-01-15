package fr.insee.queen.application.integration.component.exception;

import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntegrationValidationException extends Exception {
    private final IntegrationResultUnitDto resultError;
}
