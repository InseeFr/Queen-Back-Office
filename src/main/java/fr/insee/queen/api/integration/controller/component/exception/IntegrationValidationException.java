package fr.insee.queen.api.integration.controller.component.exception;

import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntegrationValidationException extends Exception {
    private final IntegrationResultUnitDto resultError;
}
