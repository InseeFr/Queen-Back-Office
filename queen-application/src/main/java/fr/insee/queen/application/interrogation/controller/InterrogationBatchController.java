package fr.insee.queen.application.interrogation.controller;

import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.interrogation.dto.input.InterrogationBatchInput;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.domain.interrogation.service.InterrogationBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handle interrogations
 */
@RestController
@Tag(name = "06. Interrogations", description = "Endpoints for interrogations")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class InterrogationBatchController {
    private final InterrogationBatchService interrogationBatchService;

    /**
     * Create or update an interrogation
     *
     * @param campaignId             campaign id
     * @param interrogationBatchInputs interrogations data for creation
     */
    @Operation(summary = "Create/Update interrogations")
    @PostMapping("/campaigns/{id}/interrogations")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public void createUpdateInterrogations(@IdValid @PathVariable(value = "id") String campaignId,
                                                          @NotEmpty @Valid @RequestBody List<InterrogationBatchInput> interrogationBatchInputs) {
        List<Interrogation> interrogations = interrogationBatchInputs.stream()
                .map(interrogationBatchInput -> InterrogationBatchInput.toModel(interrogationBatchInput, campaignId))
                .toList();
        interrogationBatchService.saveInterrogations(interrogations);
    }

    /**
     * Delete interrogations
     *
     * @param interrogationsId interrogations id
     */
    @Operation(summary = "Delete interrogations")
    @PostMapping("/interrogations/delete")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInterrogations(@RequestBody @NotEmpty List<String> interrogationsId) {
        interrogationBatchService.delete(interrogationsId);
    }
}
