package fr.insee.queen.api.surveyunit.controller;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.pilotage.controller.PilotageComponent;
import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.surveyunit.controller.dto.input.StateDataInputData;
import fr.insee.queen.api.surveyunit.controller.dto.output.StateDataDto;
import fr.insee.queen.api.surveyunit.controller.dto.output.SurveyUnitDto;
import fr.insee.queen.api.surveyunit.controller.dto.output.SurveyUnitOkNokDto;
import fr.insee.queen.api.surveyunit.service.StateDataService;
import fr.insee.queen.api.surveyunit.service.SurveyUnitService;
import fr.insee.queen.api.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitState;
import fr.insee.queen.api.web.validation.IdValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handle the data state of a survey unit.
 */
@RestController
@Tag(name = "06. Survey units")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class StateDataController {
    private final StateDataService stateDataService;
    private final SurveyUnitService surveyUnitService;
    private final PilotageComponent pilotageComponent;

    /**
     * Retrieve the data linked of a survey unit
     *
     * @param surveyUnitId the id of the survey unit
     * @return {@link StateDataDto} the data linked to the survey unit
     */
    @Operation(summary = "Get state-data for a survey unit")
    @GetMapping(path = "/survey-unit/{id}/state-data")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public StateDataDto getStateDataBySurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        return StateDataDto.fromModel(stateDataService.getStateData(surveyUnitId));
    }

    /**
     * Update the state-data linked to the survey unit
     *
     * @param stateDataInputDto the value to update
     * @param surveyUnitId      the id of reporting unit
     */
    @Operation(summary = "Update state-data for a survey unit")
    @PutMapping(path = "/survey-unit/{id}/state-data")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public void setStateData(@IdValid @PathVariable(value = "id") String surveyUnitId,
                             @Valid @RequestBody StateDataInputData stateDataInputDto) throws StateDataInvalidDateException {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        stateDataService.saveStateData(surveyUnitId, StateDataInputData.toModel(stateDataInputDto));
    }

    /**
     * Retrieve the state-data list of searched survey units
     *
     * @param surveyUnitIdsToSearch the ids to search
     * @return {@link SurveyUnitOkNokDto} the state-data linked for found survey units, and the list of non found survey units
     */
    @Operation(summary = "Get state-data for all survey-units defined in request body ")
    @PostMapping(path = "survey-units/state-data")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public SurveyUnitOkNokDto getStateDataBySurveyUnits(@NotEmpty @RequestBody List<String> surveyUnitIdsToSearch) {
        List<SurveyUnitState> surveyUnitsFound = surveyUnitService.findWithStateByIds(surveyUnitIdsToSearch);
        List<String> surveyUnitIdsFound = surveyUnitsFound.stream().map(SurveyUnitState::id).toList();
        List<SurveyUnitDto> surveyUnitsNOK = surveyUnitIdsToSearch.stream()
                .filter(surveyUnitIdToSearch -> !surveyUnitIdsFound.contains(surveyUnitIdToSearch))
                .map(SurveyUnitDto::createSurveyUnitNOKDto)
                .toList();
        List<SurveyUnitDto> surveyUnitsOK = surveyUnitsFound.stream()
                .map(su -> SurveyUnitDto.createSurveyUnitOKDtoWithStateData(su.id(), su.stateData()))
                .toList();
        return new SurveyUnitOkNokDto(surveyUnitsOK, surveyUnitsNOK);
    }
}
