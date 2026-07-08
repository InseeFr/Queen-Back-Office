package fr.insee.queen.domain.interrogation.service;

import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.interrogation.gateway.StateDataRepository;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidTransitionException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class StateDataApiService implements StateDataService {

    private final StateDataRepository stateDataRepository;
    private final Clock clock;

    public static final String NOT_FOUND_MESSAGE = "State data not found for interrogation %s";
    public static final String INVALID_DATE_MESSAGE = "Date for state data is invalid";
    public static final String INVALID_TRANSITION_MESSAGE = "New state is forbidden according to previous state";

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
    public void saveStateData(String interrogationId, StateData stateData, boolean verifyDate, boolean verifyTransition) throws StateDataInvalidDateException {
        Optional<StateData> previousStateData = stateDataRepository.find(interrogationId);

        if(stateData.date() == null) {
            long timestamp = clock.instant().toEpochMilli();
            stateData = new StateData(stateData.state(), timestamp, stateData.currentPage());
        }

        if (previousStateData.isEmpty()) {
            log.info("Previous state data not found, new state data -> [{}, {}, {}]", stateData.state().name(), stateData.date(), stateData.currentPage());
            stateDataRepository.save(interrogationId, stateData);
            return;
        }

        StateData existingPreviousStateData = previousStateData.get();

        if(verifyTransition && blocksTransitionFrom(existingPreviousStateData.state())){
            log.error("Invalid transition state: Previous state data : [{},{}, {}], new state data : [{}, {}, {}]",
                    existingPreviousStateData.state() != null ? existingPreviousStateData.state().name() : null,
                    existingPreviousStateData.date(),
                    existingPreviousStateData.currentPage(),
                    stateData.state().name(),
                    stateData.date(),
                    stateData.currentPage());
            throw new StateDataInvalidTransitionException(INVALID_TRANSITION_MESSAGE + ", previous state is " + existingPreviousStateData.state());
        }

        log.info("Previous state data : [{},{}, {}], new state data : [{}, {}, {}]",
                existingPreviousStateData.state() != null ? existingPreviousStateData.state().name() : null,
                existingPreviousStateData.date(),
                existingPreviousStateData.currentPage(),
                stateData.state().name(),
                stateData.date(),
                stateData.currentPage());

        // update only if incoming state-data is newer and verifyDate is true
        if (verifyDate) {
            Long previousDate = existingPreviousStateData.date();
            Long newDate = stateData.date();

            if (previousDate != null && newDate.compareTo(previousDate) < 0) {
                throw new StateDataInvalidDateException(INVALID_DATE_MESSAGE);
            }
        }
        stateDataRepository.save(interrogationId, stateData);
    }

    private boolean blocksTransitionFrom(StateDataType previousState) {
        return StateDataType.VALIDATED.equals(previousState) || StateDataType.EXTRACTED.equals(previousState);
    }
}
