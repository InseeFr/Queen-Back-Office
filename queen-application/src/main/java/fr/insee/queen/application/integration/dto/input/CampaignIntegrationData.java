package fr.insee.queen.application.integration.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.model.Campaign;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CampaignIntegration")
public record CampaignIntegrationData(
        @IdValid
        String id,
        @NotBlank
        String label,
        ObjectNode metadata) {

    public static Campaign toModel(CampaignIntegrationData campaign) {
        ObjectNode metadata = campaign.metadata();
        if(campaign.metadata() == null) {
            metadata = JsonNodeFactory.instance.objectNode();
        }
        return new Campaign(campaign.id.toUpperCase(), campaign.label, metadata);
    }
}
