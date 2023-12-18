package fr.insee.queen.api.surveyunit.service;

import fr.insee.queen.api.surveyunit.service.model.StateData;

public interface StateDataService {
    StateData getStateData(String surveyUnitId);

    void saveStateData(String surveyUnitId, StateData stateData);
}
