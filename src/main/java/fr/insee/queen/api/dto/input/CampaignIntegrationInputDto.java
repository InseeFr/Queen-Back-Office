package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.domain.CampaignData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignIntegrationInputDto(
		@IdValid
		String id,
		@NotBlank
		String label,
		@NotNull
		ObjectNode metadata){

	public static CampaignData toModel(CampaignIntegrationInputDto campaign) {
		ObjectNode metadataValue = JsonNodeFactory.instance.objectNode();
		if(campaign.metadata() != null) {
			metadataValue = campaign.metadata;
		}
		return new CampaignData(campaign.id, campaign.label, metadataValue.toString());
	}
}
