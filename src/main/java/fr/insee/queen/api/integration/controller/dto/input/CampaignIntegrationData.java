package fr.insee.queen.api.integration.controller.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.web.validation.IdValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignIntegrationData(
        @IdValid
        String id,
        @NotBlank
        String label,
        @NotNull
        ObjectNode metadata) {

    public static Campaign toModel(CampaignIntegrationData campaign) {
        return new Campaign(campaign.id, campaign.label, campaign.metadata.toString());
    }
}
