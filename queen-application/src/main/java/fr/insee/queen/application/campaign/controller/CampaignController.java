package fr.insee.queen.application.campaign.controller;

import fr.insee.queen.application.campaign.dto.input.CampaignCreationData;
import fr.insee.queen.application.campaign.dto.input.CampaignCreationDataV2;
import fr.insee.queen.application.campaign.dto.output.CampaignSummaryDto;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.service.CampaignService;
import fr.insee.queen.domain.campaign.service.exception.CampaignDeletionException;
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
    private final CampaignService campaignService;
    private final PilotageComponent pilotageComponent;

    /**
     * Retrieve all campaigns
     *
     * @return List of all {@link CampaignSummaryDto}
     */
    @Operation(summary = "Get list of all campaigns")
    @GetMapping(path = "/admin/campaigns")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public List<CampaignSummaryDto> getListCampaign() {
        return campaignService.getAllCampaigns()
                .stream().map(CampaignSummaryDto::fromModel)
                .toList();
    }

    /**
     * @deprecated
     * Create a new campaign
     *
     * @param campaignInputDto the value to create
     */
    @Deprecated(since = "4.3.0")
    @Operation(summary = "Create a campaign")
    @PostMapping(path = "/campaigns")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void createCampaign(@Valid @RequestBody CampaignCreationData campaignInputDto) {
        campaignService.createCampaign(CampaignCreationData.toModel(campaignInputDto));
    }

    /**
     * Create a new campaign
     *
     * @param campaignInputDto the value to create
     */
    @Operation(summary = "Create a campaign")
    @PostMapping(path = "/campaign")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void createCampaignV2(@Valid @RequestBody CampaignCreationDataV2 campaignInputDto) {
        campaignService.createCampaign(CampaignCreationDataV2.toModel(campaignInputDto));
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
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.OK)
    public void deleteCampaignById(@RequestParam("force") boolean force,
                                   @IdValid @PathVariable(value = "id") String campaignId) {
        if (force || pilotageComponent.isClosed(campaignId)) {
            campaignService.delete(campaignId);
            log.info("Campaign with id {} deleted", campaignId);
            return;
        }

        throw new CampaignDeletionException(String.format("Unable to delete campaign %s, campaign isn't closed", campaignId));
    }
}
