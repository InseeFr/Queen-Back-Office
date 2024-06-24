package fr.insee.queen.application.surveyunit.controller;

import fr.insee.queen.application.campaign.component.MetadataComponentConverter;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.surveyunit.dto.input.StateDataInput;
import fr.insee.queen.application.surveyunit.dto.input.SurveyUnitCreationInput;
import fr.insee.queen.application.surveyunit.dto.input.SurveyUnitDataStateDataUpdateInput;
import fr.insee.queen.application.surveyunit.dto.input.SurveyUnitUpdateInput;
import fr.insee.queen.application.surveyunit.dto.output.SurveyUnitDto;
import fr.insee.queen.application.surveyunit.dto.output.SurveyUnitMetadataDto;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitMetadata;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handle survey units
 */
@RestController
@Tag(name = "06. Survey units", description = "Endpoints for survey units")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class SurveyUnitController {
    private final SurveyUnitService surveyUnitService;
    private final PilotageComponent pilotageComponent;
    private final MetadataComponentConverter metadataConverter;

    /**
     * Retrieve all survey units id
     *
     * @return all ids of survey units
     */
    @Operation(summary = "Get all survey-units ids")
    @GetMapping(path = "/survey-units")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public List<String> getSurveyUnitIds() {
        return surveyUnitService.findAllSurveyUnitIds();
    }

    /**
     * Retrieve the survey unit
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitDto} the survey unit
     */
    @Operation(summary = "Get survey-unit")
    @GetMapping(path = "/survey-unit/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public SurveyUnitDto getSurveyUnitById(@IdValid @PathVariable(value = "id") String surveyUnitId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER);
        return SurveyUnitDto.fromModel(surveyUnitService.getSurveyUnit(surveyUnitId));
    }

    /**
     * Retrieve the survey unit metadata
     *
     * @param surveyUnitId survey unit id
     * @return {@link SurveyUnitMetadataDto} the survey unit
     */
    @Operation(summary = "Get survey-unit metadata")
    @GetMapping(path = "/survey-unit/{id}/metadata")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public SurveyUnitMetadataDto getSurveyUnitMetadataById(@IdValid @PathVariable(value = "id") String surveyUnitId) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER);
        SurveyUnitMetadata surveyUnitMetadata = surveyUnitService.getSurveyUnitMetadata(surveyUnitId);
        return SurveyUnitMetadataDto.fromModel(surveyUnitMetadata, metadataConverter);
    }

    /**
     * Update a survey unit
     *
     * @param surveyUnitId         survey unit id
     * @param surveyUnitUpdateInput survey unit form data
     */
    @Operation(summary = "Update survey-unit")
    @PutMapping(path = {"/survey-unit/{id}"})
    @PreAuthorize(AuthorityPrivileges.HAS_INTERVIEWER_PRIVILEGES)
    public void updateSurveyUnitById(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                     @Valid @RequestBody SurveyUnitUpdateInput surveyUnitUpdateInput) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        SurveyUnit surveyUnit = SurveyUnitUpdateInput.toModel(surveyUnitId, surveyUnitUpdateInput);
        surveyUnitService.updateSurveyUnit(surveyUnit);
    }

    /**
     * Create or update a survey unit
     *
     * @param campaignId             campaign id
     * @param surveyUnitCreationInput survey unit data for creation
     */
    @Operation(summary = "Create/Update a survey unit")
    @PostMapping(path = "/campaign/{id}/survey-unit")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public ResponseEntity<Void> createUpdateSurveyUnit(@IdValid @PathVariable(value = "id") String campaignId,
                                                       @Valid @RequestBody SurveyUnitCreationInput surveyUnitCreationInput) throws StateDataInvalidDateException {
        SurveyUnit surveyUnit = SurveyUnitCreationInput.toModel(surveyUnitCreationInput, campaignId);
        if (surveyUnitService.existsById(surveyUnitCreationInput.id())) {
            log.info("Update survey-unit with id {}", surveyUnitCreationInput.id());
            surveyUnitService.updateSurveyUnit(surveyUnit);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        log.info("Create survey-unit with id {}", surveyUnitCreationInput.id());
        surveyUnitService.createSurveyUnit(surveyUnit);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Update survey-unit updated data/state-data")
    @PatchMapping(path = {"/survey-unit/{id}"})
    @PreAuthorize(AuthorityPrivileges.HAS_SURVEY_UNIT_PRIVILEGES)
    public void updateSurveyUnitDataStateDataById(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                                  @Valid @RequestBody SurveyUnitDataStateDataUpdateInput surveyUnitUpdateInput) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        StateData stateData = StateDataInput.toModel(surveyUnitUpdateInput.stateData());
        surveyUnitService.updateSurveyUnit(surveyUnitId, surveyUnitUpdateInput.data(), stateData);
    }


    /**
     * Delete a survey unit
     *
     * @param surveyUnitId survey unit id
     */
    @Operation(summary = "Delete a survey unit")
    @DeleteMapping(path = "/survey-unit/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId) {
        surveyUnitService.delete(surveyUnitId);
    }
}
