package fr.insee.queen.application.paradata.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.paradata.service.ParadataEventService;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Handle creation of paradata events for an interrogation. Paradatas are data
 * giving additional information about how the user is filling the questionnaire
 */
@RestController
@Tag(name = "07. Paradata events", description = "Endpoints for paradata events")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ParadataEventController {
    private final ParadataEventService paradataEventService;
    private final PilotageComponent pilotageComponent;

    /**
     * Create a paradata event for an interrogation
     *
     * @param paradataValue paradata value
     */
    @Operation(summary = "Create paradata event for an interrogation")
    @PostMapping(path = "/paradata")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @ResponseStatus(HttpStatus.OK)
    public void addParadata(@NotNull @RequestBody ObjectNode paradataValue) {
        String paradataInterrogationIdParameter = "idInterrogation";
        if (!paradataValue.has(paradataInterrogationIdParameter)) {
            throw new EntityNotFoundException("Paradata does not contain the interrogation id");
        }

        JsonNode interrogationNode = paradataValue.get(paradataInterrogationIdParameter);
        if (!interrogationNode.isTextual() || interrogationNode.textValue() == null) {
            throw new EntityNotFoundException("Paradata does not contain the interrogation id");
        }

        String interrogationId = interrogationNode.textValue();
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER);
        paradataEventService.createParadataEvent(interrogationId, paradataValue);
    }
}
