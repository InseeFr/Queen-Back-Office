package fr.insee.queen.application.surveyunit.controller;

import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.surveyunit.dto.input.SurveyUnitCreationData;
import fr.insee.queen.application.surveyunit.dto.input.SurveyUnitUpdateData;
import fr.insee.queen.application.surveyunit.dto.output.SurveyUnitDto;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
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
     * Update a survey unit
     *
     * @param surveyUnitId         survey unit id
     * @param surveyUnitUpdateData survey unit form data
     */
    @Operation(summary = "Update survey-unit")
    @PutMapping(path = {"/survey-unit/{id}"})
    @PreAuthorize(AuthorityPrivileges.HAS_INTERVIEWER_PRIVILEGES)
    public void updateSurveyUnitById(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                     @Valid @RequestBody SurveyUnitUpdateData surveyUnitUpdateData) {
        pilotageComponent.checkHabilitations(surveyUnitId, PilotageRole.INTERVIEWER);
        SurveyUnit surveyUnit = SurveyUnitUpdateData.toModel(surveyUnitId, surveyUnitUpdateData);
        surveyUnitService.updateSurveyUnit(surveyUnit);
    }

    /**
     * Create or update a survey unit
     *
     * @param campaignId             campaign id
     * @param surveyUnitCreationData survey unit data for creation
     */
    @Operation(summary = "Create/Update a survey unit")
    @PostMapping(path = "/campaign/{id}/survey-unit")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public ResponseEntity<Void> createUpdateSurveyUnit(@IdValid @PathVariable(value = "id") String campaignId,
                                                       @Valid @RequestBody SurveyUnitCreationData surveyUnitCreationData) throws StateDataInvalidDateException {
        SurveyUnit surveyUnit = SurveyUnitCreationData.toModel(surveyUnitCreationData, campaignId);
        if (surveyUnitService.existsById(surveyUnitCreationData.id())) {
            log.info("Update survey-unit with id {}", surveyUnitCreationData.id());
            surveyUnitService.updateSurveyUnit(surveyUnit);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        log.info("Create survey-unit with id {}", surveyUnitCreationData.id());
        surveyUnitService.createSurveyUnit(surveyUnit);
        return new ResponseEntity<>(HttpStatus.CREATED);
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
