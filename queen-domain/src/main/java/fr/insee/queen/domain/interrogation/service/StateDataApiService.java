package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.interrogation.gateway.StateDataRepository;
import fr.insee.queen.domain.interrogation.model.StateData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class StateDataApiService implements StateDataService {

    private final StateDataRepository stateDataRepository;
    private final Clock clock;

    public static final String NOT_FOUND_MESSAGE = "State data not found for interrogation %s";
    public static final String INVALID_DATE_MESSAGE = "Date for state data is invalid";

    @Override
    public StateData getStateData(String interrogationId) {
        return findStateData(interrogationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, interrogationId)));
    }

    @Override
    public Optional<StateData> findStateData(String interrogationId) {
        return stateDataRepository.find(interrogationId);
    }

    @Override
    @Transactional
    public void saveStateData(String interrogationId, StateData stateData, boolean verifyDate) throws StateDataInvalidDateException {
        Optional<StateData> previousStateData = stateDataRepository.find(interrogationId);

        if(stateData.date() == null) {
            long timestamp = ZonedDateTime.now(clock).toInstant().toEpochMilli();
            stateData = new StateData(stateData.state(), timestamp, stateData.currentPage());
        }

        if (previousStateData.isEmpty()) {
            stateDataRepository.save(interrogationId, stateData);
            return;
        }

        // update only if incoming state-data is newer and verifyDate is true
        if (verifyDate) {
            Long previousDate = previousStateData.get().date();
            Long newDate = stateData.date();

            if (previousDate != null && newDate.compareTo(previousDate) < 0) {
                throw new StateDataInvalidDateException(INVALID_DATE_MESSAGE);
            }
        }
        stateDataRepository.save(interrogationId, stateData);
    }
}
