package fr.insee.queen.jms.service;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.networknt.schema.JsonSchema;
import fr.insee.jms.dto.CommandMessage;
import fr.insee.jms.validation.JsonSchemaValidator;
import fr.insee.jms.validation.SchemaType;
import fr.insee.modelefiliere.InterrogationDto;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.service.InterrogationBatchService;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationBatchException;
import fr.insee.queen.jms.exception.PropertyException;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fr.insee.queen.jms.service.utils.PropertyValidator.isBlank;
import static fr.insee.queen.jms.service.utils.PropertyValidator.textValue;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterrogationQueueConsumer {
    private final ObjectMapper objectMapper;
    private final InterrogationResponsePublisher replyQueuePublisher;
    private final InterrogationBatchService interrogationBatchService;

    @JmsListener(destination = "queue-ue")
    public void createInterrogation(Message message, Session session) throws JMSException {
        String replyQueue=null;
        String correlationId=null;
        JMSOutputMessage responseMessage;
        try {
            String json = message.getBody(String.class);
            // jakarta.jms.JMSException: Invalid JSON: Java 8 date/time type java.time.Instant not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (or disable MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES)
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);   // ISO-8601
            objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            // ---
            JsonNode root = objectMapper.readTree(json);

            replyQueue = textValue(root, "replyTo");
            correlationId = textValue(root, "correlationID");

            CommandMessage pm = JsonSchemaValidator.readAndValidateFromClasspath(
                    root,
                    SchemaType.PROCESS_MESSAGE.getSchemaFileName(),
                    CommandMessage.class,
                    objectMapper
            );
            log.debug(pm.toString());

            String payloadJson = root.path("payload").asText();
            ObjectNode payloadObject = (ObjectNode) objectMapper.readTree(payloadJson);

            InterrogationDto interrogationDto = JsonSchemaValidator.readAndValidateFromClasspath(
                    payloadObject,
                    SchemaType.INTERROGATION.getSchemaFileName(),
                    InterrogationDto.class,
                    objectMapper
            );
            log.debug(interrogationDto.toString());

//            replyQueue = root.path("replyTo").asText();
//            correlationId = root.path("correlationID").asText();

            // TODO
            /* [
                {
                    "name": "whoAnswers1",
                        "value": "LATRECHE RYAN"
                },
                {
                    "name": "whoAnswers2",
                        "value": " vous avez été sélectionné(e) pour participer à l'enquête."
                },
                {
                    "name": "whoAnswers3",
                        "value": ""
                }
            ]*/
            ArrayNode personalization = objectMapper.createArrayNode();
            // personalization.add(interrogationDto.getExtCoverPageData());

            // TODO Deprecated
            ObjectNode comment = JsonNodeFactory.instance.objectNode();

            // TODO identifier le questionnaire de manière unique
            ObjectNode data = objectMapper.convertValue(interrogationDto.getQuestionnaires().getFirst().getQuestionningData(), ObjectNode.class);

            StateData stateData = null;

            Interrogation interrogation = Interrogation.create(interrogationDto.getInterrogationId().toString(),
                    interrogationDto.getSurveyUnitId().toString(),
                    personalization,
                    comment,
                    data,
                    stateData);

            List<Interrogation> interrogations = new ArrayList<>();
            interrogations.add(interrogation);
            // TODO
            interrogationBatchService.saveInterrogations(interrogations);

            responseMessage = JMSOutputMessage.createResponse(ResponseCode.CREATED);

        } catch (InterrogationBatchException ibe) {
            log.error("InterrogationBatchException : {}", ibe.getMessage());
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.BUSINESS_ERROR, ibe.getMessage());
        } catch (JsonSchemaValidator.SchemaValidationException jsv) {
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