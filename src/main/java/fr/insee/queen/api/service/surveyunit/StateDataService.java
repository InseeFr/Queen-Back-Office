package fr.insee.queen.api.service.surveyunit;

import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.repository.StateDataRepository;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class StateDataService {

	private final StateDataRepository stateDataRepository;

	public StateDataDto getStateData(String surveyUnitId) {
		return stateDataRepository.findBySurveyUnitId(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("State data not found for survey unit %s", surveyUnitId)));
	}

	public void updateStateData(String surveyUnitId, StateDataDto stateData) {
		if(stateDataRepository.existsBySurveyUnitId(surveyUnitId)) {
			stateDataRepository.updateStateData(surveyUnitId, stateData);
			return;
		}
		stateDataRepository.createStateData(surveyUnitId, stateData);
	}
}
