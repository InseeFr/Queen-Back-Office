package fr.insee.queen.application.integration.controller;

import fr.insee.modelefiliere.ContextDto;
import fr.insee.queen.application.integration.component.ContextIntegrationComponent;
import fr.insee.queen.application.integration.dto.output.IntegrationResultsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling context-related operations.
 * Processes ContextDto from Protools and integrates with the registre.
 */
@RestController
@Tag(name = "01. Integrations", description = "Endpoints for integration")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ContextController {

    private final ContextIntegrationComponent contextIntegrationComponent;


    /**
     * Processes a context DTO from Protools and integrates the collection instruments.
     * For each instrument, retrieves data from the registre and creates necessary entities.
     *
     * @param contextDto the context DTO containing collection instruments
     * @return integration results
     */
    @Operation(summary = "Post context")
    @PostMapping(value = "/context", produces = "application/json")
    public IntegrationResultsDto postContext(@RequestBody @Valid ContextDto contextDto) {
        log.info("Import context: {}", contextDto.getId());
        return contextIntegrationComponent.processContext(contextDto);
    }
}