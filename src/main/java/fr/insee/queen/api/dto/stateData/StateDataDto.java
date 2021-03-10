package fr.insee.queen.api.dto.stateData;

import fr.insee.queen.api.domain.StateStateData;

public interface StateDataDto {
	int getIdStateData();
	StateStateData getState();
	Long getDate();
	int getCurrentPage();
}
