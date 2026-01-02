package fr.insee.queen.jms.service.consummers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Event consumer that handles QUESTIONNAIRE_COMPLETED events.
 * This consumer updates the state data to COMPLETED when a QUESTIONNAIRE_COMPLETED event is received.
 */
@Component
public class QuestionnaireCompletedEventConsumer extends AbstractStateDataEventConsumer {

    public QuestionnaireCompletedEventConsumer(StateDataService stateDataService, Clock clock) {
        super(stateDataService, clock);
    }

    @Override
    protected EventDto.EventTypeEnum getEventType() {
        return EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED;
    }

    @Override
    protected StateDataType getStateDataType() {
        return StateDataType.COMPLETED;
    }
}