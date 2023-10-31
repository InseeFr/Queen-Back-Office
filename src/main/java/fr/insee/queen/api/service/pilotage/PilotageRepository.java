package fr.insee.queen.api.service.pilotage;

import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto;

import java.util.LinkedHashMap;
import java.util.List;

public interface PilotageRepository {
   boolean isClosed(String campaignId, String authToken);
   List<LinkedHashMap<String, String>> getCurrentSurveyUnit(String authToken, String campaignId);
   List<CampaignSummaryDto> getInterviewerCampaigns(String authToken);
   boolean hasHabilitation(SurveyUnitHabilitationDto surveyUnit, PilotageRole role, String idep, String authToken);
}
