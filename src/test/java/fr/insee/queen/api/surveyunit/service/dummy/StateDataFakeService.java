package fr.insee.queen.api.surveyunit.service.dummy;

import fr.insee.queen.api.surveyunit.service.StateDataService;
import fr.insee.queen.api.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import lombok.Getter;
import lombok.Setter;

public class StateDataFakeService implements StateDataService {

    public static final String ERROR_MESSAGE = "error";
    @Getter
    private StateData stateDataSaved = null;

    @Setter
    private boolean isDateInvalid = false;

    @Override
    public StateData getStateData(String surveyUnitId) {
        return null;
    }

    @Override
    public void saveStateData(String surveyUnitId, StateData stateData) throws StateDataInvalidDateException {
        if(isDateInvalid) {
            throw new StateDataInvalidDateException(ERROR_MESSAGE);
        }
        stateDataSaved = stateData;
    }
}
