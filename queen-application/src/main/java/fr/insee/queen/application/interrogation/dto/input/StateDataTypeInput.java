package fr.insee.queen.application.interrogation.dto.input;

import fr.insee.queen.domain.interrogation.model.StateDataType;

public enum StateDataTypeInput {
    NOT_INIT(StateDataType.NOT_INIT),
    INIT(StateDataType.INIT),
    COMPLETED(StateDataType.COMPLETED),
    VALIDATED(StateDataType.VALIDATED),
    TOEXTRACT(StateDataType.TOEXTRACT),
    EXTRACTED(StateDataType.EXTRACTED),
    IS_MOVED(StateDataType.IS_MOVED);

    private final StateDataType stateDataType;

    StateDataTypeInput(StateDataType stateDataType) {
        this.stateDataType = stateDataType;
    }

    public StateDataType getStateDataType() {
        return stateDataType;
    }
}
