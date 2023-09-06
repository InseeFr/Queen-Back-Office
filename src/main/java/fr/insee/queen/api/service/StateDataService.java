package fr.insee.queen.api.service;

import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.dto.input.StateDataInputDto;
import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.StateDataRepository;
import fr.insee.queen.api.repository.SurveyUnitCreateUpdateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class StateDataService {

	private final StateDataRepository stateDataRepository;
	private final SurveyUnitCreateUpdateRepository surveyUnitCreateUpdateRepository;

	public void save(StateData stateData) {
		stateDataRepository.save(stateData);
	}

	public void updateStateData(String surveyUnitId, StateDataInputDto stateData) {
		surveyUnitCreateUpdateRepository.updateSurveyUnitStateData(surveyUnitId, stateData);
	}

	public StateDataDto getStateData(String surveyUnitId) {
		return stateDataRepository.findBySurveyUnitId(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("State data not found for survey unit %s", surveyUnitId)));
	}
}
