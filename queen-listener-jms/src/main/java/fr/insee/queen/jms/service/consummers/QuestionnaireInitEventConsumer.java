package fr.insee.queen.jms.service.consummers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Event consumer that handles QUESTIONNAIRE_INIT events.
 * This consumer refreshes the state data date when a QUESTIONNAIRE_INIT event is received.
 * If no state data exists, it creates a new one with INIT state.
 */
@Component
public class QuestionnaireInitEventConsumer extends AbstractStateDataRefreshEventConsumer {

    public QuestionnaireInitEventConsumer(StateDataService stateDataService, Clock clock) {
        super(stateDataService, clock);
    }

    @Override
    protected EventDto.EventTypeEnum getEventType() {
        return EventDto.EventTypeEnum.QUESTIONNAIRE_INIT;
    }
}