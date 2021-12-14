package fr.insee.queen.api.service;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.SurveyUnit;

public interface StateDataService extends BaseService<StateData, UUID> {

	void save(StateData stateData);

	Optional<StateData> findDtoBySurveyUnitId(String id);
	
	public void updateStateDataFromJson(StateData sd, JsonNode json);

	ResponseEntity<Object> updateStateData(String id, JsonNode dataValue, SurveyUnit surveyUnit);

	ResponseEntity<Object> updateStateData(String id, JsonNode dataValue);


}
