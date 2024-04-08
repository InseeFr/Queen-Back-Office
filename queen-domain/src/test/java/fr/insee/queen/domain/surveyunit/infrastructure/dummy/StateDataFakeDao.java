package fr.insee.queen.domain.surveyunit.infrastructure.dummy;

import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.gateway.StateDataRepository;
import fr.insee.queen.domain.surveyunit.model.StateData;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class StateDataFakeDao implements StateDataRepository {

    @Setter
    private boolean hasEmptyStateData = false;

    @Getter
    private StateData stateDataSaved = null;

    @Setter
    @Getter
    public StateData stateDataReturned = new StateData(StateDataType.INIT, 90000000L, "2");

    @Override
    public Optional<StateData> find(String surveyUnitId) {
        if(hasEmptyStateData) {
            return Optional.empty();
        }
        return Optional.of(stateDataReturned);
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
