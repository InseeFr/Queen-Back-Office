package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.pilotage.model.PilotageGroup;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;

import java.util.List;


public interface PilotageService {
    /**
     * Check if a group is closed
     * @param groupId group id
     * @return true if group is closed, false otherwise
     */
    boolean isClosed(String groupId);

    /**
     * Retrieve interrogation list of a group
     * @param groupId group id
     * @return List of {@link InterrogationSummary} interrogations of the group
     */
    List<InterrogationSummary> getInterrogations(String groupId);

    /**
     * Retrieve interrogation list for an interviewer
     * @return List of {@link Interrogation} interrogations of the group
     */
    List<Interrogation> getInterviewerInterrogations();

    /**
     * Retrieve groups the user has access to as an interviewer
     * @return List of {@link PilotageGroup} authorized groups
     */
    List<PilotageGroup> getInterviewerGroups();
}
