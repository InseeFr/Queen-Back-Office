package fr.insee.queen.jms.service.consummers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Event consumer that handles MULTIMODE_MOVED events.
 * This consumer updates the state data to IS_MOVED when a MULTIMODE_MOVED event is received.
 */
@Slf4j
@Component
public class QuestionnaireMovedEventConsumer extends AbstractStateDataEventConsumer {

    private final InterrogationService interrogationService;

    public QuestionnaireMovedEventConsumer(StateDataService stateDataService, Clock clock, InterrogationService interrogationService) {
        super(stateDataService, clock);
        this.interrogationService = interrogationService;
    }

    @Override
    protected EventDto.EventTypeEnum getEventType() {
        return EventDto.EventTypeEnum.MULTIMODE_MOVED;
    }

    @Override
    protected StateDataType getStateDataType() {
        return StateDataType.IS_MOVED;
    }

    @Override
    public boolean canConsume(String interrogationId) {
        try {
            Interrogation interrogation = interrogationService.getInterrogation(interrogationId);
            Boolean locked = interrogation.locked();
            log.info("Checking if interrogation {} is locked: {}", interrogationId, locked);
            // Can only consume if locked is false or null
            return locked == null || !locked;
        } catch (Exception e) {
            log.warn("Could not check locked status for interrogation {}: {}", interrogationId, e.getMessage());
            return true;
        }
    }
}