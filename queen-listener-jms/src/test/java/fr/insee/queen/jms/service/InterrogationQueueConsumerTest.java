package fr.insee.queen.jms.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.domain.interrogation.model.InterrogationCommand;
import fr.insee.queen.domain.interrogation.service.exception.InterrogationCommandException;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import fr.insee.queen.jms.service.stub.InterrogationCommandFakeService;
import fr.insee.queen.jms.service.stub.InterrogationFakePublisher;
import fr.insee.queen.jms.service.utils.PropertyValidator;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class InterrogationQueueConsumerTest {
    private InterrogationQueueConsumer consumer;
    private InterrogationFakePublisher publisher;
    private InterrogationCommandFakeService interrogationCommandFakeService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private Message commandMessage;
    @Mock
    private Session session;

    private final String correlationId = "123456";
    private final UUID interrogationId = UUID.randomUUID();
    private final String questionnaireId = "questionnaire-id";
    private final UUID surveyUnitId = UUID.randomUUID();
    private final String defaultBody = """ 
        {
            "replyTo": "queueResponse",
            "correlationID": "%s",
            "payload": {
                "interrogationId": "%s",
                "questionnaires": [
                    {
                        "questionnaireModelId": "%s"
                    }
                ],
                "surveyUnitId": "%s"
            }
        }""";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        interrogationCommandFakeService = new InterrogationCommandFakeService();
        publisher = new InterrogationFakePublisher();
        consumer = new InterrogationQueueConsumer(mapper, interrogationCommandFakeService, publisher);
    }

    @Test
    @DisplayName("Should create interrogation when message is valid")
    void ok() throws JMSException, JsonProcessingException {
        // Given
        String message = String.format(defaultBody, correlationId, interrogationId, questionnaireId, surveyUnitId);
        when(commandMessage.getBody(String.class)).thenReturn(message);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        // command is created
        InterrogationCommand interrogationCommandUsed = interrogationCommandFakeService.getInterrogationCommandUsed();
        assertThat(interrogationCommandUsed.correlationId()).isEqualTo(correlationId);
        assertThat(interrogationCommandUsed.questionnaireId()).isEqualTo(questionnaireId);
        assertThat(interrogationCommandUsed.id()).isEqualTo(interrogationId.toString());
        assertThat(interrogationCommandUsed.surveyUnitId()).isEqualTo(surveyUnitId.toString());
        assertThat(interrogationCommandUsed.data()).isEqualTo(JsonNodeFactory.instance.objectNode());
        assertThat(interrogationCommandUsed.personalization()).isEqualTo(JsonNodeFactory.instance.arrayNode());

        // response publisher is called
        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.CREATED.getCode());
        assertThat(responseMessage.message()).isEqualTo(ResponseCode.CREATED.name());
    }

    @Test
    @DisplayName("Should log error when no correlation id in command message")
    void shouldLogErrorWhenNoCorrelationId(CapturedOutput output) throws JMSException, JsonProcessingException {
        String message = """
        {
            "replyTo": "queueResponse",
            "payload": {
                "interrogationId": "%s",
                "questionnaires": [
                    {
                        "questionnaireModelId": "%s"
                    }
                ],
                "surveyUnitId": "%s"
            }
        }
        """;
        String invalidMessage = String.format(message, interrogationId, questionnaireId, surveyUnitId);

        checkInvalidMessageError(invalidMessage, "correlationID", output);
    }

    @Test
    @DisplayName("Should log error when no reply to in command message")
    void shouldLogErrorWhenNoReplyTo(CapturedOutput output) throws JMSException, JsonProcessingException {
        String message = """
        {
            "correlationID": "%s",
            "payload": {
                "interrogationId": "%s",
                "questionnaires": [
                    {
                        "questionnaireModelId": "%s"
                    }
                ],
                "surveyUnitId": "%s"
            }
        }
        """;
        String noReplyToMessage = String.format(message, correlationId, interrogationId, questionnaireId, surveyUnitId);

        checkInvalidMessageError(noReplyToMessage, "replyTo", output);
    }

    private void checkInvalidMessageError(String invalidMessage, String invalidPropertyName, CapturedOutput output) throws JMSException, JsonProcessingException {
        // Given
        when(commandMessage.getBody(String.class)).thenReturn(invalidMessage);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationCommandFakeService.getInterrogationCommandUsed()).isNull();
        String expectedLogMessage = String.format(PropertyValidator.PROPERTY_NOT_EMPTY, invalidPropertyName);
        assertThat(output).contains(expectedLogMessage);
    }

    @Test
    @DisplayName("Should log error when invalid json in command message")
    void shouldLogErrorWhenInvalidJsonMessage(CapturedOutput output) throws JMSException, JsonProcessingException {
        // Given
        when(commandMessage.getBody(String.class)).thenReturn("invalid json");

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationCommandFakeService.getInterrogationCommandUsed()).isNull();
        assertThat(output).contains(JsonParseException.class.getName());
    }

    @Test
    @DisplayName("Should log error when jms exception")
    void shouldLogErrorWhenJMSException(CapturedOutput output) throws JMSException, JsonProcessingException {
        // Given
        String exceptionMessage = "jms exception";
        when(commandMessage.getBody(String.class)).thenThrow(new JMSException(exceptionMessage));

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationCommandFakeService.getInterrogationCommandUsed()).isNull();
        assertThat(output).contains(exceptionMessage);
    }

    @Test
    @DisplayName("Should send business error when questionnaire id is invalid")
    void shouldLogErrorWhenInvalidQuestionnaireId() throws JMSException, JsonProcessingException {
        // Given
        String invalidMessage = """
        {
            "correlationID": "%s",
            "replyTo": "12345",
            "payload": {
                "interrogationId": "%s",
                "questionnaires": [
                    {
                    }
                ],
                "surveyUnitId": "%s"
            }
        }
        """;
        String messageQuestionnaireInvalid = String.format(invalidMessage, correlationId, interrogationId, surveyUnitId);

        when(commandMessage.getBody(String.class)).thenReturn(messageQuestionnaireInvalid);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String correlationPublisherId = publisher.getCorrelationIdUsed();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(interrogationCommandFakeService.getInterrogationCommandUsed()).isNull();
        assertThat(correlationPublisherId).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("12345");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.BUSINESS_ERROR.getCode());
        String expectedMessage = String.format(PropertyValidator.PROPERTY_NOT_EMPTY, "questionnaireModelId");
        assertThat(responseMessage.message()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should publisher send business error when survey unit id is invalid")
    void shouldLogErrorWhenInvalidSurveyUnitId() throws JMSException, JsonProcessingException {
        // Given
        String invalidMessage = """
        {
            "correlationID": "%s",
            "replyTo": "12345",
            "payload": {
                "interrogationId": "%s",
                "questionnaires": [
                    {
                        "questionnaireModelId": "%s"
                    }
                ]
            }
        }
        """;
        String messageNoSurveyUnitId = String.format(invalidMessage, correlationId, interrogationId, questionnaireId);

        when(commandMessage.getBody(String.class)).thenReturn(messageNoSurveyUnitId);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String correlationPublisherId = publisher.getCorrelationIdUsed();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(interrogationCommandFakeService.getInterrogationCommandUsed()).isNull();
        assertThat(correlationPublisherId).isEqualTo("123456");
        assertThat(replyQueue).isEqualTo("12345");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.BUSINESS_ERROR.getCode());
        String expectedMessage = String.format(PropertyValidator.PROPERTY_NOT_EMPTY, "surveyUnitId");
        assertThat(responseMessage.message()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should publisher send business error when interrogation command exception")
    void shouldSendBusinessErrorWhenSurveyUnitCommandException() throws JMSException, JsonProcessingException {
        // Given
        interrogationCommandFakeService.setShouldThrowInterrogationCommandException(true);
        String messageFormat = """
        {
            "correlationID": "%s",
            "replyTo": "queueResponse",
            "payload": {
                "interrogationId": "%s",
                "questionnaires": [
                    {
                        "questionnaireModelId": "%s"
                    }
                ],
                "surveyUnitId": "%s"
            }
        }
        """;
        String message = String.format(messageFormat, correlationId, interrogationId, questionnaireId, surveyUnitId);
        when(commandMessage.getBody(String.class)).thenReturn(message);

        // When
        consumer.createInterrogation(commandMessage, session);

        // response publisher is called
        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.BUSINESS_ERROR.getCode());
        String expectedMessage = String.format(InterrogationCommandException.CAMPAIGN_NOT_FOUND_MESSAGE, questionnaireId);
        assertThat(responseMessage.message()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should publisher send business error when interrogation command exception")
    void shouldSendTechnicalErrorWhenRuntimeException() throws JMSException, JsonProcessingException {
        // Given
        interrogationCommandFakeService.setShouldThrowRuntimeException(true);
        String message = String.format(defaultBody, correlationId, interrogationId, questionnaireId, surveyUnitId);
        when(commandMessage.getBody(String.class)).thenReturn(message);

        // When
        consumer.createInterrogation(commandMessage, session);

        // response publisher is called
        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
        assertThat(responseMessage.message()).isEqualTo(InterrogationCommandFakeService.RUNTIME_EXCEPTION_MESSAGE);
    }
}

