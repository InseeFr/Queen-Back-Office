package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.interrogation.model.StateData;

import java.util.Optional;

public interface StateDataService {
    StateData getStateData(String interrogationId);

    void saveStateData(String interrogationId, StateData stateData) throws StateDataInvalidDateException;

    Optional<StateData> findStateData(String interrogationId);
}
