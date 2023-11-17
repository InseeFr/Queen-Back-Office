package fr.insee.queen.api.campaign.controller;

import fr.insee.queen.api.campaign.controller.dto.input.CampaignCreationData;
import fr.insee.queen.api.campaign.controller.dto.output.CampaignSummaryDto;
import fr.insee.queen.api.campaign.service.CampaignService;
import fr.insee.queen.api.campaign.service.exception.CampaignDeletionException;
import fr.insee.queen.api.configuration.auth.AuthorityRole;
import fr.insee.queen.api.pilotage.controller.PilotageComponent;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.web.authentication.AuthenticationHelper;
import fr.insee.queen.api.web.validation.IdValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handle campaigns
 */
@RestController
@Tag(name = "02. Campaigns", description = "Endpoints for campaigns")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CampaignController {
    private final AuthenticationHelper authHelper;
    private final CampaignService campaignService;
    private final PilotageComponent pilotageComponent;

    /**
     * Retrieve all campaigns
     *
     * @return List of all {@link CampaignSummaryDto}
     */
    @Operation(summary = "Get list of all campaigns")
    @GetMapping(path = "/admin/campaigns")
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    public List<CampaignSummaryDto> getListCampaign() {
        String userId = authHelper.getUserId();
        log.info("Admin {} request all campaigns", userId);
        return campaignService.getAllCampaigns()
                .stream().map(CampaignSummaryDto::fromModel)
                .toList();
    }

    /**
     * Retrieve the campaigns the current user has access to (or all campaigns if admin)
     *
     * @return List of {@link CampaignSummaryDto}
     */
    @Operation(summary = "Get campaign list for the current user")
    @GetMapping(path = "/campaigns")
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
     * Create a new campaign
     *
     * @param campaignInputDto the value to create
     */
    @Operation(summary = "Create a campaign")
    @PostMapping(path = "/campaigns")
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void createCampaign(@Valid @RequestBody CampaignCreationData campaignInputDto) {
        String userId = authHelper.getUserId();
        log.info("User {} requests campaign {} creation", userId, campaignInputDto.id());
        campaignService.createCampaign(CampaignCreationData.toModel(campaignInputDto));
    }

    /**
     * Delete a campaign. The deletion is processed in two cases:
     * - the campaign is closed (check on pilotage api)
     * - pilotage api is disabled or force option is set to true
     *
     * @param force      force the full deletion of the campaign (without checking if campaign is closed in pilotage api)
     * @param campaignId campaign id
     */
    @Operation(summary = "Delete a campaign")
    @DeleteMapping(path = "/campaign/{id}")
    @PreAuthorize(AuthorityRole.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.OK)
    public void deleteCampaignById(@RequestParam("force") boolean force,
                                   @IdValid @PathVariable(value = "id") String campaignId) {
        String userId = authHelper.getUserId();
        log.info("Admin {} requests deletion of campaign {}", userId, campaignId);

        if (force || pilotageComponent.isClosed(campaignId)) {
            campaignService.delete(campaignId);
            log.info("Campaign with id {} deleted", campaignId);
            return;
        }

        throw new CampaignDeletionException(String.format("Unable to delete campaign %s, campaign isn't closed", campaignId));
    }
}
