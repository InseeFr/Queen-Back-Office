package fr.insee.queen.domain.interrogation.infrastructure.dummy;

import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.gateway.StateDataRepository;
import fr.insee.queen.domain.interrogation.model.StateData;
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
    public Optional<StateData> find(String interrogationId) {
        if(hasEmptyStateData) {
            return Optional.empty();
        }
        return Optional.of(stateDataReturned);
    }

    @Override
    public void save(String interrogationId, StateData stateData) {
        stateDataSaved = stateData;
    }

    @Override
    public void create(String interrogationId, StateData stateData) {
        // not implemented yet
    }

    @Override
    public boolean exists(String interrogationId) {
        return false;
    }
}
