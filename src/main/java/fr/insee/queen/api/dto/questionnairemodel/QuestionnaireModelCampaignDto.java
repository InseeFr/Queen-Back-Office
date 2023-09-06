package fr.insee.queen.api.dto.questionnairemodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.queen.api.dto.campaign.CampaignDto;

@JsonInclude
public record QuestionnaireModelCampaignDto(@JsonProperty("questionnaireId") String id, CampaignDto campaign) {

}
