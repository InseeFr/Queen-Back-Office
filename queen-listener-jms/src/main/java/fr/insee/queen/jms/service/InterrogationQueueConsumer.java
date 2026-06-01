package fr.insee.queen.jms.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.jms.validation.JsonSchemaValidator;
import fr.insee.jms.validation.SchemaType;
import fr.insee.modelefiliere.CommandDto;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.service.InterrogationBatchService;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationBatchException;
import fr.insee.queen.jms.exception.PropertyException;
import fr.insee.queen.jms.exception.SchemaValidationException;
import fr.insee.queen.jms.model.InterrogationAsyncInput;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.util.UUID;

import static fr.insee.queen.jms.service.utils.PropertyValidator.textValue;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterrogationQueueConsumer {
    private final JsonMapper jsonMapper;
    private final InterrogationResponsePublisher replyQueuePublisher;
    private final InterrogationBatchService interrogationBatchService;

    @JmsListener(destination = "${broker.queue.interrogation.request}")
    public void createInterrogation(Message message, Session session) throws JMSException {
        String replyQueue=null;
        String correlationId=null;
        JMSOutputMessage responseMessage;
        try {
            String jsonString = message.getBody(String.class);
            // ---
            JsonNode root = jsonMapper.readTree(jsonString);

            replyQueue = textValue(root, "replyTo");
            correlationId = textValue(root, "correlationID");

            CommandDto command = JsonSchemaValidator.readAndValidateFromClasspath(
                    root,
                    SchemaType.PROCESS_MESSAGE.getSchemaFileName(),
                    CommandDto.class,
                    jsonMapper
            );
            log.debug(command.toString());

            // TODO personalization
            ArrayNode personalization = jsonMapper.createArrayNode();

            // TODO identifier le questionnaire de manière unique
            ObjectNode data = jsonMapper.convertValue(command.getPayload().get("TODO"), ObjectNode.class);

            Interrogation interrogation = InterrogationAsyncInput.toModel(new InterrogationAsyncInput(command.getPayload().get("interrogationId").toString(),
                                                                                            command.getPayload().get("surveyUnitId").toString(),
                                                                                            "questionnaireId",
                                                                                            personalization,
                                                                                            data,
                                                                                            UUID.randomUUID()), "CampaignId");

            // TODO saveInterrogation
            interrogationBatchService.saveInterrogation(interrogation);

            responseMessage = JMSOutputMessage.createResponse(ResponseCode.CREATED);

        } catch (InterrogationBatchException ibe) {
            log.error("InterrogationBatchException : {}", ibe.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.BUSINESS_ERROR, ibe.getMessage());
        } catch (SchemaValidationException jsv) {
            log.error("JsonSchemaValidator : {}", jsv.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, jsv.getMessage());
        } catch (JacksonException | IOException ioe) {
            log.error("IOException : {}", ioe.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, ioe.getMessage());
        }catch (EntityNotFoundException enfe) {
            log.error("EntityNotFoundException : {}", enfe.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.NOT_FOUND, enfe.getMessage());
        } catch (PropertyException pe) {
            log.error("PropertyException : {}", pe.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, pe.getMessage());
        }
        replyQueuePublisher.send(replyQueue, correlationId, responseMessage);
    }
}