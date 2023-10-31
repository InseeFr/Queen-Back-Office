package fr.insee.queen.api.controller.integration.component.exception;

import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class IntegrationValidationsException extends Exception {
    private final List<IntegrationResultErrorUnitDto> resultErrors;
}
