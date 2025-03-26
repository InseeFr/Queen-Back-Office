package fr.insee.queen.application.interrogationtempzone.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.interrogationtempzone.dto.output.InterrogationTempZoneDto;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.interrogationtempzone.service.InterrogationTempZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handle temp zone for interrogations. The temp zone is used when interviewers synchronized orphan interrogations
 */
@RestController
@Tag(name = "08. Interrogations in temp Zone", description = "Endpoints for interrogations in temporary zone")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class InterrogationTempZoneController {
    private final InterrogationTempZoneService interrogationTempZoneService;

    /**
     * Create a interrogation to the temp zone area
     *
     * @param interrogationId interrogation id
     * @param interrogation   interrogation json
     */
    @Operation(summary = "Create interrogation to temp-zone",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    schema = @Schema(ref = SchemaType.Names.INTERROGATION_TEMP_ZONE))))
    @Parameter(name = "userId", hidden = true)
    @PostMapping("/interrogation/{id}/temp-zone")
    @PreAuthorize(AuthorityPrivileges.HAS_INTERVIEWER_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void postInterrogationByIdInTempZone(@IdValid @PathVariable(value = "id") String interrogationId,
                                             @RequestBody
                                             ObjectNode interrogation,
                                             @CurrentSecurityContext(expression = "authentication.name") String userId) {
        interrogationTempZoneService.saveInterrogationToTempZone(interrogationId, userId, interrogation);
    }

    /**
     * Retrieve all interrogations in temp zone
     *
     * @return List of {@link InterrogationTempZoneDto} interrogations
     */
    @Operation(summary = "GET all interrogations in temp-zone")
    @GetMapping("/interrogations/temp-zone")
    @PreAuthorize(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES)
    public List<InterrogationTempZoneDto> getInterrogationsInTempZone() {
        return interrogationTempZoneService.getAllInterrogationTempZone()
                .stream().map(InterrogationTempZoneDto::fromModel)
                .toList();
    }
}
