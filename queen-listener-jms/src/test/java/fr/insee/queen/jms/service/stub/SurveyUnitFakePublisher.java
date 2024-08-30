package fr.insee.queen.jms.service.stub;

import fr.insee.queen.jms.model.JmsResponse;
import fr.insee.queen.jms.service.SurveyUnitResponsePublisher;
import lombok.Getter;

public class SurveyUnitFakePublisher implements SurveyUnitResponsePublisher {

    @Getter
    private String replyQueueUsed = null;

    @Getter
    private String correlationIdUsed = null;

    @Getter
    private JmsResponse responseSent = null;

    @Override
    public void send(String replyQueue, String correlationId, JmsResponse responseMessage) {
        replyQueueUsed = replyQueue;
        correlationIdUsed = correlationId;
        responseSent = responseMessage;
    }
}
