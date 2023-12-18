package fr.insee.queen.api.surveyunit.dao.dummy;

import fr.insee.queen.api.depositproof.service.model.StateDataType;
import fr.insee.queen.api.surveyunit.service.gateway.StateDataRepository;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class StateDataFakeDao implements StateDataRepository {

    @Setter
    private boolean hasEmptyStateData = false;

    @Getter
    private StateData stateDataSaved = null;

    public static final StateData STATE_DATA = new StateData(StateDataType.INIT, 90000000L, "2");

    @Override
    public Optional<StateData> find(String surveyUnitId) {
        if(hasEmptyStateData) {
            return Optional.empty();
        }
        return Optional.of(STATE_DATA);
    }

    @Override
    public void save(String surveyUnitId, StateData stateData) {
        stateDataSaved = stateData;
    }

    @Override
    public void create(String surveyUnitId, StateData stateData) {

    }

    @Override
    public boolean exists(String surveyUnitId) {
        return false;
    }
}
