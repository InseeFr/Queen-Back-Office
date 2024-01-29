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
 * Handle creation of paradata events for a survey unit. Paradatas are data
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
     * Create a paradata event for a survey unit
     *
     * @param paradataValue paradata value
     */
    @Operation(summary = "Create paradata event for a survey unit")
    @PostMapping(path = "/paradata")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @ResponseStatus(HttpStatus.OK)
    public void addParadata(@NotNull @RequestBody ObjectNode paradataValue) {
        String paradataSurveyUnitIdParameter = "idSU";
        if (!paradataValue.has(paradataSurveyUnitIdParameter)) {
            throw new EntityNotFoundException("Paradata does not contain the survey unit id");
        }

        JsonNode surveyUnitNode = paradataValue.get(paradataSurveyUnitIdParameter);
        if (!surveyUnitNode.isTextual() || surveyUnitNode.textValue() == null) {
            throw new EntityNotFoundException("Paradata does not contain the survey unit id");
        }

        String surveyUnitId = surveyUnitNode.textValue();
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        paradataEventService.createParadataEvent(surveyUnitId, paradataValue.toString());
    }
}
