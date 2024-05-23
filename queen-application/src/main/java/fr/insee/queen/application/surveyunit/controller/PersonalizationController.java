package fr.insee.queen.application.surveyunit.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.application.web.validation.json.JsonValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.service.PersonalizationService;
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
 * Handle personalization data for a survey unit
 */
@RestController
@Tag(name = "06. Survey units")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PersonalizationController {
    private final PersonalizationService personalizationService;
    private final PilotageComponent pilotageComponent;


    /**
     * Retrieve the personalization data of a survey unit
     *
     * @param surveyUnitId the id of the survey unit
     * @return {@link ArrayNode} the personalization linked to the survey unit
     */
    @Operation(summary = "Get personalization for a survey unit")
    @GetMapping(path = "/survey-unit/{id}/personalization")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(ref = SchemaType.Names.PERSONALIZATION))})
    public ArrayNode getPersonalizationBySurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        return personalizationService.getPersonalization(surveyUnitId);
    }

    /**
     * Update the personalization data linked to the survey unit
     *
     * @param personalizationValues the value to update
     * @param surveyUnitId          the id of the survey unit
     */
    @Operation(summary = "Update personalization for a survey unit",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    schema = @Schema(ref = SchemaType.Names.PERSONALIZATION))))
    @PutMapping(path = "/survey-unit/{id}/personalization")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public void setPersonalization(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                   @NotNull @RequestBody @JsonValid(SchemaType.PERSONALIZATION) ArrayNode personalizationValues) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        personalizationService.updatePersonalization(surveyUnitId, personalizationValues);
    }
}