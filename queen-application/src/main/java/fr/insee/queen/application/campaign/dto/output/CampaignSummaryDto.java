package fr.insee.queen.application.campaign.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@Schema(name = "CampaignSummary")
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
