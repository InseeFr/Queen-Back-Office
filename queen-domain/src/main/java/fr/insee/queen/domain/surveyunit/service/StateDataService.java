package fr.insee.queen.domain.surveyunit.service;

import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.surveyunit.model.StateData;

import java.util.Optional;

public interface StateDataService {
    StateData getStateData(String surveyUnitId);

    void saveStateData(String surveyUnitId, StateData stateData, boolean verifyDate) throws StateDataInvalidDateException;

    Optional<StateData> findStateData(String surveyUnitId);
}
