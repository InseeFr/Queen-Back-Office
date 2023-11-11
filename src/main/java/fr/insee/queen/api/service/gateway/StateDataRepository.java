package fr.insee.queen.api.service.gateway;

import fr.insee.queen.api.dto.statedata.StateDataDto;

import java.util.Optional;

/**
 * StateDataRepository is the repository using to access to  StateData table in DB
 *
 * @author Claudel Benjamin
 *
 */
public interface StateDataRepository {
    Optional<StateDataDto> find(String surveyUnitId);
    void update(String surveyUnitId, StateDataDto stateData);
    void create(String surveyUnitId, StateDataDto stateData);
    boolean exists(String surveyUnitId);
}
