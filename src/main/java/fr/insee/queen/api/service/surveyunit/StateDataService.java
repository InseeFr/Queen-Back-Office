package fr.insee.queen.api.service.surveyunit;

import fr.insee.queen.api.dto.statedata.StateDataDto;

public interface StateDataService {
	StateDataDto getStateData(String surveyUnitId);
	void updateStateData(String surveyUnitId, StateDataDto stateData);
}
