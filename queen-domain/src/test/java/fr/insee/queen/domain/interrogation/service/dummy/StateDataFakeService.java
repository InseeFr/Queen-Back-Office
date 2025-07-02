package fr.insee.queen.domain.interrogation.service.dummy;

import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class StateDataFakeService implements StateDataService {

    public static final String ERROR_MESSAGE = "error";
    @Getter
    private StateData stateDataSaved = null;

    @Setter
    private boolean isDateInvalid = false;

    @Override
    public StateData getStateData(String interrogationId) {
        return null;
    }

    @Override
    public void saveStateData(String interrogationId, StateData stateData, boolean verifyDate) throws StateDataInvalidDateException {
        if(isDateInvalid) {
            throw new StateDataInvalidDateException(ERROR_MESSAGE);
        }
        stateDataSaved = stateData;
    }

    @Override
    public Optional<StateData> findStateData(String interrogationId) {
        return Optional.empty();
    }
}
