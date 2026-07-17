package fr.insee.queen.jms.service.stub;

import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.service.InterrogationResponsePublisher;
import lombok.Getter;

import java.util.UUID;

public class InterrogationFakePublisher implements InterrogationResponsePublisher {

    @Getter
    private String replyQueueUsed = null;

    @Getter
    private UUID correlationIdUsed = null;

    @Getter
    private JMSOutputMessage responseSent = null;

    @Override
    public void send(String replyQueue, UUID correlationId, JMSOutputMessage responseMessage) {
        replyQueueUsed = replyQueue;
        correlationIdUsed = correlationId;
        responseSent = responseMessage;
    }
}
