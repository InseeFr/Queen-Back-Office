package fr.insee.queen.application.interrogation.controller;

import fr.insee.queen.application.campaign.component.MetadataConverter;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.configuration.auth.AuthorityRoleEnum;
import fr.insee.queen.application.interrogation.dto.output.InterrogationBySurveyUnitDto;
import fr.insee.queen.application.interrogation.dto.output.InterrogationStateDto;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.interrogation.controller.exception.LockedResourceException;
import fr.insee.queen.application.interrogation.dto.input.StateDataInput;
import fr.insee.queen.application.interrogation.dto.input.InterrogationCreationInput;
import fr.insee.queen.application.interrogation.dto.input.InterrogationDataStateDataUpdateInput;
import fr.insee.queen.application.interrogation.dto.input.InterrogationUpdateInput;
import fr.insee.queen.application.interrogation.dto.output.InterrogationDto;
import fr.insee.queen.application.interrogation.dto.output.InterrogationMetadataDto;
import fr.insee.queen.application.web.authentication.AuthenticationHelper;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Handle interrogations
 */
@RestController
@Tag(name = "06. Interrogations", description = "Endpoints for interrogations")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class InterrogationController {
    private final InterrogationService interrogationService;
    private final PilotageComponent pilotageComponent;
    private final MetadataConverter metadataConverter;
    private final StateDataService stateDataService;
    private final AuthenticationHelper authenticationUserHelper;

    /**
     * Retrieve all interrogations id
     *
     * @return all ids of interrogations
     */
    @Operation(summary = "Get all interrogations ids")
    @GetMapping(path = "/interrogations")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public List<String> getInterrogationIds() {
        return interrogationService.findAllInterrogationIds();
    }

    /**
     * Retrieve interrogations filtered by state
     *
     * @param campaignId campaign id
     * @param stateDataType state
     * @return all ids of interrogations
     */
    @Operation(summary = "Retrieve interrogations by state")
    @GetMapping("/admin/campaign/{id}/interrogations")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public List<InterrogationStateDto> getInterrogationsByState(
            @IdValid @PathVariable("id") String campaignId,
            @RequestParam(required = false, name="state") StateDataType stateDataType) {
        return interrogationService
                .getInterrogations(campaignId, stateDataType)
                .stream()
                .map(InterrogationStateDto::fromModel)
                .toList();
    }

    /**
     * Retrieve interrogations filtered by survey-unit
     *
     * @param surveyUnitId
     * @return all {@link InterrogationBySurveyUnitDto}
     */
    @Operation(summary = "Retrieve interrogations by survey-unit")
    @GetMapping("/survey-units/{id}/interrogations")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public List<InterrogationBySurveyUnitDto> getInterrogationsBySurveyUnit(
            @IdValid @PathVariable("id") String surveyUnitId) {
        return interrogationService
                .findSummariesBySurveyUnitId(surveyUnitId)
                .stream()
                .map(InterrogationBySurveyUnitDto::fromModel)
                .toList();
    }

    /**
     * Retrieve the interrogation
     *
     * @param interrogationId interrogation id
     * @return {@link InterrogationDto} the interrogation
     */
    @Operation(summary = "Get interrogation")
    @GetMapping("/interrogations/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public InterrogationDto getInterrogationById(@IdValid @PathVariable(value = "id") String interrogationId) {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER);
        InterrogationSummary interrogationSummary = interrogationService.getSummaryById(interrogationId);

        // if campaign sensitivity is OFF, return data
        if(interrogationSummary.campaign().getSensitivity().equals(CampaignSensitivity.NORMAL)) {
            return InterrogationDto.fromModel(interrogationService.getInterrogation(interrogationId));
        }

        // here, campaign sensitivity is ON !

        // admin can see everything
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.ADMIN, AuthorityRoleEnum.WEBCLIENT)){
            return InterrogationDto.fromModel(interrogationService.getInterrogation(interrogationId));
        }

        // interviewer retrieve the dto with filled or empty data
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.SURVEY_UNIT)){
            Interrogation su = interrogationService.getInterrogation(interrogationId);
            StateData stateData = su.stateData();
            if(stateData == null) {
                return InterrogationDto.fromModel(su);
            }

            // survey is finished, not returning interrogation data
            if(StateDataType.EXTRACTED.equals(stateData.state())
                    || StateDataType.VALIDATED.equals(stateData.state())) {
                return InterrogationDto.fromSensitiveModel(su);
            }

            return InterrogationDto.fromModel(su);
        }

        // reviewer cannot see data
        throw new AccessDeniedException("Not authorized to see interrogation data");
    }

    /**
     * Retrieve the interrogation metadata
     *
     * @param interrogationId interrogation id
     * @return {@link InterrogationMetadataDto} the interrogation
     */
    @Operation(summary = "Get interrogation metadata")
    @GetMapping("/interrogations/{id}/metadata")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public InterrogationMetadataDto getInterrogationMetadataById(@IdValid @PathVariable(value = "id") String interrogationId) {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER);
        InterrogationMetadata interrogationMetadata = interrogationService.getInterrogationMetadata(interrogationId);
        return InterrogationMetadataDto.fromModel(interrogationMetadata, metadataConverter);
    }

    /**
     * Update an interrogation
     *
     * @param interrogationId         interrogation id
     * @param interrogationUpdateInput interrogation form data
     */
    @Operation(summary = "Update interrogation")
    @PutMapping("/interrogations/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_INTERVIEWER_PRIVILEGES)
    public void updateInterrogationById(@IdValid @PathVariable(value = "id") String interrogationId,
                                     @Valid @RequestBody InterrogationUpdateInput interrogationUpdateInput) throws LockedResourceException {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER);

        InterrogationSummary interrogationSummary = interrogationService.getSummaryById(interrogationId);

        // if campaign sensitivity is OFF, update data
        if(interrogationSummary.campaign().getSensitivity().equals(CampaignSensitivity.NORMAL)) {
            Interrogation interrogation = InterrogationUpdateInput.toModel(interrogationId, interrogationUpdateInput);
            interrogationService.updateInterrogation(interrogation);
            return;
        }

        // here, campaign sensitivity is ON !

        // admin can see everything
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.ADMIN, AuthorityRoleEnum.WEBCLIENT)){
            Interrogation interrogation = InterrogationUpdateInput.toModel(interrogationId, interrogationUpdateInput);
            interrogationService.updateInterrogation(interrogation);
            return;
        }

        // interviewer can update data if survey is not ended
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.INTERVIEWER)){
            Optional<StateDataType> validatedState = stateDataService
                    .findStateData(interrogationId)
                    .map(StateData::state)
                    .filter(state -> StateDataType.EXTRACTED.equals(state)
                            || StateDataType.VALIDATED.equals(state));

            if (validatedState.isEmpty()) {
                Interrogation interrogation = InterrogationUpdateInput.toModel(interrogationId, interrogationUpdateInput);
                interrogationService.updateInterrogation(interrogation);
                return;
            }
            throw new LockedResourceException(interrogationId);
        }

        throw new AccessDeniedException("Not authorized to update interrogation data");
    }

    /**
     * Create or update an interrogation
     *
     * @param campaignId             campaign id
     * @param interrogationCreationInput interrogation data for creation
     */
    @Operation(summary = "Create/Update an interrogation")
    @PostMapping({"/campaign/{id}/interrogation", "/campaigns/{id}/interrogation"})
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public ResponseEntity<Void> createUpdateInterrogation(@IdValid @PathVariable(value = "id") String campaignId,
                                                          @Valid @RequestBody InterrogationCreationInput interrogationCreationInput) throws StateDataInvalidDateException {
        Interrogation interrogation = InterrogationCreationInput.toModel(interrogationCreationInput, campaignId);
        if (interrogationService.existsById(interrogationCreationInput.id())) {
            log.info("Update interrogation with id {}", interrogationCreationInput.id());
            interrogationService.updateInterrogation(interrogation);
            log.debug("Interrogation with id {} updated", interrogationCreationInput.id());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        log.info("Create interrogation with id {}", interrogationCreationInput.id());
        interrogationService.createInterrogation(interrogation);
        log.debug("Interrogation with id {} created", interrogationCreationInput.id());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Create or update an interrogation
     *
     * @param campaignId             campaign id
     * @param interrogationCreationInput interrogation data for creation
     */
    @Operation(summary = "Create an interrogation")
    @PostMapping({"/campaigns/{id}/interrogations/create"})
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public void createInterrogation(@IdValid @PathVariable(value = "id") String campaignId,
                                                          @Valid @RequestBody InterrogationCreationInput interrogationCreationInput) throws StateDataInvalidDateException {
        Interrogation interrogation = InterrogationCreationInput.toModel(interrogationCreationInput, campaignId);
        log.info("Create interrogation with id {}", interrogationCreationInput.id());
        interrogationService.createInterrogation(interrogation);
        log.debug("Interrogation with id {} created", interrogationCreationInput.id());
    }

    @Operation(summary = "Update interrogation updated data/state-data")
    @PatchMapping("/interrogations/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_SURVEY_UNIT_PRIVILEGES)
    public void updateInterrogationDataStateDataById(@IdValid @PathVariable(value = "id") String interrogationId,
                                                  @Valid @RequestBody InterrogationDataStateDataUpdateInput interrogationUpdateInput) throws LockedResourceException {
        pilotageComponent.checkHabilitations(interrogationId, PilotageRole.INTERVIEWER);

        InterrogationSummary interrogationSummary = interrogationService.getSummaryById(interrogationId);

        // if campaign sensitivity is OFF, update data
        if(interrogationSummary.campaign().getSensitivity().equals(CampaignSensitivity.NORMAL)) {
            StateData stateData = StateDataInput.toModel(interrogationUpdateInput.stateData());
            interrogationService.updateInterrogation(interrogationId, interrogationUpdateInput.data(), stateData);
            return;
        }

        // here, campaign sensitivity is ON !

        // admin can do everything
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.ADMIN, AuthorityRoleEnum.WEBCLIENT)){
            StateData stateData = StateDataInput.toModel(interrogationUpdateInput.stateData());
            interrogationService.updateInterrogation(interrogationId, interrogationUpdateInput.data(), stateData);
            return;
        }

        // interviewer/interrogation can update data if survey is not ended
        if(authenticationUserHelper.hasRole(AuthorityRoleEnum.INTERVIEWER, AuthorityRoleEnum.SURVEY_UNIT)){
            Optional<StateDataType> validatedState = stateDataService
                    .findStateData(interrogationId)
                    .map(StateData::state)
                    .filter(state -> StateDataType.EXTRACTED.equals(state)
                            || StateDataType.VALIDATED.equals(state));

            if (validatedState.isEmpty()) {
                StateData stateData = StateDataInput.toModel(interrogationUpdateInput.stateData());
                interrogationService.updateInterrogation(interrogationId, interrogationUpdateInput.data(), stateData);
                return;
            }
            throw new LockedResourceException(interrogationId);
        }
        throw new AccessDeniedException("Not authorized to update interrogation data");
    }


    /**
     * Delete an interrogation
     *
     * @param interrogationId interrogation id
     */
    @Operation(summary = "Delete an interrogation")
    @DeleteMapping("/interrogations/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInterrogation(@IdValid @PathVariable(value = "id") String interrogationId) {
        interrogationService.delete(interrogationId);
    }
}
