package fr.insee.queen.api.service;

import fr.insee.queen.api.domain.StateData;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.input.StateDataInputDto;
import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.repository.StateDataRepository;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class StateDataService {
	private final SurveyUnitRepository surveyUnitRepository;

	private final StateDataRepository stateDataRepository;

	public StateDataDto getStateData(String surveyUnitId) {
		return stateDataRepository.findBySurveyUnitId(surveyUnitId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("State data not found for survey unit %s", surveyUnitId)));
	}
	
	public void updateStateData(String surveyUnitId, StateDataInputDto stateDataInput) {
		Optional<StateData> stateDataOptional = stateDataRepository.findEntityBySurveyUnitId(surveyUnitId);
		SurveyUnit surveyUnit = surveyUnitRepository.getReferenceById(surveyUnitId);
		if(stateDataOptional.isPresent()) {
			StateData stateData = stateDataOptional.get();
			stateData.state(stateDataInput.state());
			stateData.date(stateDataInput.date());
			stateData.currentPage(stateDataInput.currentPage());
			stateData.surveyUnit(surveyUnit);
			stateDataRepository.save(stateData);
			return;
		}
		StateData stateData = new StateData(UUID.randomUUID(), stateDataInput.state(), stateDataInput.date(), stateDataInput.currentPage(), surveyUnit);
		stateDataRepository.save(stateData);
	}
}
