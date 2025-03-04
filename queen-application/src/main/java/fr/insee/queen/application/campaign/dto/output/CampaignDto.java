package fr.insee.queen.application.campaign.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@Schema(name = "Campaign")
public class CampaignDto {
    @JsonProperty
    private String id;
    @JsonProperty
    private CampaignSensitivity sensitivity;
    @JsonProperty
    private List<String> questionnaireIds;
    @JsonProperty
    private ObjectNode metadata;


    public static CampaignDto fromModel(Campaign campaign) {
        return new CampaignDto(campaign.getId(),
                campaign.getSensitivity(),
                campaign.getQuestionnaireIds().stream().toList(),
                campaign.getMetadata());
    }
}
