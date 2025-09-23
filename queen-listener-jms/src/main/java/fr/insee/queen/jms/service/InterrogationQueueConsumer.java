package fr.insee.queen.jms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.modelefiliere.InterrogationDto;
import fr.insee.queen.domain.interrogation.model.InterrogationCommand;
import fr.insee.queen.domain.interrogation.service.InterrogationCommandService;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationCommandException;
import fr.insee.queen.jms.exception.PropertyException;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import fr.insee.queen.jms.service.utils.JsonValidator;
import fr.insee.queen.jms.service.utils.PropertyValidator;
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterrogationQueueConsumer {
    private final ObjectMapper objectMapper;
    private final InterrogationCommandService interrogationCommandService;
    private final InterrogationResponsePublisher replyQueuePublisher;

    // @JmsListener is the only required annotation to convert a method to a JMS listening endpoint
    @JmsListener(destination = "queue-ue")
    public void createInterrogation(Message message, Session session) throws JMSException, JsonProcessingException {
        String replyQueue;
        String correlationId;
        JMSOutputMessage responseMessage;

        String json = JsonValidator.extractJson(message);
        if (json == null) {
            log.error("Aucun contenu JSON détecté (message=" + message + ")");
            return;
        }
        JsonNode root = objectMapper.readTree(json);
        String payloadJson = root.path("payload").asText(); // payload est une chaîne JSON
        ObjectNode payloadObject = (ObjectNode) objectMapper.readTree(payloadJson);

        JsonNode commentNode = payloadObject.path("comment");
        if (commentNode.isObject()) {
            String text = commentNode.path("text").asText("");
            payloadObject.put("comment", text);
        }
        if (commentNode.isObject()) {
            String text = commentNode.path("text").asText("");
            payloadObject.put("personalization", text);
        }

        InterrogationDto interrogation = objectMapper.treeToValue(payloadObject, InterrogationDto.class);

        replyQueue = root.path("replyTo").asText();
        correlationId = root.path("correlationID").asText();

        try {
            PropertyValidator.checkPropertyValue("replyTo", replyQueue);
            PropertyValidator.checkPropertyValue("correlationID", correlationId);
            PropertyValidator.checkPropertyValue("id", interrogation.getInterrogationId());
            PropertyValidator.checkPropertyValue("surveyUnitId", interrogation.getSurveyUnitId());
            PropertyValidator.checkPropertyValue("questionnaireId", interrogation.getQuestionnaires().getFirst().getQuestionnaireModelId());
            InterrogationCommand interrogationCommand = new InterrogationCommand(
                    interrogation.getInterrogationId().toString(),
                    interrogation.getSurveyUnitId().toString(),
                    interrogation.getQuestionnaires().getFirst().getQuestionnaireModelId(),
                    JsonNodeFactory.instance.arrayNode(),
                    JsonNodeFactory.instance.objectNode(),
                    correlationId);
            interrogationCommandService.createInterrogation(interrogationCommand);
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.CREATED);
        } catch (InterrogationCommandException ex) {
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.BUSINESS_ERROR, ex.getMessage());
        } catch (RuntimeException ex) {
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, ex.getMessage());
        } catch (PropertyException e) {
            throw new RuntimeException(e);
        }
        replyQueuePublisher.send(replyQueue, correlationId, responseMessage);
    }
}