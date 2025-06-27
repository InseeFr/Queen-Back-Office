package fr.insee.queen.application.interrogation.controller;

import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.interrogation.controller.exception.LockedResourceException;
import fr.insee.queen.application.interrogation.dto.input.StateDataInput;
import fr.insee.queen.application.interrogation.dto.output.StateDataDto;
import fr.insee.queen.application.interrogation.dto.output.InterrogationDto;
import fr.insee.queen.application.interrogation.dto.output.InterrogationOkNokDto;
import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.model.InterrogationState;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
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
 * Handle the data state of an interrogation.
 */
@RestController
@Tag(name = "06. Interrogations")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class StateDataController {
    private final StateDataService stateDataService;
    private final InterrogationService interrogationService;
    private final PilotageComponent pilotageComponent;
    private final AuthenticationHelper authenticationUserHelper;

    /**
     * Retrieve the data linked of an interrogation
     *
     * @param interrogationId the id of the interrogation
     * @return {@link StateDataDto} the data linked to the interrogation
     */
    @Operation(summary = "Get state-data for an interrogation")
    @GetMapping("/interrogations/{id}/state-data")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public StateDataDto getStateDataByInterrogation(@IdValid @PathVariable(value = "id") String interrogationId) {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER);
        return StateDataDto.fromModel(stateDataService.getStateData(interrogationId));
    }

    /**
     * Update the state-data linked to the interrogation
     *
     * @param stateDataInputDto the value to update
     * @param interrogationId      the id of reporting unit
     */
    @Operation(summary = "Update state-data for an interrogation")
    @PutMapping("/interrogations/{id}/state-data")
    @PreAuthorize(AuthorityPrivileges.HAS_SURVEY_UNIT_PRIVILEGES)
    public void setStateData(@IdValid @PathVariable(value = "id") String interrogationId,
                             @Valid @RequestBody StateDataInput stateDataInputDto) throws StateDataInvalidDateException, LockedResourceException {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER);
        InterrogationSummary interrogationSummary = interrogationService.getSummaryById(interrogationId);

        // if campaign sensitivity is OFF, update data
        if(interrogationSummary.campaign().getSensitivity().equals(CampaignSensitivity.NORMAL)) {
            stateDataService.saveStateData(interrogationId, StateDataInput.toModel(stateDataInputDto));
            return;
        }

        // here, campaign sensitivity is ON !

        // admin can do everything
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.ADMIN, AuthorityRoleEnum.WEBCLIENT)){
            stateDataService.saveStateData(interrogationId, StateDataInput.toModel(stateDataInputDto));
            return;
        }

        // interviewer/survey-unit can update data if survey is not ended
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.SURVEY_UNIT)){
            Optional<StateDataType> validatedState = stateDataService
                    .findStateData(interrogationId)
                    .map(StateData::state)
                    .filter(state -> StateDataType.EXTRACTED.equals(state)
                            || StateDataType.VALIDATED.equals(state));

            if (validatedState.isEmpty()) {
                stateDataService.saveStateData(interrogationId, StateDataInput.toModel(stateDataInputDto));
                return;
            }
            throw new LockedResourceException(interrogationId);
        }
        throw new AccessDeniedException("Not authorized to update interrogation data");
    }

    /**
     * Retrieve the state-data list of searched interrogations
     *
     * @param interrogationIdsToSearch the ids to search
     * @return {@link InterrogationOkNokDto} the state-data linked for found interrogations, and the list of non found interrogations
     */
    @Operation(summary = "Get state-data for all interrogations defined in request body ")
    @PostMapping("interrogations/state-data")
    @PreAuthorize(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES)
    public InterrogationOkNokDto getStateDataByInterrogations(@NotEmpty @RequestBody List<String> interrogationIdsToSearch) {
        List<InterrogationState> interrogationsFound = interrogationService.findWithStateByIds(interrogationIdsToSearch);
        List<String> interrogationIdsFound = interrogationsFound.stream().map(InterrogationState::id).toList();
        List<InterrogationDto> interrogationsNOK = interrogationIdsToSearch.stream()
                .filter(interrogationIdToSearch -> !interrogationIdsFound.contains(interrogationIdToSearch))
                .map(InterrogationDto::createInterrogationNOKDto)
                .toList();
        List<InterrogationDto> interrogationsOK = interrogationsFound.stream()
                .map(su -> InterrogationDto.createInterrogationOKDtoWithStateData(su.id(), su.stateData()))
                .toList();
        return new InterrogationOkNokDto(interrogationsOK, interrogationsNOK);
    }
}
