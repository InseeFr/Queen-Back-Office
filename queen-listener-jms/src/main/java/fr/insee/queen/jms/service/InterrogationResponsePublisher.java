package fr.insee.queen.jms.service;

import fr.insee.queen.jms.model.JMSOutputMessage;

public interface InterrogationResponsePublisher {
    void send(String replyQueue, String correlationId, JMSOutputMessage responseMessage);
}
