package fr.insee.queen.api.service.pilotage;

import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto;

import java.util.List;


public interface PilotageService {
    boolean isClosed(String campaignId, String authToken);
    List<SurveyUnitSummaryDto> getSurveyUnitsByCampaign(String campaignId, String authToken);
    List<CampaignSummaryDto> getInterviewerCampaigns(String authToken);
    boolean hasHabilitation(SurveyUnitHabilitationDto surveyUnit, PilotageRole role, String idep, String authToken);
}
