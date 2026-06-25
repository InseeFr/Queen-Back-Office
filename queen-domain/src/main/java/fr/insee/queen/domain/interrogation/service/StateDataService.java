package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidTransitionException;

import java.util.Optional;

public interface StateDataService {
    StateData getStateData(String interrogationId);

    void saveStateData(String interrogationId, StateData stateData, boolean verifyDate) throws StateDataInvalidDateException, StateDataInvalidTransitionException;

    Optional<StateData> findStateData(String interrogationId);
}
