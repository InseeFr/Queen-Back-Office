package fr.insee.queen.api.dto.surveyunit;

import fr.insee.queen.api.dto.campaign.CampaignDto;
import fr.insee.queen.api.dto.statedata.StateDataDto;

public record SurveyUnitDepositProofDto(String id, CampaignDto campaign, StateDataDto stateData) {
}
