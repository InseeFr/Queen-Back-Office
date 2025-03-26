package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.application.campaign.dto.output.CampaignSummaryDto;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.interrogation.dto.output.InterrogationByCampaignDto;
import fr.insee.queen.application.interrogation.dto.output.InterrogationDto;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
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
@ConditionalOnProperty(name = "feature.interviewer-mode.enabled", havingValue="true")
public class InterviewerController {
    private final PilotageComponent pilotageComponent;

    /**
     * Retrieve the campaigns the current user has access to
     *
     * @return List of {@link CampaignSummaryDto}
     */
    @Operation(summary = "Get campaign list for the current user")
    @Parameter(name = "userId", hidden = true)
    @Tag(name = "02. Campaigns")
    @GetMapping(path = "/campaigns")
    @PreAuthorize(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES)
    public List<CampaignSummaryDto> getInterviewerCampaignList(@CurrentSecurityContext(expression = "authentication.name")
                                                                   String userId) {

        List<PilotageCampaign> campaigns = pilotageComponent.getInterviewerCampaigns();
        log.info("{} campaign(s) found for {}", campaigns.size(), userId);

        return campaigns.stream()
                .map(CampaignSummaryDto::fromPilotageModel)
                .toList();
    }

    /**
     * Retrieve all the interrogations of the current interviewer
     *
     * @return List of {@link InterrogationDto} interrogations
     */
    @Operation(summary = "Get list of interrogations linked to the current interviewer")
    @Tag(name = "06. Interrogations")
    @GetMapping("/interrogations/interviewer")
    @PreAuthorize(AuthorityPrivileges.HAS_INTERVIEWER_PRIVILEGES)
    public List<InterrogationDto> getInterviewerInterrogations() {
        // get interrogations for the interviewer
        List<Interrogation> interrogations = pilotageComponent.getInterviewerInterrogations();

        return interrogations.stream()
                .map(InterrogationDto::fromModel)
                .toList();
    }

    /**
     * Retrieve all the interrogations of a campaign
     *
     * @param campaignId the id of campaign
     * @return List of {@link InterrogationByCampaignDto}
     */
    @Operation(summary = "Get list of interrogations for a campaign")
    @Tag(name = "06. Interrogations")
    @GetMapping("/campaign/{id}/interrogations")
    @PreAuthorize(AuthorityPrivileges.HAS_REVIEWER_PRIVILEGES)
    public List<InterrogationByCampaignDto> getListInterrogationByCampaign(@IdValid @PathVariable(value = "id") String campaignId) {
        // get interrogations of a campaign from the pilotage api
        List<InterrogationSummary> interrogations = pilotageComponent.getInterrogationsByCampaign(campaignId);

        if (interrogations.isEmpty()) {
            throw new EntityNotFoundException(String.format("No interrogations for the campaign with id %s", campaignId));
        }

        return interrogations.stream()
                .map(InterrogationByCampaignDto::fromModel)
                .toList();
    }
}
