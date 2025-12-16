package fr.insee.queen.jms.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.insee.jms.validation.JsonSchemaValidator;
import fr.insee.jms.validation.SchemaType;
import fr.insee.modelefiliere.CommandRequestDto;
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

import java.io.IOException;
import java.util.UUID;

import static fr.insee.queen.jms.service.utils.PropertyValidator.textValue;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterrogationQueueConsumer {
    private final ObjectMapper objectMapper;
    private final InterrogationResponsePublisher replyQueuePublisher;
    private final InterrogationBatchService interrogationBatchService;

    @JmsListener(destination = "${broker.queue.interrogation.request}")
    public void createInterrogation(Message message, Session session) throws JMSException {
        String replyQueue=null;
        String correlationId=null;
        JMSOutputMessage responseMessage;
        try {
            String jsonString = message.getBody(String.class);
            // jakarta.jms.JMSException: Invalid JSON: Java 8 date/time type java.time.Instant not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (or disable MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES)
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);   // ISO-8601
            objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            // ---
            JsonNode root = objectMapper.readTree(jsonString);

            replyQueue = textValue(root, "replyTo");
            correlationId = textValue(root, "correlationId");

            CommandRequestDto command = JsonSchemaValidator.readAndValidateFromClasspath(
                    root,
                    SchemaType.PROCESS_MESSAGE.getSchemaFileName(),
                    CommandRequestDto.class,
                    objectMapper
            );
            log.debug(command.toString());

            // TODO personalization
            ArrayNode personalization = objectMapper.createArrayNode();

            // TODO identifier le questionnaire de manière unique
            ObjectNode data = objectMapper.convertValue(command.getPayload().get("TODO"), ObjectNode.class);

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
        } catch (IOException ioe) {
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