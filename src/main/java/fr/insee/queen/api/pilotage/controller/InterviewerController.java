package fr.insee.queen.api.pilotage.controller;

import fr.insee.queen.api.campaign.controller.dto.output.CampaignSummaryDto;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.configuration.swagger.role.DisplayRolesOnUI;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.controller.dto.output.SurveyUnitByCampaignDto;
import fr.insee.queen.api.surveyunit.controller.dto.output.SurveyUnitDto;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import fr.insee.queen.api.web.authentication.AuthenticationHelper;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import fr.insee.queen.api.web.validation.IdValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
@ConditionalOnProperty(name = "feature.enable.interviewer-collect", havingValue="true")
public class InterviewerController {
    private final AuthenticationHelper authHelper;
    private final PilotageComponent pilotageComponent;

    /**
     * Retrieve the campaigns the current user has access to
     *
     * @return List of {@link CampaignSummaryDto}
     */
    @Operation(summary = "Get campaign list for the current user")
    @Tag(name = "02. Campaigns")
    @GetMapping(path = "/campaigns")
    @DisplayRolesOnUI
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public List<CampaignSummaryDto> getInterviewerCampaignList() {

        String userId = authHelper.getUserId();
        log.info("User {} need his campaigns", userId);

        List<PilotageCampaign> campaigns = pilotageComponent.getInterviewerCampaigns();
        log.info("{} campaign(s) found for {}", campaigns.size(), userId);

        return campaigns.stream()
                .map(CampaignSummaryDto::fromPilotageModel)
                .toList();
    }

    /**
     * Retrieve all the survey units of the current interviewer
     *
     * @return List of {@link SurveyUnitDto} survey units
     */
    @Operation(summary = "Get list of survey units linked to the current interviewer")
    @Tag(name = "06. Survey units")
    @GetMapping(path = "/survey-units/interviewer")
    @DisplayRolesOnUI
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES + "||" + AuthorityRole.HAS_ROLE_INTERVIEWER)
    public List<SurveyUnitDto> getInterviewerSurveyUnits() {
        String userId = authHelper.getUserId();
        log.info("GET survey-units for interviewer with id {}", userId);

        // get survey units for the interviewer
        List<SurveyUnit> surveyUnits = pilotageComponent.getInterviewerSurveyUnits();

        return surveyUnits.stream()
                .map(SurveyUnitDto::fromModel)
                .toList();
    }

    /**
     * Retrieve all the survey units of a campaign
     *
     * @param campaignId the id of campaign
     * @return List of {@link SurveyUnitByCampaignDto}
     */
    @Operation(summary = "Get list of survey units for a campaign")
    @Tag(name = "06. Survey units")
    @GetMapping(path = "/campaign/{id}/survey-units")
    @DisplayRolesOnUI
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public List<SurveyUnitByCampaignDto> getListSurveyUnitByCampaign(@IdValid @PathVariable(value = "id") String campaignId) {
        log.info("GET survey-units for campaign with id {}", campaignId);

        // get survey units of a campaign from the pilotage api
        List<SurveyUnitSummary> surveyUnits = pilotageComponent.getSurveyUnitsByCampaign(campaignId);

        if (surveyUnits.isEmpty()) {
            throw new EntityNotFoundException(String.format("No survey units for the campaign with id %s", campaignId));
        }

        return surveyUnits.stream()
                .map(SurveyUnitByCampaignDto::fromModel)
                .toList();
    }
}
