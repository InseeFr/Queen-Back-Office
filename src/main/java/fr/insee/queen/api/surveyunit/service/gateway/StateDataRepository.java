package fr.insee.queen.api.surveyunit.service.gateway;

import fr.insee.queen.api.surveyunit.service.model.StateData;

import java.util.Optional;

/**
 * StateDataRepository is the repository using to access to  StateData table in DB
 *
 * @author Claudel Benjamin
 */
public interface StateDataRepository {
    Optional<StateData> find(String surveyUnitId);

    void update(String surveyUnitId, StateData stateData);

    void create(String surveyUnitId, StateData stateData);

    boolean exists(String surveyUnitId);
}
