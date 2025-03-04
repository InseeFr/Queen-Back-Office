package fr.insee.queen.domain.surveyunit.service;

import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.surveyunit.model.StateData;

public interface StateDataService {
    StateData getStateData(String surveyUnitId);

    void saveStateData(String surveyUnitId, StateData stateData) throws StateDataInvalidDateException;
}
