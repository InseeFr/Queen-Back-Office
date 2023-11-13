package fr.insee.queen.api.surveyunit.controller.dto.input;

import fr.insee.queen.api.depositproof.service.model.StateDataType;

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
