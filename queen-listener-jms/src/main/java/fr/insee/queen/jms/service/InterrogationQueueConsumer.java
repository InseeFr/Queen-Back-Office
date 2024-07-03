package fr.insee.queen.jms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.modelefiliere.InterrogationDto;
import fr.insee.queen.domain.interrogation.model.InterrogationCommand;
import fr.insee.queen.domain.interrogation.service.InterrogationCommandService;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationCommandException;
import fr.insee.queen.jms.exception.PropertyException;
import fr.insee.queen.jms.model.JMSInputMessage;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import fr.insee.queen.jms.service.utils.PropertyValidator;
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterrogationQueueConsumer {
    private final ObjectMapper objectMapper;
    private final InterrogationCommandService interrogationCommandService;
    private final InterrogationResponsePublisher replyQueuePublisher;

    // @JmsListener is the only required annotation to convert a method to a JMS listening endpoint
    @JmsListener(destination = "${spring.jms.listener.command-queue}", containerFactory = "jmsListenerFactory")
    public void createInterrogation(Message message, Session session) {
        JMSInputMessage inputMessage;
        String replyQueue;
        String correlationId;
        try {
            inputMessage = objectMapper.readValue(message.getBody(String.class), JMSInputMessage.class);
            replyQueue = inputMessage.replyTo();
            correlationId = inputMessage.correlationID();

            PropertyValidator.checkPropertyValue("correlationID", correlationId);
            PropertyValidator.checkPropertyValue("replyTo", replyQueue);
        } catch (PropertyException | JsonProcessingException | JMSException ex) {
            // not enough information to send back to the reply queue, so only log
            log.error(ex.getMessage(), ex);
            return;
        }

        JMSOutputMessage responseMessage;
        try {
            InterrogationDto interrogationFiliere = inputMessage.payload();
            String questionnaireId = interrogationFiliere.getQuestionnaires().getFirst().getQuestionnaireModelId();
            PropertyValidator.checkPropertyValue("questionnaireModelId", questionnaireId);
            UUID surveyUnitId = interrogationFiliere.getSurveyUnitId();
            PropertyValidator.checkPropertyValue("surveyUnitId", surveyUnitId);
            InterrogationCommand interrogationCommand = new InterrogationCommand(
                    interrogationFiliere.getInterrogationId().toString(),
                    surveyUnitId.toString(),
                    questionnaireId,
                    JsonNodeFactory.instance.arrayNode(),
                    JsonNodeFactory.instance.objectNode(),
                    correlationId);
            interrogationCommandService.createInterrogation(interrogationCommand);
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.CREATED);
        } catch (PropertyException | InterrogationCommandException ex) {
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.BUSINESS_ERROR, ex.getMessage());
        } catch (RuntimeException ex) {
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, ex.getMessage());
        }
        replyQueuePublisher.send(replyQueue, correlationId, responseMessage);
    }
}