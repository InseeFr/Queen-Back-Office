package fr.insee.queen.api.dto.input;

import fr.insee.queen.api.dto.statedata.StateDataType;

public enum StateDataTypeInputDto {
	INIT(StateDataType.INIT),
	COMPLETED(StateDataType.COMPLETED),
	VALIDATED(StateDataType.VALIDATED),
	TOEXTRACT(StateDataType.TOEXTRACT),
	EXTRACTED(StateDataType.EXTRACTED);

	private final StateDataType stateDataType;

	StateDataTypeInputDto(StateDataType stateDataType) {
		this.stateDataType = stateDataType;
	}

	public StateDataType getStateDataType() {
		return stateDataType;
	}
}
