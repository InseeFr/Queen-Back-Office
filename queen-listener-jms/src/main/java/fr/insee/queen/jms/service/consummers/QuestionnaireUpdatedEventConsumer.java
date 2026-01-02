package fr.insee.queen.jms.service.consummers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import org.springframework.stereotype.Component;

import java.time.Clock;


@Component
public class QuestionnaireUpdatedEventConsumer extends AbstractStateDataRefreshEventConsumer {
    public QuestionnaireUpdatedEventConsumer(StateDataService stateDataService, Clock clock) {
        super(stateDataService, clock);
    }

    @Override
    protected EventDto.EventTypeEnum getEventType() {
        return EventDto.EventTypeEnum.QUESTIONNAIRE_UPDATED;
    }
}
