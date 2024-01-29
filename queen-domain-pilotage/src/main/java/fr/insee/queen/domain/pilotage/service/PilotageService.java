package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;

import java.util.List;


public interface PilotageService {
    /**
     * Check if a campaign is closed
     * @param campaignId campaign id
     * @return true if campaign is closed, false otherwise
     */
    boolean isClosed(String campaignId);

    /**
     * Retrieve survey unit list of a campaign
     * @param campaignId campaign id
     * @return List of {@link SurveyUnitSummary} survey units of the campaign
     */
    List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId);

    /**
     * Retrieve survey unit list for an interviewer
     * @return List of {@link SurveyUnit} survey units of the campaign
     */
    List<SurveyUnit> getInterviewerSurveyUnits();

    /**
     * Retrieve campaigns the user has access to as an interviewer
     * @return List of {@link PilotageCampaign} authorized campaigns
     */
    List<PilotageCampaign> getInterviewerCampaigns();

    /**
     * Check if a user has the habilitation to perform actions on a survey unit in a campaign with a specified role
     * @param surveyUnit survey unit (with campaign) we want to check the habilitation
     * @param role role in which the user want to perform actions
     * @param idep user id
     * @return true if habilitation is granted, false otherwise
     */
    boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep);
}
