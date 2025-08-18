package fr.insee.queen.application.interrogation.dto.input;

import fr.insee.queen.domain.interrogation.model.StateDataType;

public enum StateDataTypeInput {
    INIT(StateDataType.INIT),
    COMPLETED(StateDataType.COMPLETED),
    VALIDATED(StateDataType.VALIDATED),
    TOEXTRACT(StateDataType.TOEXTRACT),
    EXTRACTED(StateDataType.EXTRACTED);

    private final StateDataType stateDataType;

    StateDataTypeInput(StateDataType stateDataType) {
        this.stateDataType = stateDataType;
    }

    public StateDataType getStateDataType() {
        return stateDataType;
    }
}
