package fr.insee.queen.jms.service.consummers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Event consumer that handles QUESTIONNAIRE_VALIDATED events.
 * This consumer updates the state data to VALIDATED when a QUESTIONNAIRE_VALIDATED event is received.
 */
@Component
public class QuestionnaireValidatedEventConsumer extends AbstractStateDataEventConsumer {

    public QuestionnaireValidatedEventConsumer(StateDataService stateDataService, Clock clock) {
        super(stateDataService, clock);
    }

    @Override
    protected EventDto.EventTypeEnum getEventType() {
        return EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED;
    }

    @Override
    protected StateDataType getStateDataType() {
        return StateDataType.VALIDATED;
    }
}