package fr.insee.queen.api.controller.integration.component.exception;

import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntegrationValidationException extends Exception {
    private final IntegrationResultErrorUnitDto resultError;
}
