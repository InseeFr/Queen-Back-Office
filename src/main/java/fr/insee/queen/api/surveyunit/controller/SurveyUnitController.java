package fr.insee.queen.api.surveyunit.controller;

import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.pilotage.controller.HabilitationComponent;
import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.PilotageService;
import fr.insee.queen.api.surveyunit.controller.dto.input.SurveyUnitCreationData;
import fr.insee.queen.api.surveyunit.controller.dto.input.SurveyUnitUpdateData;
import fr.insee.queen.api.surveyunit.controller.dto.output.SurveyUnitByCampaignDto;
import fr.insee.queen.api.surveyunit.controller.dto.output.SurveyUnitDto;
import fr.insee.queen.api.surveyunit.service.SurveyUnitService;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import fr.insee.queen.api.web.authentication.AuthenticationHelper;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import fr.insee.queen.api.web.validation.IdValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@AllArgsConstructor
@Validated
public class SurveyUnitController {
    @Value("${application.pilotage.integration-override}")
    private final String integrationOverride;
    private final SurveyUnitService surveyUnitService;
    private final PilotageService pilotageService;
    private final HabilitationComponent habilitationComponent;
    private final AuthenticationHelper authHelper;

    /**
     * Retrieve all survey units id
     *
     * @return all ids of survey units
     */
    @Operation(summary = "Get all survey-units ids")
    @GetMapping(path = "/survey-units")
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    public List<String> getSurveyUnitIds() {
        log.info("GET survey-units");
        return surveyUnitService.findAllSurveyUnitIds();
    }

    /**
     * Retrieve the survey unit
     *
     * @param surveyUnitId survey unit id
     * @param auth         authenticated user
     * @return {@link SurveyUnitDto} the survey unit
     */
    @Operation(summary = "Get survey-unit")
    @GetMapping(path = "/survey-unit/{id}")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public SurveyUnitDto getSurveyUnitById(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                           Authentication auth) {
        log.info("GET survey-units with id {}", surveyUnitId);
        habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER, PilotageRole.REVIEWER);
        return SurveyUnitDto.fromModel(surveyUnitService.getSurveyUnit(surveyUnitId));
    }

    /**
     * Update a survey unit
     *
     * @param surveyUnitId         survey unit id
     * @param surveyUnitUpdateData survey unit form data
     * @param auth                 authenticated user
     */
    @Operation(summary = "Update survey-unit")
    @PutMapping(path = {"/survey-unit/{id}"})
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES + "||" + AuthorityRole.HAS_ROLE_INTERVIEWER)
    public void updateSurveyUnitById(@IdValid @PathVariable(value = "id") String surveyUnitId,
                                     @Valid @RequestBody SurveyUnitUpdateData surveyUnitUpdateData,
                                     Authentication auth) {
        log.info("PUT survey-unit for reporting unit with id {}", surveyUnitId);
        habilitationComponent.checkHabilitations(auth, surveyUnitId, PilotageRole.INTERVIEWER);
        SurveyUnit surveyUnit = SurveyUnitUpdateData.toModel(surveyUnitId, surveyUnitUpdateData);
        surveyUnitService.updateSurveyUnit(surveyUnit);
    }

    /**
     * Retrieve all the survey units of a campaign
     *
     * @param campaignId the id of campaign
     * @param auth       authenticated user
     * @return List of {@link SurveyUnitByCampaignDto}
     */
    @Operation(summary = "Get list of survey units for a campaign")
    @GetMapping(path = "/campaign/{id}/survey-units")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public List<SurveyUnitByCampaignDto> getListSurveyUnitByCampaign(@IdValid @PathVariable(value = "id") String campaignId,
                                                                     Authentication auth) {
        log.info("GET survey-units for campaign with id {}", campaignId);

        List<SurveyUnitSummary> surveyUnits;
        if (integrationOverride != null && integrationOverride.equals("true")) {
            surveyUnits = surveyUnitService.findByCampaignId(campaignId);
        } else {
            String authToken = authHelper.getAuthToken(auth);
            // get survey units of a campaign from the pilotage api
            surveyUnits = pilotageService.getSurveyUnitsByCampaign(campaignId, authToken);
        }

        if (surveyUnits.isEmpty()) {
            throw new EntityNotFoundException(String.format("No survey units for the campaign with id %s", campaignId));
        }

        return surveyUnits.stream()
                .map(SurveyUnitByCampaignDto::fromModel)
                .toList();
    }

    /**
     * Create or update a survey unit
     *
     * @param campaignId             campaign id
     * @param surveyUnitCreationData survey unit data for creation
     */
    @Operation(summary = "Create/Update a survey unit")
    @PostMapping(path = "/campaign/{id}/survey-unit")
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    public ResponseEntity<Void> createUpdateSurveyUnit(@IdValid @PathVariable(value = "id") String campaignId,
                                                       @Valid @RequestBody SurveyUnitCreationData surveyUnitCreationData) {
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
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSurveyUnit(@IdValid @PathVariable(value = "id") String surveyUnitId) {
        log.info("DELETE survey-unit with id {}", surveyUnitId);
        surveyUnitService.delete(surveyUnitId);
    }
}
