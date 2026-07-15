package fr.insee.queen.jms.service;

import fr.insee.queen.jms.exception.ValidationException;
import fr.insee.modelefiliere.*;
import jakarta.jms.JMSException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import fr.insee.jms.validation.JsonSchemaValidator;
import fr.insee.jms.validation.SchemaType;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.service.InterrogationBatchService;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationBatchException;
import fr.insee.queen.jms.exception.PropertyException;
import fr.insee.queen.jms.exception.SchemaValidationException;
import fr.insee.queen.jms.mapper.PersonalizationMapper;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static fr.insee.queen.jms.service.utils.PropertyValidator.textValue;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterrogationQueueConsumer {
    private final JsonMapper jsonMapper;
    private final InterrogationResponsePublisher replyQueuePublisher;
    private final InterrogationBatchService interrogationBatchService;
    private final PersonalizationMapper personalizationMapper;

    @JmsListener(destination = "${broker.queue.interrogation.request}")
    public void createInterrogation(Message message) {
        JsonNode root;
        String replyQueue;
        String correlationId;

        try {
            root = jsonMapper.readTree(message.getBody(String.class));
            replyQueue = textValue(root, "replyTo");
            correlationId = textValue(root, "correlationId");
        } catch (JacksonException | PropertyException | JMSException ex) {
            log.error("Cannot process message !!! Exception : {}", ex.getMessage(), ex);
            return;
        }

        JMSOutputMessage responseMessage;
        try {
            CommandRequestDto command = JsonSchemaValidator.readAndValidateFromClasspath(
                    root,
                    SchemaType.PROCESS_MESSAGE.getSchemaFileName(),
                    CommandRequestDto.class,
                    jsonMapper
            );
            log.debug(command.toString());

            InterrogationDto interrogationCommand = JsonSchemaValidator.readAndValidateFromClasspath(
                    root.get("payload"),
                    SchemaType.INTERROGATION.getSchemaFileName(),
                    InterrogationDto.class,
                    jsonMapper
            );
            QuestionnaireDto questionnaire = extractCawiQuestionnaire(interrogationCommand);
            Interrogation interrogation = toInterrogation(command, interrogationCommand, questionnaire, correlationId);
            interrogationBatchService.saveInterrogation(interrogation);

            responseMessage = JMSOutputMessage.createResponse(ResponseCode.CREATED);

        } catch (InterrogationBatchException ibe) {
            log.error("InterrogationBatchException : {}", ibe.getMessage(), ibe);
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.BUSINESS_ERROR, ibe.getMessage());
        } catch (SchemaValidationException jsv) {
            log.error("JsonSchemaValidator : {}", jsv.getMessage(), jsv);
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, jsv.getMessage());
        } catch (JacksonException | IOException ioe) {
            log.error("IOException : {}", ioe.getMessage(), ioe);
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, ioe.getMessage());
        } catch (EntityNotFoundException enfe) {
            log.error("EntityNotFoundException : {}", enfe.getMessage(), enfe);
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.NOT_FOUND, enfe.getMessage());
        } catch (ValidationException ex) {
            log.error("ValidationException : {}", ex.getMessage(), ex);
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, ex.getMessage());
        } catch (Exception ex) {
            log.error("Exception : {}", ex.getMessage(), ex);
            responseMessage = JMSOutputMessage.createResponse(ResponseCode.TECHNICAL_ERROR, ex.getMessage());
        }
        replyQueuePublisher.send(replyQueue, correlationId, responseMessage);
    }

    private QuestionnaireDto extractCawiQuestionnaire(InterrogationDto interrogationCommand) {
        List<QuestionnaireDto> cawiQuestionnaires = interrogationCommand.getQuestionnaires().stream()
                .filter(q -> ModeDto.CAWI.equals(q.getMode()))
                .toList();
        if (cawiQuestionnaires.isEmpty()) {
            throw new InterrogationBatchException("Interrogation %s has no CAWI questionnaire".formatted(interrogationCommand.getId()));
        }
        if (cawiQuestionnaires.size() > 1) {
            throw new InterrogationBatchException("Interrogation %s should not have 2 CAWI questionnaires".formatted(interrogationCommand.getId()));
        }
        return cawiQuestionnaires.getFirst();
    }

    private Interrogation toInterrogation(CommandRequestDto command,
                                          InterrogationDto interrogationCommand,
                                          QuestionnaireDto questionnaire,
                                          String correlationId) throws ValidationException{
        UUID interrogationId = interrogationCommand.getId();
        if (interrogationId == null) {
            throw new ValidationException("InterrogationId is null for correlation ID %s".formatted(correlationId));
        }
        UUID partitioningId = interrogationCommand.getPartitionId();
        if (partitioningId == null) {
            throw new ValidationException("partitioning id is null for correlation ID %s".formatted(correlationId));
        }
        if (!(questionnaire.getData() instanceof ObjectNode data)) {
            throw new ValidationException("interrogation data is malformed for correlation ID %s".formatted(correlationId));
        }
        ArrayNode personalizationArrayNode = personalizationMapper.toArrayNode(interrogationCommand);
        return new Interrogation(
                interrogationId.toString(),
                interrogationCommand.getUsualSurveyUnitId(),
                partitioningId.toString(),
                questionnaire.getCollectionInstrumentId(),
                personalizationArrayNode,
                data,
                null,
                command.getCorrelationId());
    }
}