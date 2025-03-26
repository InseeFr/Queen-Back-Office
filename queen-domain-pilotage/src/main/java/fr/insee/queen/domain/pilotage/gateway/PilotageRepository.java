package fr.insee.queen.domain.pilotage.gateway;

import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.model.PilotageInterrogation;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;

import java.util.List;

public interface PilotageRepository {
    /**
     * Check if the campaign is closed
     * @param campaignId campaign id
     * @return true if closed, false otherwise
     */
    boolean isClosed(String campaignId);

    /**
     * Retrieve interrogations linked to a user
     *
     * @return List of interrogations
     */
    List<PilotageInterrogation> getInterrogations();

    /**
     * Retrieve campaigns where user is interviewer
     * @return List of {@link PilotageCampaign} campaigns
     */
    List<PilotageCampaign> getInterviewerCampaigns();

    boolean hasHabilitation(InterrogationSummary interrogation, PilotageRole role, String idep);
}
