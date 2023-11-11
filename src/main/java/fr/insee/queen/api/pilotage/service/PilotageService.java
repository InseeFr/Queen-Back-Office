package fr.insee.queen.api.pilotage.service;

import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;

import java.util.List;


public interface PilotageService {
    boolean isClosed(String campaignId, String authToken);

    List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId, String authToken);

    List<PilotageCampaign> getInterviewerCampaigns(String authToken);

    boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken);
}
