package fr.insee.queen.application.campaign.controller;

import fr.insee.queen.application.configuration.auth.AuthorityRole;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.service.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handle the campaign metadata
 */
@RestController
@Tag(name = "05. Metadata", description = "Endpoints for retrieving metadata")
@RequestMapping(path = "/api")
@Slf4j
@AllArgsConstructor
@Validated
public class MetadataController {

    private final MetadataService metadataService;

    /**
     * Retrieve metadata linked to a campaign
     *
     * @param campaignId the id of the campaign
     * @return the metadata linked to the campaign
     */
    @Operation(summary = "Get metadata for a campaign ")
    @GetMapping(path = "/campaign/{id}/metadata")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public String getMetadataByCampaignId(@IdValid @PathVariable(value = "id") String campaignId) {
        return metadataService.getMetadata(campaignId);
    }

    /**
     * Retrieve the campaign metadata by the questionnaire id
     *
     * @param questionnaireId the id of the questionnaire
     * @return the cmapaign metadata
     */
    @Operation(summary = "Get metadata for a questionnaire ")
    @GetMapping(path = "/questionnaire/{id}/metadata")
    @PreAuthorize(AuthorityRole.HAS_ANY_ROLE)
    public String getMetadataByQuestionnaireId(@IdValid @PathVariable(value = "id") String questionnaireId) {
        return metadataService.getMetadataByQuestionnaireId(questionnaireId);
    }
}
