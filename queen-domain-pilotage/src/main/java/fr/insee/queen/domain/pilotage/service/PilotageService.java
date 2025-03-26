package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;

import java.util.List;


public interface PilotageService {
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
     * Retrieve interrogation list for an interviewer
     * @return List of {@link Interrogation} interrogations of the campaign
     */
    List<Interrogation> getInterviewerInterrogations();

    /**
     * Retrieve campaigns the user has access to as an interviewer
     * @return List of {@link PilotageCampaign} authorized campaigns
     */
    List<PilotageCampaign> getInterviewerCampaigns();

    /**
     * Check if a user has the habilitation to perform actions on a interrogation in a campaign with a specified role
     * @param interrogation interrogation (with campaign) we want to check the habilitation
     * @param role role in which the user want to perform actions
     * @param idep user id
     * @return true if habilitation is granted, false otherwise
     */
    boolean hasHabilitation(InterrogationSummary interrogation, PilotageRole role, String idep);
}
