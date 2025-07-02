package fr.insee.queen.application.surveyunit.controller;

import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.surveyunit.controller.exception.LockedResourceException;
import fr.insee.queen.application.surveyunit.dto.input.StateDataInput;
import fr.insee.queen.application.surveyunit.dto.output.StateDataDto;
import fr.insee.queen.application.surveyunit.dto.output.SurveyUnitDto;
import fr.insee.queen.application.surveyunit.dto.output.SurveyUnitOkNokDto;
import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitState;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.domain.surveyunit.service.StateDataService;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    private final AuthenticationHelper authenticationUserHelper;

    /**
     * Retrieve the data linked of a survey unit
     *
     * @param surveyUnitId the id of the survey unit
     * @return {@link StateDataDto} the data linked to the survey unit
     */
    @Operation(summary = "Get state-data for a survey unit")
    @GetMapping(path = "/survey-unit/{id}/state-data")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
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
    @PreAuthorize(AuthorityPrivileges.HAS_SURVEY_UNIT_PRIVILEGES)
    public void setStateData(@IdValid @PathVariable(value = "id") String surveyUnitId,
                             @Valid @RequestBody StateDataInput stateDataInputDto) throws StateDataInvalidDateException, LockedResourceException {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        SurveyUnitSummary surveyUnitSummary = surveyUnitService.getSummaryById(surveyUnitId);

        // if campaign sensitivity is OFF, update data
        if(surveyUnitSummary.campaign().getSensitivity().equals(CampaignSensitivity.NORMAL)) {
            stateDataService.saveStateData(surveyUnitId, StateDataInput.toModel(stateDataInputDto), false);
            return;
        }

        // here, campaign sensitivity is ON !

        // admin can do everything
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.ADMIN, AuthorityRoleEnum.WEBCLIENT)){
            stateDataService.saveStateData(surveyUnitId, StateDataInput.toModel(stateDataInputDto), false);
            return;
        }

        // interviewer/survey-unit can update data if survey is not ended
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.SURVEY_UNIT)){
            Optional<StateDataType> validatedState = stateDataService
                    .findStateData(surveyUnitId)
                    .map(StateData::state)
                    .filter(state -> StateDataType.EXTRACTED.equals(state)
                            || StateDataType.VALIDATED.equals(state));

            if (validatedState.isEmpty()) {
                stateDataService.saveStateData(surveyUnitId, StateDataInput.toModel(stateDataInputDto), false);
                return;
            }
            throw new LockedResourceException(surveyUnitId);
        }
        throw new AccessDeniedException("Not authorized to update survey unit data");
    }

    /**
     * Retrieve the state-data list of searched survey units
     *
     * @param surveyUnitIdsToSearch the ids to search
     * @return {@link SurveyUnitOkNokDto} the state-data linked for found survey units, and the list of non found survey units
     */
    @Operation(summary = "Get state-data for all survey-units defined in request body ")
    @PostMapping(path = "survey-units/state-data")
    @PreAuthorize(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES)
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
