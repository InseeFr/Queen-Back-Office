package fr.insee.queen.jms.service;

import fr.insee.queen.jms.model.JMSOutputMessage;

import java.util.UUID;

public interface InterrogationResponsePublisher {
    void send(String replyQueue, UUID correlationId, JMSOutputMessage responseMessage);
}
