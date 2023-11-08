package fr.insee.queen.api.service.surveyunit;

import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.service.gateway.StateDataRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class StateDataApiService implements StateDataService {

	private final StateDataRepository stateDataRepository;

	public StateDataDto getStateData(String surveyUnitId) {
		return stateDataRepository.find(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("State data not found for survey unit %s", surveyUnitId)));
	}

	public void updateStateData(String surveyUnitId, StateDataDto stateData) {
		if(stateDataRepository.exists(surveyUnitId)) {
			stateDataRepository.update(surveyUnitId, stateData);
			return;
		}
		stateDataRepository.create(surveyUnitId, stateData);
	}
}
