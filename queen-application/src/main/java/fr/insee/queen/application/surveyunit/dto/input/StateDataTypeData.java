package fr.insee.queen.application.surveyunit.dto.input;

import fr.insee.queen.domain.surveyunit.model.StateDataType;

public enum StateDataTypeData {
    INIT(StateDataType.INIT),
    COMPLETED(StateDataType.COMPLETED),
    VALIDATED(StateDataType.VALIDATED),
    TOEXTRACT(StateDataType.TOEXTRACT),
    EXTRACTED(StateDataType.EXTRACTED);

    private final StateDataType stateDataType;

    StateDataTypeData(StateDataType stateDataType) {
        this.stateDataType = stateDataType;
    }

    public StateDataType getStateDataType() {
        return stateDataType;
    }
}
