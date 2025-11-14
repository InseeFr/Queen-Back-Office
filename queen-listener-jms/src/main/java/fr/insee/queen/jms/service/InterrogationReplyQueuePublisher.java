package fr.insee.queen.jms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.jms.model.JMSOutputMessage;
import jakarta.jms.DeliveryMode;
import jakarta.jms.ObjectMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InterrogationReplyQueuePublisher implements InterrogationResponsePublisher {
    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsQueuePublisher;

    public void send(String replyQueue, String correlationId, JMSOutputMessage responseMessage) {
        log.info("Command {} - reply to queue {} - response code: {} - response message: {} - ",
                correlationId, replyQueue, responseMessage.code(), responseMessage.message());

        String jsonResponse;
        try {
            jsonResponse = objectMapper.writeValueAsString(responseMessage);
        } catch (JsonProcessingException e) {
            log.error("Command {} - Unable to process json response", correlationId);
            return;
        }

        jmsQueuePublisher.send(replyQueue, session -> {
            ObjectMessage objectMessage = session.createObjectMessage(jsonResponse);
            objectMessage.setJMSCorrelationID(correlationId);
            objectMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            return objectMessage;
        });
        log.info("Command {} - sent", correlationId);
    }
}
