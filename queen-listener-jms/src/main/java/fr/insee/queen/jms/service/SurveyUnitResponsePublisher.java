package fr.insee.queen.jms.service;

import fr.insee.queen.jms.model.JmsResponse;

public interface SurveyUnitResponsePublisher {
    void send(String replyQueue, String correlationId, JmsResponse responseMessage);
}
