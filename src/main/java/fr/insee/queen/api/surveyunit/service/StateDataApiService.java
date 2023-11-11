package fr.insee.queen.api.surveyunit.service;

import fr.insee.queen.api.surveyunit.service.gateway.StateDataRepository;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class StateDataApiService implements StateDataService {

    private final StateDataRepository stateDataRepository;

    public StateData getStateData(String surveyUnitId) {
        return stateDataRepository.find(surveyUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("State data not found for survey unit %s", surveyUnitId)));
    }

    public void updateStateData(String surveyUnitId, StateData stateData) {
        stateDataRepository.update(surveyUnitId, stateData);
    }
}
