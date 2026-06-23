package fr.insee.queen.application.integration.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.group.model.Group;
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

    public static Group toModel(CampaignIntegrationData campaign) {
        ObjectNode metadata = campaign.metadata();
        if(campaign.metadata() == null) {
            metadata = JsonNodeFactory.instance.objectNode();
        }
        return new Group(campaign.id.toUpperCase(), campaign.label, metadata);
    }
}
