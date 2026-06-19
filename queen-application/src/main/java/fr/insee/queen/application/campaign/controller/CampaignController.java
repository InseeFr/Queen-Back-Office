package fr.insee.queen.application.campaign.controller;

import fr.insee.queen.application.campaign.dto.input.CampaignCreationData;
import fr.insee.queen.application.campaign.dto.input.CampaignCreationDataV2;
import fr.insee.queen.application.campaign.dto.output.CampaignDto;
import fr.insee.queen.application.campaign.dto.output.CampaignSummaryDto;
import fr.insee.queen.application.campaign.dto.output.CampaignIdsDto;
import fr.insee.queen.application.configuration.auth.AuthorityPrivileges;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.service.CampaignService;
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
@Tag(name = "02. Groups", description = "Endpoints for groups")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CampaignController {
    private final CampaignService campaignService;

    /**
     * Retrieve all campaigns
     *
     * @return List of all {@link CampaignSummaryDto}
     */
    @Operation(summary = "Get list of all groups")
    @GetMapping(path = "/admin/${application.group.path-plural}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public List<CampaignSummaryDto> getListCampaign() {
        return campaignService.getAllCampaigns()
                .stream().map(CampaignSummaryDto::fromModel)
                .toList();
    }

    /**
     * Retrieve all campaigns ids
     *
     * @return List of all {@link CampaignIdsDto}
     */
    @Operation(summary = "Get list of all group ids")
    @GetMapping(path = "/${application.group.path-plural}/ids")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public List<CampaignIdsDto> getListCampaignsIds() {
        return campaignService.getAllCampaignIds()
                .stream().map(CampaignIdsDto::fromModel)
                .toList();
    }

    /**
     * Retrieve a campaign
     *
     * @return {@link CampaignSummaryDto}
     */
    @Operation(summary = "Get group")
    @GetMapping(path = "/admin/${application.group.path-plural}/{id}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public CampaignDto getCampaign(@IdValid @PathVariable(value = "id") String campaignId) {
        return CampaignDto.fromModel(campaignService.getCampaign(campaignId));
    }

    /**
     * @deprecated
     * Create a new campaign
     *
     * @param campaignInputDto the value to create
     */
    @Deprecated(since = "4.3.0")
    @Operation(summary = "Create a group")
    @PostMapping(path = "/${application.group.path-plural}")
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
    @Operation(summary = "Create a group")
    @PostMapping(path = "/${application.group.path-singular}")
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    @ResponseStatus(HttpStatus.CREATED)
    public void createCampaignV2(@Valid @RequestBody CampaignCreationDataV2 campaignInputDto) {
        campaignService.createCampaign(CampaignCreationDataV2.toModel(campaignInputDto));
    }
}
