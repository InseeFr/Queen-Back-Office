package fr.insee.queen.jms.service;

import fr.insee.queen.jms.model.JMSOutputMessage;
import jakarta.jms.DeliveryMode;
import jakarta.jms.ObjectMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class InterrogationReplyQueuePublisher implements InterrogationResponsePublisher {
    private final JsonMapper jsonMapper;
    private final JmsTemplate jmsQueuePublisher;

    public void send(String replyQueue, UUID correlationId, JMSOutputMessage responseMessage) {
        log.info("Command {} - reply to queue '{}' - response code: {} - response message: {} - ",
                correlationId, replyQueue, responseMessage.code(), responseMessage.message());

        String jsonResponse;
        try {
            jsonResponse = jsonMapper.writeValueAsString(responseMessage);
        } catch (JacksonException e) {
            log.error("Command '{}' - Unable to process json response", correlationId, e);
            return;
        }

        jmsQueuePublisher.send(replyQueue, session -> {
            ObjectMessage objectMessage = session.createObjectMessage(jsonResponse);
            objectMessage.setJMSCorrelationID(correlationId.toString());
            objectMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            return objectMessage;
        });
        log.info("Command '{}' - sent", correlationId);
    }
}
