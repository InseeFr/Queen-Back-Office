package fr.insee.queen.api.pilotage.service.gateway;

import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;

import java.util.LinkedHashMap;
import java.util.List;

public interface PilotageRepository {
    /**
     * Check if the campaign is closed
     * @param campaignId campaign id
     * @param authToken user token
     * @return true if closed, false otherwise
     */
    boolean isClosed(String campaignId, String authToken);

    /**
     * Retrieve survey units linked to a user for a campaign
     * @param authToken user token
     * @param campaignId campaignId
     * @return List of survey units
     */
    List<LinkedHashMap<String, String>> getSurveyUnits(String authToken, String campaignId);

    /**
     * Retrieve campaigns where user is interviewer
     * @param authToken user token
     * @return List of {@link PilotageCampaign} campaigns
     */
    List<PilotageCampaign> getInterviewerCampaigns(String authToken);

    boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken);
}
