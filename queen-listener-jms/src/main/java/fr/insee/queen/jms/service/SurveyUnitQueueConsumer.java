package fr.insee.queen.jms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitCommand;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitCommandService;
import fr.insee.queen.domain.surveyunit.service.exception.SurveyUnitCommandException;
import fr.insee.queen.jms.exception.PropertyException;
import fr.insee.queen.jms.model.JmsResponse;
import fr.insee.queen.jms.model.ResponseCode;
import fr.insee.queen.jms.service.utils.JsonPropertyRetriever;
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyUnitQueueConsumer {
    private final ObjectMapper objectMapper;
    private final SurveyUnitCommandService surveyUnitCommandService;
    private final SurveyUnitResponsePublisher replyQueuePublisher;

    // @JmsListener is the only required annotation to convert a method to a JMS listening endpoint
    @JmsListener(destination = "${spring.jms.listener.command-queue}", containerFactory = "jmsListenerFactory")
    public void createSurveyUnit(Message message, Session session) {
        JsonNode command;
        String replyQueue;
        String correlationId;
        try {
            command = objectMapper.readTree(message.getBody(String.class));
            replyQueue = JsonPropertyRetriever.getPropertyValue(command, "replyTo");
            correlationId = JsonPropertyRetriever.getPropertyValue(command, "correlationID");
        } catch (PropertyException | JsonProcessingException | JMSException ex) {
            // not enough information to send back to the reply queue, so only log
            log.error(ex.getMessage(), ex);
            return;
        }

        JmsResponse responseMessage;
        try {
            JsonNode payload = command.path("payload");
            String questionnaireId = JsonPropertyRetriever.getPropertyValue(payload, "questionnaireID");
            String surveyUnitId = JsonPropertyRetriever.getPropertyValue(payload, "repositoryId");
            SurveyUnitCommand surveyUnitCommand = new SurveyUnitCommand(surveyUnitId, questionnaireId,
                    JsonNodeFactory.instance.arrayNode(), JsonNodeFactory.instance.objectNode(), correlationId);
            surveyUnitCommandService.createSurveyUnit(surveyUnitCommand);
            responseMessage = JmsResponse.createResponse(ResponseCode.CREATED);
        } catch (PropertyException | SurveyUnitCommandException ex) {
            responseMessage = JmsResponse.createResponse(ResponseCode.BUSINESS_ERROR, ex.getMessage());
        } catch (RuntimeException ex) {
            responseMessage = JmsResponse.createResponse(ResponseCode.TECHNICAL_ERROR, ex.getMessage());
        }
        replyQueuePublisher.send(replyQueue, correlationId, responseMessage);
    }
}