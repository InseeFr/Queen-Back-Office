package fr.insee.queen.api.surveyunit.service.dummy;

import fr.insee.queen.api.surveyunit.service.StateDataService;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import lombok.Getter;

public class StateDataFakeService implements StateDataService {

    @Getter
    private StateData stateDataSaved = null;

    @Override
    public StateData getStateData(String surveyUnitId) {
        return null;
    }

    @Override
    public void saveStateData(String surveyUnitId, StateData stateData) {
        stateDataSaved = stateData;
    }
}
