package fr.insee.queen.jms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitCommand;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitCommandServiceImpl;
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

import static fr.insee.queen.jms.configuration.ConfigurationJMS.SU_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyUnitQueueConsumer {
    private final ObjectMapper objectMapper;
    private final SurveyUnitCommandServiceImpl surveyUnitCommandService;
    private final SurveyUnitReplyQueuePublisher replyQueuePublisher;

    // @JmsListener is the only required annotation to convert a method to a JMS listening endpoint
    @JmsListener(destination = SU_QUEUE, containerFactory = "jmsListenerFactory")
    public void createSurveyUnit(Message message, Session session) {
        JsonNode command;
        String replyQueue;
        String correlationId;
        try {
            command = objectMapper.readTree(message.getBody(String.class));
            replyQueue = JsonPropertyRetriever.getPropertyValue(command, "replyTo");
            correlationId = JsonPropertyRetriever.getPropertyValue(command, "correlationID");
        } catch (PropertyException | JsonProcessingException | JMSException ex) {
            log.error(ex.getMessage(), ex);
            return;
        }

        String questionnaireId;
        String surveyUnitId;
        try {
            JsonNode payload = command.get("payload");
            questionnaireId = JsonPropertyRetriever.getPropertyValue(payload, "questionnaireID");
            surveyUnitId = JsonPropertyRetriever.getPropertyValue(payload, "repositoryId");
        } catch (PropertyException ex) {
            JmsResponse responseMessage = JmsResponse.createResponse(ResponseCode.BUSINESS_ERROR, ex.getMessage());
            replyQueuePublisher.send(replyQueue, correlationId, responseMessage);
            return;
        }

        JmsResponse responseMessage;
        try {
            SurveyUnitCommand surveyUnitCommand = new SurveyUnitCommand(surveyUnitId, questionnaireId,
                    JsonNodeFactory.instance.arrayNode(), JsonNodeFactory.instance.objectNode(), correlationId);
            surveyUnitCommandService.createSurveyUnit(surveyUnitCommand);
            responseMessage = JmsResponse.createResponse(ResponseCode.CREATED);
        } catch (SurveyUnitCommandException e) {
            log.error(e.getMessage(), e);
            responseMessage = JmsResponse.createResponse(ResponseCode.BUSINESS_ERROR, e.getMessage());

        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            responseMessage = JmsResponse.createResponse(ResponseCode.TECHNICAL_ERROR, e.getMessage());
        }
        replyQueuePublisher.send(replyQueue, correlationId, responseMessage);
    }
}