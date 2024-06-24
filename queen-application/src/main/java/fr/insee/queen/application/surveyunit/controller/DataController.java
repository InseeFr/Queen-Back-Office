package fr.insee.queen.application.surveyunit.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.application.web.validation.json.JsonValid;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.service.DataService;
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
 * These endpoints handle the questionnaire form data of a survey unit
 */
@RestController
@Tag(name = "06. Survey units")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class DataController {
    private final DataService dataService;
    private final PilotageComponent pilotageComponent;

    /**
     * Retrieve the questionnaire form data of a survey unit
     *
     * @param surveyUnitId the id of reporting unit
     * @return {@link String} the questionnaire form data of a survey unit
     */
    @Operation(summary = "Get data for a survey unit")
    @GetMapping(path = "/survey-unit/{id}/data")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(ref = SchemaType.Names.DATA))})
    public ObjectNode getDataBySurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        return dataService.getData(surveyUnitId);
    }


    /**
     * Update the questionnaire form data of a survey unit
     *
     * @param dataValue    the questionnaire form data to update
     * @param surveyUnitId the id of the survey unit
     */
    @Operation(summary = "Update data for a survey unit",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    schema = @Schema(ref = SchemaType.Names.DATA))))
    @PutMapping(path = "/survey-unit/{id}/data")
    @PreAuthorize(AuthorityPrivileges.HAS_SURVEY_UNIT_PRIVILEGES)
    public void updateData(
            @NotNull
            @RequestBody
            @JsonValid(SchemaType.DATA)
            ObjectNode dataValue,
            @IdValid
            @PathVariable(value = "id")
            String surveyUnitId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        dataService.saveData(surveyUnitId, dataValue);
    }
}
