package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;

import java.util.List;

public interface PilotageComponent {
    /**
     * Check if the current user has defined roles for a survey unit
     *
     * @param surveyUnitId the survey unit the user want to access
     * @param roles        the roles the current user should have to access the survey unit
     */
    void checkHabilitations(String surveyUnitId, PilotageRole... roles);

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
     * Retrieve campaigns the user has access to as an interviewer
     * @return List of {@link PilotageCampaign} authorized campaigns
     */
    List<PilotageCampaign> getInterviewerCampaigns();

    /**
     * Retrieve survey unit list for an interviewer
     * @return List of {@link SurveyUnit} survey units of the campaign
     */
    List<SurveyUnit> getInterviewerSurveyUnits();
}
