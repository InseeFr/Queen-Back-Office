package fr.insee.queen.domain.surveyunit.gateway;

import fr.insee.queen.domain.surveyunit.model.StateData;

import java.util.Optional;

/**
 * Repository for survey unit state data
 */
public interface StateDataRepository {
    /**
     * Find state data for a survey unit
     *
     * @param surveyUnitId survey unit id
     * @return {@link Optional< StateData >} state data of the survey unit
     */
    Optional<StateData> find(String surveyUnitId);

    /**
     * Save (create or update) state data for a survey unit
     * @param surveyUnitId survey unit id
     * @param stateData state data to save
     */
    void save(String surveyUnitId, StateData stateData);

    /**
     * Create state data for a survey unit
     * @param surveyUnitId survey unit id
     * @param stateData state data to create
     */
    void create(String surveyUnitId, StateData stateData);

    /**
     * Check if a state data exists for a survey unit
     * @param surveyUnitId survey unit to check
     * @return true if state data exists, false otherwise
     */
    boolean exists(String surveyUnitId);
}
