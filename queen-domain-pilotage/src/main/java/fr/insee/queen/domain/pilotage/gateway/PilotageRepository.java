package fr.insee.queen.domain.pilotage.gateway;

import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.model.PilotageSurveyUnit;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;

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
     * Retrieve survey units linked to a user
     *
     * @param authToken user token
     * @return List of survey units
     */
    List<PilotageSurveyUnit> getSurveyUnits(String authToken);

    /**
     * Retrieve campaigns where user is interviewer
     * @param authToken user token
     * @return List of {@link PilotageCampaign} campaigns
     */
    List<PilotageCampaign> getInterviewerCampaigns(String authToken);

    boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken);
}
