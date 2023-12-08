package fr.insee.queen.api.campaign.controller.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.queen.api.campaign.service.model.CampaignSummary;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class CampaignSummaryDto {
    @JsonProperty
    private String id;
    @JsonProperty
    private List<String> questionnaireIds;

    public static CampaignSummaryDto fromModel(CampaignSummary campaign) {
        return new CampaignSummaryDto(campaign.getId(), campaign.getQuestionnaireIds().stream().toList());
    }

    public static CampaignSummaryDto fromPilotageModel(PilotageCampaign campaign) {
        return new CampaignSummaryDto(campaign.id(), campaign.questionnaireIds().stream().toList());
    }
}
