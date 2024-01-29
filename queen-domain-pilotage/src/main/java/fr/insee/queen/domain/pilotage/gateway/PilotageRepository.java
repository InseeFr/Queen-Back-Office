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
     * @return true if closed, false otherwise
     */
    boolean isClosed(String campaignId);

    /**
     * Retrieve survey units linked to a user
     *
     * @return List of survey units
     */
    List<PilotageSurveyUnit> getSurveyUnits();

    /**
     * Retrieve campaigns where user is interviewer
     * @return List of {@link PilotageCampaign} campaigns
     */
    List<PilotageCampaign> getInterviewerCampaigns();

    boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep);
}
