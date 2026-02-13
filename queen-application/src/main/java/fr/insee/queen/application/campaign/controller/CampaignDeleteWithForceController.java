package fr.insee.queen.application.campaign.controller;

import fr.insee.queen.application.pilotage.controller.PilotageComponent;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.service.CampaignService;
import fr.insee.queen.domain.campaign.service.exception.CampaignDeletionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Handle campaigns deletions, with full deletion (used in dev/test environments)
 */
@RestController
@Tag(name = "02. Campaigns", description = "Endpoints for campaigns")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
@Validated
@ConditionalOnProperty(name = "application.campaign.check-interrogations-on-delete", havingValue="false")
public class CampaignDeleteWithForceController {

    private final CampaignService campaignService;
    private final PilotageComponent pilotageComponent;

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
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCampaignById(@RequestParam("force") boolean force,
                                   @IdValid @PathVariable(value = "id") String campaignId) {
        if (force || pilotageComponent.isClosed(campaignId)) {
            campaignService.delete(campaignId, true);
            log.info("Campaign with id {} deleted", campaignId);
            return;
        }

        throw new CampaignDeletionException(String.format("Unable to delete campaign %s, campaign isn't closed", campaignId));
    }
}
