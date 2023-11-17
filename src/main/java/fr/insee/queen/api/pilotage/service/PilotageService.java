package fr.insee.queen.api.pilotage.service;

import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;

import java.util.List;


public interface PilotageService {
    /**
     * Check if a campaign is closed
     * @param campaignId campaign id
     * @param authToken token of current user
     * @return true if campaign is closed, false otherwise
     */
    boolean isClosed(String campaignId, String authToken);

    /**
     * Retrieve survey unit list of a campaign
     * @param campaignId campaign id
     * @param authToken user token
     * @return List of {@link SurveyUnitSummary} survey units of the campaign
     */
    List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId, String authToken);

    /**
     * Retrieve survey unit list for an interviewer
     * @param authToken user token
     * @return List of {@link SurveyUnit} survey units of the campaign
     */
    List<SurveyUnit> getInterviewerSurveyUnits(String authToken);

    /**
     * Retrieve campaigns the user has access to as an interviewer
     * @param authToken user token
     * @return List of {@link PilotageCampaign} authorized campaigns
     */
    List<PilotageCampaign> getInterviewerCampaigns(String authToken);

    /**
     * Check if a user has the habilitation to perform actions on a survey unit in a campaign with a specified role
     * @param surveyUnit survey unit (with campaign) we want to check the habilitation
     * @param role role in which the user want to perform actions
     * @param idep user id
     * @param authToken token of the user
     * @return true if habilitation is granted, false otherwise
     */
    boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken);
}
