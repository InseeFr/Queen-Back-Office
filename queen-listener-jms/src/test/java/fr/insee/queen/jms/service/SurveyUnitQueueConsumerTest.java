package fr.insee.queen.jms.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitCommand;
import fr.insee.queen.domain.surveyunit.service.exception.SurveyUnitCommandException;
import fr.insee.queen.jms.model.JmsResponse;
import fr.insee.queen.jms.model.ResponseCode;
import fr.insee.queen.jms.service.stub.SurveyUnitCommandFakeService;
import fr.insee.queen.jms.service.stub.SurveyUnitFakePublisher;
import fr.insee.queen.jms.service.utils.JsonPropertyRetriever;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class SurveyUnitQueueConsumerTest {
    private SurveyUnitQueueConsumer consumer;
    private SurveyUnitFakePublisher publisher;
    private SurveyUnitCommandFakeService surveyUnitCommandService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private Message commandMessage;
    @Mock
    private Session session;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        surveyUnitCommandService = new SurveyUnitCommandFakeService();
        publisher = new SurveyUnitFakePublisher();
        consumer = new SurveyUnitQueueConsumer(mapper, surveyUnitCommandService, publisher);
    }

    @Test
    @DisplayName("Should create survey unit when message is valid")
    void ok() throws JMSException {
        // Given
        String correlationId = "123456";
        String questionnaireId = "questionnaire-id";
        String repositoryId = "survey-unit-id";
        String messageFormat = """
        {
            "correlationID": "%s",
            "replyTo": "queueResponse",
            "payload": {
                "questionnaireID": "%s",
                "repositoryId": "%s"
            }
        }
        """;
        String message = String.format(messageFormat, correlationId, questionnaireId, repositoryId);
        when(commandMessage.getBody(String.class)).thenReturn(message);

        // When
        consumer.createSurveyUnit(commandMessage, session);

        // Then
        // command is created
        SurveyUnitCommand surveyUnitCommandUsed = surveyUnitCommandService.getSurveyUnitCommandUsed();
        assertThat(surveyUnitCommandUsed.correlationId()).isEqualTo(correlationId);
        assertThat(surveyUnitCommandUsed.questionnaireId()).isEqualTo(questionnaireId);
        assertThat(surveyUnitCommandUsed.id()).isEqualTo(repositoryId);
        assertThat(surveyUnitCommandUsed.data()).isEqualTo(JsonNodeFactory.instance.objectNode());
        assertThat(surveyUnitCommandUsed.personalization()).isEqualTo(JsonNodeFactory.instance.arrayNode());

        // response publisher is called
        JmsResponse responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.CREATED.getCode());
        assertThat(responseMessage.message()).isEqualTo(ResponseCode.CREATED.name());
    }

    @Test
    @DisplayName("Should log error when no correlation id in command message")
    void shouldLogErrorWhenNoCorrelationId(CapturedOutput output) throws JMSException {
        String message = """
        {
            "replyTo": "queueResponse",
            "payload": {
                "questionnaireID": "questionnaire-id",
                "repositoryId": "survey-unit-id"
            }
        }
        """;
        checkInvalidMessageError(message, "correlationID", output);
    }

    @Test
    @DisplayName("Should log error when no reply to in command message")
    void shouldLogErrorWhenNoReplyTo(CapturedOutput output) throws JMSException {
        String message = """
        {
            "correlationID": "123456",
            "payload": {
                "questionnaireID": "questionnaire-id",
                "repositoryId": "survey-unit-id"
            }
        }
        """;
        checkInvalidMessageError(message, "replyTo", output);
    }

    private void checkInvalidMessageError(String invalidMessage, String invalidPropertyName, CapturedOutput output) throws JMSException {
        // Given
        when(commandMessage.getBody(String.class)).thenReturn(invalidMessage);

        // When
        consumer.createSurveyUnit(commandMessage, session);

        // Then
        assertThat(surveyUnitCommandService.getSurveyUnitCommandUsed()).isNull();
        String expectedLogMessage = String.format(JsonPropertyRetriever.PROPERTY_NOT_TEXTUAL_MESSAGE, invalidPropertyName);
        assertThat(output).contains(expectedLogMessage);
    }

    @Test
    @DisplayName("Should log error when invalid json in command message")
    void shouldLogErrorWhenInvalidJsonMessage(CapturedOutput output) throws JMSException {
        // Given
        when(commandMessage.getBody(String.class)).thenReturn("invalid json");

        // When
        consumer.createSurveyUnit(commandMessage, session);

        // Then
        assertThat(surveyUnitCommandService.getSurveyUnitCommandUsed()).isNull();
        assertThat(output).contains(JsonParseException.class.getName());
    }

    @Test
    @DisplayName("Should log error when jms exception")
    void shouldLogErrorWhenJMSException(CapturedOutput output) throws JMSException {
        // Given
        String exceptionMessage = "jms exception";
        when(commandMessage.getBody(String.class)).thenThrow(new JMSException(exceptionMessage));

        // When
        consumer.createSurveyUnit(commandMessage, session);

        // Then
        assertThat(surveyUnitCommandService.getSurveyUnitCommandUsed()).isNull();
        assertThat(output).contains(exceptionMessage);
    }

    @Test
    @DisplayName("Should send business error when questionnaire id is invalid")
    void shouldLogErrorWhenInvalidQuestionnaireId() throws JMSException {
        // Given
        String invalidMessage = """
        {
            "correlationID": "123456",
            "replyTo": "12345",
            "payload": {
                "repositoryId": "survey-unit-id"
            }
        }
        """;
        when(commandMessage.getBody(String.class)).thenReturn(invalidMessage);

        // When
        consumer.createSurveyUnit(commandMessage, session);

        // Then
        JmsResponse responseMessage = publisher.getResponseSent();
        String correlationId = publisher.getCorrelationIdUsed();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(surveyUnitCommandService.getSurveyUnitCommandUsed()).isNull();
        assertThat(correlationId).isEqualTo("123456");
        assertThat(replyQueue).isEqualTo("12345");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.BUSINESS_ERROR.getCode());
        String expectedMessage = String.format(JsonPropertyRetriever.PROPERTY_NOT_TEXTUAL_MESSAGE, "questionnaireID");
        assertThat(responseMessage.message()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should publisher send business error when repository id is invalid")
    void shouldLogErrorWhenInvalidRepositoryId() throws JMSException {
        // Given
        String invalidMessage = """
        {
            "correlationID": "123456",
            "replyTo": "12345",
            "payload": {
                "questionnaireID": "questionnaire-id"
            }
        }
        """;
        when(commandMessage.getBody(String.class)).thenReturn(invalidMessage);

        // When
        consumer.createSurveyUnit(commandMessage, session);

        // Then
        JmsResponse responseMessage = publisher.getResponseSent();
        String correlationId = publisher.getCorrelationIdUsed();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(surveyUnitCommandService.getSurveyUnitCommandUsed()).isNull();
        assertThat(correlationId).isEqualTo("123456");
        assertThat(replyQueue).isEqualTo("12345");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.BUSINESS_ERROR.getCode());
        String expectedMessage = String.format(JsonPropertyRetriever.PROPERTY_NOT_TEXTUAL_MESSAGE, "repositoryId");
        assertThat(responseMessage.message()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should publisher send business error when survey unit command exception")
    void shouldSendBusinessErrorWhenSurveyUnitCommandException() throws JMSException {
        // Given
        surveyUnitCommandService.setShouldThrowSurveyUnitCommandException(true);
        String correlationId = "123456";
        String questionnaireId = "questionnaire-id";
        String repositoryId = "survey-unit-id";
        String messageFormat = """
        {
            "correlationID": "%s",
            "replyTo": "queueResponse",
            "payload": {
                "questionnaireID": "%s",
                "repositoryId": "%s"
            }
        }
        """;
        String message = String.format(messageFormat, correlationId, questionnaireId, repositoryId);
        when(commandMessage.getBody(String.class)).thenReturn(message);

        // When
        consumer.createSurveyUnit(commandMessage, session);

        // response publisher is called
        JmsResponse responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.BUSINESS_ERROR.getCode());
        String expectedMessage = String.format(SurveyUnitCommandException.CAMPAIGN_NOT_FOUND_MESSAGE, questionnaireId);
        assertThat(responseMessage.message()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should publisher send business error when survey unit command exception")
    void shouldSendTechnicalErrorWhenRuntimeException() throws JMSException {
        // Given
        surveyUnitCommandService.setShouldThrowRuntimeException(true);
        String correlationId = "123456";
        String questionnaireId = "questionnaire-id";
        String repositoryId = "survey-unit-id";
        String messageFormat = """
        {
            "correlationID": "%s",
            "replyTo": "queueResponse",
            "payload": {
                "questionnaireID": "%s",
                "repositoryId": "%s"
            }
        }
        """;
        String message = String.format(messageFormat, correlationId, questionnaireId, repositoryId);
        when(commandMessage.getBody(String.class)).thenReturn(message);

        // When
        consumer.createSurveyUnit(commandMessage, session);

        // response publisher is called
        JmsResponse responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
        assertThat(responseMessage.message()).isEqualTo(SurveyUnitCommandFakeService.RUNTIME_EXCEPTION_MESSAGE);
    }
}

