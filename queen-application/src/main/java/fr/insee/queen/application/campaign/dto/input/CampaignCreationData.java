package fr.insee.queen.application.campaign.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.web.validation.IdValid;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

/**
 * Data used for campaign creation
 *
 * @param id campaign id
 * @param label campaign label
 * @param sensitivity campaign sensitivity (classic or sensitive)
 * @param questionnaireIds list of questionnaire ids linked to the campaign
 * @param metadata campaign metadata
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CampaignCreation")
public record CampaignCreationData(
        @IdValid
        String id,
        @NotBlank
        String label,
        CampaignSensitivity sensitivity,
        @NotEmpty
        Set<String> questionnaireIds,
        @Valid
        MetadataCreationData metadata) {

    public static Campaign toModel(CampaignCreationData campaign) {
        ObjectNode metadataValue = JsonNodeFactory.instance.objectNode();
        CampaignSensitivity sensitivityValue = CampaignSensitivity.NORMAL;
        if (campaign.metadata() != null) {
            metadataValue = campaign.metadata.value();
        }
        if(campaign.sensitivity != null) {
            sensitivityValue = campaign.sensitivity();
        }

        return new Campaign(campaign.id, campaign.label, sensitivityValue, campaign.questionnaireIds, metadataValue);
    }
}
