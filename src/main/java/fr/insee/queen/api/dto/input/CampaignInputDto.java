package fr.insee.queen.api.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.api.controller.validation.IdValid;
import fr.insee.queen.api.domain.CampaignData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignInputDto(
		@IdValid
		String id,
		@NotBlank
		String label,
		@NotEmpty
		Set<String> questionnaireIds,
		@Valid
		MetadataInputDto metadata){

		public static CampaignData toModel(CampaignInputDto campaign) {
			ObjectNode metadataValue = JsonNodeFactory.instance.objectNode();
			if(campaign.metadata() != null) {
				metadataValue = campaign.metadata.value();
			}
			return new CampaignData(campaign.id, campaign.label, campaign.questionnaireIds, metadataValue.toString());
		}
}
