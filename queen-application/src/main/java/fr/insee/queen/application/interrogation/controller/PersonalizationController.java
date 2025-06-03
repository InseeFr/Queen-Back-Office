package fr.insee.queen.application.interrogation.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.application.web.validation.json.JsonValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.service.PersonalizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Handle personalization data for an interrogation
 */
@RestController
@Tag(name = "06. Interrogations")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PersonalizationController {
    private final PersonalizationService personalizationService;
    private final PilotageComponent pilotageComponent;


    /**
     * Retrieve the personalization data of an interrogation
     *
     * @param interrogationId the id of the interrogation
     * @return {@link ArrayNode} the personalization linked to the interrogation
     */
    @Operation(summary = "Get personalization for an interrogation")
    @GetMapping("/interrogations/{id}/personalization")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(ref = SchemaType.Names.PERSONALIZATION))})
    public ArrayNode getPersonalizationByInterrogation(@IdValid @PathVariable(value = "id") String interrogationId) {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER);
        return personalizationService.getPersonalization(interrogationId);
    }

    /**
     * Update the personalization data linked to the interrogation
     *
     * @param personalizationValues the value to update
     * @param interrogationId          the id of the interrogation
     */
    @Operation(summary = "Update personalization for an interrogation",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    schema = @Schema(ref = SchemaType.Names.PERSONALIZATION))))
    @PutMapping("/interrogations/{id}/personalization")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public void setPersonalization(@IdValid @PathVariable(value = "id") String interrogationId,
                                   @NotNull @RequestBody @JsonValid(SchemaType.PERSONALIZATION) ArrayNode personalizationValues) {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER);
        personalizationService.updatePersonalization(interrogationId, personalizationValues);
    }
}