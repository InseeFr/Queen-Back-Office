package fr.insee.queen.api.pilotage.service.gateway;

import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;

import java.util.LinkedHashMap;
import java.util.List;

public interface PilotageRepository {
    boolean isClosed(String campaignId, String authToken);

    List<LinkedHashMap<String, String>> getCurrentSurveyUnit(String authToken, String campaignId);

    List<PilotageCampaign> getInterviewerCampaigns(String authToken);

    boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken);
}
