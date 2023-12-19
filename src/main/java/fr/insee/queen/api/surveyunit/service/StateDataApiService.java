package fr.insee.queen.api.surveyunit.service;

import fr.insee.queen.api.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.api.surveyunit.service.gateway.StateDataRepository;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class StateDataApiService implements StateDataService {

    private final StateDataRepository stateDataRepository;

    public static final String NOT_FOUND_MESSAGE = "State data not found for survey unit %s";
    public static final String INVALID_DATE_MESSAGE = "Date for state data is invalid";

    @Override
    public StateData getStateData(String surveyUnitId) {
        return stateDataRepository.find(surveyUnitId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, surveyUnitId)));
    }

    @Override
    public void saveStateData(String surveyUnitId, StateData stateData) throws StateDataInvalidDateException {
        Optional<StateData> previousStateData = stateDataRepository.find(surveyUnitId);
        if (previousStateData.isEmpty()) {
            stateDataRepository.save(surveyUnitId, stateData);
            return;
        }

        // update only if incoming state-data is newer
        Long previousDate = previousStateData.get().date();
        Long newDate = stateData.date();
        if (newDate.compareTo(previousDate) < 0) {
            throw new StateDataInvalidDateException(INVALID_DATE_MESSAGE);
        }
        stateDataRepository.save(surveyUnitId, stateData);
    }
}
