package fr.insee.queen.jms.service.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitCommand;
import fr.insee.queen.jms.exception.PropertyException;
import fr.insee.queen.jms.model.JmsResponse;
import fr.insee.queen.jms.model.ResponseCode;
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static fr.insee.queen.jms.configuration.ConfigurationJMS.SU_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyUnitCreationListener {
    private final ObjectMapper objectMapper;
    //private final SurveyUnitCommandServiceImpl surveyUnitCommandService;

    private static final String PROPERTY_NOT_FOUND_MESSAGE =
            "Property %s does not exist";
    private static final String PROPERTY_NOT_TEXTUAL_MESSAGE =
            "Property %s does not have a textual value";

    // @JmsListener is the only required annotation to convert a method to a JMS listening endpoint
    @JmsListener(destination = SU_QUEUE, containerFactory = "jmsListenerFactory")
    public void createSurveyUnit(Message message, Session session) {
        JsonNode command;
        String replyQueue;
        String correlationId;
        try {
            command = objectMapper.readTree(message.getBody(String.class));
            replyQueue = getPropertyValue(command, "replyTo");
            correlationId = getPropertyValue(command, "correlationID");
        } catch (PropertyException | JsonProcessingException | JMSException ex) {
            log.error(ex.getMessage(), ex);
            return;
        }

        String questionnaireId;
        String surveyUnitId;
        try {
            JsonNode payload = command.get("payload");
            questionnaireId = getPropertyValue(payload, "questionnaireID");
            surveyUnitId = getPropertyValue(payload, "repositoryId");
        } catch (PropertyException ex) {
            JmsResponse responseMessage = JmsResponse.createResponse(ResponseCode.BUSINESS_ERROR, ex.getMessage());
            sendToReplyQueue(replyQueue, correlationId, responseMessage);
            return;
        }

    try {
        SurveyUnitCommand surveyUnitCommand = new SurveyUnitCommand(surveyUnitId, questionnaireId, null, null, correlationId);
        //surveyUnitCommandService.createSurveyUnit(surveyUnitCommand);
        JmsResponse response = JmsResponse.createResponse(ResponseCode.CREATED);
        sendToReplyQueue(replyQueue, correlationId, response);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendToReplyQueue(String replyQueue, String correlationId, JmsResponse responseMessage) {
        log.info("Command {} - reply to queue {} - response code: {} - response message: {} - ",
                correlationId, replyQueue, responseMessage.code(), responseMessage.message());
        JmsTemplateQueue.send(replyQueue, session -> {
                String jsonResponse = objectMapper.writeValueAsString(responseMessage);
                ObjectMessage objectMessage = session.createObjectMessage(jsonResponse);
                objectMessage.setJMSCorrelationID(correlationId);
                objectMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
                return objectMessage;
        });
        log.debug("Command {} - sent", correlationId);
    }
    
    private String getPropertyValue(JsonNode sourceNode, String propertyToFind) throws PropertyException {
        JsonNode propertyValue = sourceNode.get(propertyToFind);
        if(propertyValue.isNull() || propertyValue.isEmpty()) {
            throw new PropertyException(
                    String.format(PROPERTY_NOT_FOUND_MESSAGE, propertyToFind)
            );
        }

        if(!propertyValue.isTextual()) {
            throw new PropertyException(
                    String.format(PROPERTY_NOT_TEXTUAL_MESSAGE, propertyToFind)
            );
        }
    }
}