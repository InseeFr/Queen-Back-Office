package fr.insee.queen.application.pilotage.controller;

import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;

import java.util.List;

public interface PilotageComponent {
    /**
     * Check if the current user has defined roles for an interrogation
     *
     * @param interrogationId the interrogation the user want to access
     * @param roles        the roles the current user should have to access the interrogation
     */
    void checkHabilitations(String interrogationId, PilotageRole... roles);

    /**
     * Check if a campaign is closed
     * @param campaignId campaign id
     * @return true if campaign is closed, false otherwise
     */
    boolean isClosed(String campaignId);

    /**
     * Retrieve interrogation list of a campaign
     * @param campaignId campaign id
     * @return List of {@link InterrogationSummary} interrogations of the campaign
     */
    List<InterrogationSummary> getInterrogationsByCampaign(String campaignId);

    /**
     * Retrieve campaigns the user has access to as an interviewer
     * @return List of {@link PilotageCampaign} authorized campaigns
     */
    List<PilotageCampaign> getInterviewerCampaigns();

    /**
     * Retrieve interrogation list for an interviewer
     * @return List of {@link Interrogation} interrogations of the campaign
     */
    List<Interrogation> getInterviewerInterrogations();
}
