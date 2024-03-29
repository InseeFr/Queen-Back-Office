package fr.insee.queen.application.surveyunit.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.surveyunit.dto.input.StateDataInput;
import fr.insee.queen.application.surveyunit.dto.input.SurveyUnitDataStateDataUpdateInput;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.service.DataService;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "06. Survey units", description = "Endpoints for survey units")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
@ConditionalOnExpression(value = "${feature.perfdata.enabled} == true")
public class PerfDataController {
    private final SurveyUnitService surveyUnitService;
    private final PilotageComponent pilotageComponent;
    private final DataService dataService;

    /**
     * Update a survey unit data/state-data
     *
     * @param surveyUnitId         survey unit id
     * @param surveyUnitUpdateInput survey unit form data/state data
     */
    @Operation(summary = "Update survey-unit data/state-data")
    @PatchMapping(path = {"/survey-unit/{id}"})
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public void updateSurveyUnitDataStateDataById(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                                  @Valid @RequestBody SurveyUnitDataStateDataUpdateInput surveyUnitUpdateInput) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER);
        StateData stateData = StateDataInput.toModel(surveyUnitUpdateInput.stateData());
        surveyUnitService.updateSurveyUnit(surveyUnitId, surveyUnitUpdateInput.data(), stateData);
    }

    /**
     * Update the collected data of a survey unit
     *
     * @param collectedDataValue the collected form data to update
     * @param surveyUnitId the id of the survey unit
     */
    @Operation(summary = "Update collected data for a survey unit")
    @PatchMapping(path = "/survey-unit/{id}/data")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public void updateCollectedData(@NotNull @RequestBody ObjectNode collectedDataValue,
                                    @IdValid @PathVariable(value = "id") String surveyUnitId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        dataService.updateCollectedData(surveyUnitId, collectedDataValue);
    }
}
