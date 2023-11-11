package fr.insee.queen.api.integration.controller.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.web.validation.IdValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignIntegrationInputDto(
        @IdValid
        String id,
        @NotBlank
        String label,
        @NotNull
        ObjectNode metadata) {

    public static Campaign toModel(CampaignIntegrationInputDto campaign) {
        ObjectNode metadataValue = JsonNodeFactory.instance.objectNode();
        if (campaign.metadata() != null) {
            metadataValue = campaign.metadata;
        }
        return new Campaign(campaign.id, campaign.label, metadataValue.toString());
    }
}
