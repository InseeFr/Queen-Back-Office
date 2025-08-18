package fr.insee.queen.domain.interrogation.gateway;

import fr.insee.queen.domain.interrogation.model.StateData;

import java.util.Optional;

/**
 * Repository for interrogation state data
 */
public interface StateDataRepository {
    /**
     * Find state data for an interrogation
     *
     * @param interrogationId interrogation id
     * @return {@link Optional<StateData>} state data of the interrogation
     */
    Optional<StateData> find(String interrogationId);

    /**
     * Save (create or update) state data for an interrogation
     * @param interrogationId interrogation id
     * @param stateData state data to save
     */
    void save(String interrogationId, StateData stateData);

    /**
     * Create state data for an interrogation
     * @param interrogationId interrogation id
     * @param stateData state data to create
     */
    void create(String interrogationId, StateData stateData);

    /**
     * Check if a state data exists for an interrogation
     * @param interrogationId interrogation to check
     * @return true if state data exists, false otherwise
     */
    boolean exists(String interrogationId);
}
