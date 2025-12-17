package fr.insee.queen.jms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import fr.insee.queen.jms.service.stub.InterrogationBatchFakeService;
import fr.insee.queen.jms.service.stub.InterrogationFakePublisher;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class InterrogationQueueConsumerTest {
    private InterrogationQueueConsumer consumer;
    private InterrogationFakePublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();
    private InterrogationBatchFakeService interrogationBatchFakeService;
    @Mock
    private Message commandMessage;
    @Mock
    private Session session;

    private String additionalFieldCommand = "";
    private String additionalFieldInterrogation = "";
    private final String interrogationId = "a1b2c3d4-e5f6-4789-abcd-112233445566";
    private final String surveyUnitId = "0f1e2d3c-4b5a-6978-9123-abcdefabcdef";
    private final String questionnaireId = "questionnaire-id";
    private final String correlationId = "c7f0a0b1-9d8c-4e7f-b6a5-1234567890ab";
    private final String replyTo = "queueResponse";

    private final String defaultBody = """
                                            {
                                              "correlationId": "%s",
                                              "processInstanceId": "9f2c9f4a-5a2b-4f0e-9a6f-2c8f0c3a1d55",
                                              "target": "QUESTIONNAIRE-API-WEB",
                                              "operation": "CREATE",
                                              "aggregateType": "INTERROGATION",
                                              %s
                                              "payload": {
                                                "replyTo": "%s",
                                                "partitionId": "3d3f6a2b-8d4d-4d7a-9c0b-1a2b3c4d5e6f",
                                                %s
                                                "interrogationId": "%s",
                                                "surveyUnitId": "%s",
                                                "campaignId": "ECO-ENT-2025",
                                                "questionnaireId": "%s",
                                                "originId": "552100123",
                                                "parentId": "11111111-2222-3333-4444-555555555555",
                                                "childIds": [
                                                  "66666666-7777-8888-9999-000000000000",
                                                  "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                                                ],
                                                "displayName": "12345678900011",
                                                "corporateName": "Société Exemple SA",
                                                "unitLabel": "établissement",
                                                "ape": "62.01Z",
                                                "legalCategory": "5710",
                                                "turnover": "1500000",
                                                "workforce": "42",
                                                "managementId": "OPALE-DEM-75",
                                                "ssech": "SSECH-01",
                                                "noGrap": "12",
                                                "noLog": "03",
                                                "comment": "Contact préférable le matin.",
                                                "cityCode": "75120",
                                                "extCoverPageData": {
                                                  "whoAnswers1": "Le dirigeant ou son représentant",
                                                  "whoAnswers2": "Service comptable si nécessaire",
                                                  "whoAnswers3": "Répondre sous 10 jours"
                                                },
                                                "extPostCollectionData": {},
                                                "extFaData": {},
                                                "questionnaires": [
                                                  {
                                                    "questionnaireModelId": "%s",
                                                    "questionningData": {
                                                      "prefill_year": 2024,
                                                      "language": "fr"
                                                    }
                                                  }
                                                ],
                                                "communicationPersos": [
                                                  {
                                                    "communicationId": "2d2ab0b2-7e9f-4b2a-8e61-8f8c1d1f2a3b",
                                                    "extCommunicationData": [
                                                      {
                                                        "key": "RAISON_SOCIALE",
                                                        "value": "Société Exemple SA",
                                                        "type": "string"
                                                      },
                                                      {
                                                        "key": "EMAIL_VALIDE",
                                                        "value": true,
                                                        "type": "boolean"
                                                      }
                                                    ]
                                                  },
                                                  {
                                                    "communicationId": "9b8a7c6d-5e4f-4321-98ab-0123456789ab",
                                                    "extCommunicationData": [
                                                      {
                                                        "key": "SIREN",
                                                        "value": "123456789",
                                                        "type": "string"
                                                      }
                                                    ]
                                                  }
                                                ],
                                                "address": {
                                                  "streetNumber": "10",
                                                  "repetitionIndex": "",
                                                  "streetType": "rue",
                                                  "streetName": "de la République",
                                                  "addressSupplement": "Bâtiment B",
                                                  "cityName": "Paris",
                                                  "zipCode": "75011",
                                                  "cedexCode": "",
                                                  "cedexName": "",
                                                  "specialDistribution": "",
                                                  "countryCode": "FR",
                                                  "countryName": "France"
                                                },
                                                "addressComplement": {
                                                  "building": "B",
                                                  "floor": "3",
                                                  "staircase": "Escalier 2",
                                                  "door": "3B",
                                                  "elevator": true,
                                                  "cityPriorityDistrict": false
                                                },
                                                "contacts": [
                                                  {
                                                    "contactId": "e3f1b1c2-d3a4-45ef-9a10-b2c3d4e5f6a7",
                                                    "gender": "MME",
                                                    "firstName": "Claire",
                                                    "lastName": "Martin",
                                                    "dateOfBirth": "1987-04-12",
                                                    "function": "Responsable administratif",
                                                    "businessName": "Société Exemple SA",
                                                    "contactRank": 1,
                                                    "phoneNumbers": [
                                                      {
                                                        "source": "ADMINISTRATIVE",
                                                        "favorite": true,
                                                        "number": "+33 1 23 45 67 89"
                                                      },
                                                      {
                                                        "source": "INTERVIEWER",
                                                        "favorite": false,
                                                        "number": "+33 6 12 34 56 78"
                                                      }
                                                    ],
                                                    "email": "claire.martin@example.com",
                                                    "address": {
                                                      "streetNumber": "10",
                                                      "repetitionIndex": "",
                                                      "streetType": "rue",
                                                      "streetName": "de la République",
                                                      "addressSupplement": "",
                                                      "cityName": "Paris",
                                                      "zipCode": "75011",
                                                      "cedexCode": "",
                                                      "cedexName": "",
                                                      "specialDistribution": "",
                                                      "countryCode": "FR",
                                                      "countryName": "France"
                                                    },
                                                    "webConnectionId": "WEB-PORTAL-USER-001"
                                                  }
                                                ]
                                              }
                                            }
                                            """;

    @BeforeEach
    void setup() {
        Locale.setDefault(Locale.US);
        MockitoAnnotations.openMocks(this);
        interrogationBatchFakeService = new InterrogationBatchFakeService();
        publisher = new InterrogationFakePublisher();
        consumer = new InterrogationQueueConsumer(mapper, publisher, interrogationBatchFakeService);
    }

    @Test
    @DisplayName("Should create interrogation when message is valid")
    void ok() throws JMSException {
        // Given
        String ok = String.format(defaultBody, correlationId, additionalFieldCommand, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireId);
        when(commandMessage.getBody(String.class)).thenReturn(ok);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        Interrogation interrogationBatchUsed = interrogationBatchFakeService.getInterrogationBatchUsed();
        assertThat(interrogationBatchUsed.id()).isEqualTo("a1b2c3d4-e5f6-4789-abcd-112233445566");
        assertThat(interrogationBatchUsed.surveyUnitId()).isEqualTo("0f1e2d3c-4b5a-6978-9123-abcdefabcdef");
        assertThat(interrogationBatchUsed.questionnaireId()).isNull();
        assertThat(interrogationBatchUsed.correlationId()).isNull();

        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo("c7f0a0b1-9d8c-4e7f-b6a5-1234567890ab");
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.CREATED.getCode());
        assertThat(responseMessage.message()).isEqualTo(ResponseCode.CREATED.name());
    }

    @Test
    @DisplayName("Should log error when additional field command")
    void ShouldLogErrorWhenAdditionalFieldCommand(CapturedOutput output) throws JMSException {
        // Given
        additionalFieldCommand = "\"newFieldCommand\": true,";
        String additionalFieldCommandMessage = String.format(defaultBody, correlationId, additionalFieldCommand, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireId);
        // When and Then
        checkInvalidMessageError(additionalFieldCommandMessage, "IOException : Unrecognized field \"newFieldCommand\"", output);
    }

    @Disabled
    @Test
    @DisplayName("Should log error when additional field interrogation")
    void ShouldLogErrorWhenAdditionalFieldInterrogation(CapturedOutput output) throws JMSException {
        // Given
        additionalFieldInterrogation = "\"newFieldInterrogation\": true,";
        String additionalFieldInterrogationMessage = String.format(defaultBody, correlationId, additionalFieldCommand, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireId);
        // When and Then
        checkInvalidMessageError(additionalFieldInterrogationMessage, "$.payload: property 'newFieldInterrogation' is not defined in the schema and the schema does not allow additional properties", output);
    }

    @Test
    @DisplayName("Should log error when no correlation id in command message")
    void shouldLogErrorWhenNoCorrelationId(CapturedOutput output) throws JMSException {
        // Given
        String invalidMessage = String.format(defaultBody, null, additionalFieldCommand, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireId);
        // When and Then
        checkInvalidMessageError(invalidMessage, "PropertyException : Missing or null field : 'correlationId'", output);
    }

    @Test
    @DisplayName("Should log error when no reply to in command message")
    void shouldLogErrorWhenNoReplyTo(CapturedOutput output) throws JMSException {
        // Given
        String noReplyToMessage = String.format(defaultBody, correlationId, additionalFieldCommand, null, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireId);
        // When and Then
        checkInvalidMessageError(noReplyToMessage, "PropertyException : Missing or null field : 'replyTo'", output);
    }


    @Disabled
    @Test
    @DisplayName("Should log error when jms exception")
    void shouldLogErrorWhenJMSException(CapturedOutput output) throws JMSException {
        // Given
        String exceptionMessage = "jms exception";
        when(commandMessage.getBody(String.class)).thenThrow(new JMSException(exceptionMessage));

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(output).contains(exceptionMessage);
    }

    @Disabled
    @Test
    @DisplayName("Should publisher send business error when survey unit id is invalid")
    void shouldLogErrorWhenInvalidSurveyUnitId() throws JMSException {
        // Given
        String messageNoSurveyUnitId = String.format(defaultBody, correlationId, additionalFieldCommand, replyTo, additionalFieldInterrogation, interrogationId, null, questionnaireId, questionnaireId);
        when(commandMessage.getBody(String.class)).thenReturn(messageNoSurveyUnitId);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String correlationPublisherId = publisher.getCorrelationIdUsed();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(correlationPublisherId).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
    }

    @Disabled
    @Test
    @DisplayName("Should publisher send business error when interrogation command exception")
    void shouldSendBusinessErrorWhenSurveyUnitCommandException() throws JMSException {
        // Given
        interrogationBatchFakeService.setShouldThrowInterrogationBatchException(true);
        String message = String.format(defaultBody, correlationId, additionalFieldCommand, replyTo, additionalFieldInterrogation, interrogationId, null, questionnaireId, questionnaireId);
        when(commandMessage.getBody(String.class)).thenReturn(message);

        // When
        consumer.createInterrogation(commandMessage, session);

        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
    }

    private void checkInvalidMessageError(String invalidMessage, String invalidPropertyName, CapturedOutput output) throws JMSException {
        // Given
        when(commandMessage.getBody(String.class)).thenReturn(invalidMessage);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        String expectedLogMessage = String.format(invalidPropertyName);
        assertThat(output).contains(expectedLogMessage);
    }

    @Test
    @DisplayName("Should map InterrogationBatchException to BUSINESS_ERROR and send a response")
    void throwInterrogationBatchException(CapturedOutput output) throws JMSException {
        // Given
        interrogationBatchFakeService.setShouldThrowInterrogationBatchException(true);
        String msg = String.format(defaultBody, correlationId, additionalFieldCommand, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireId);
        when(commandMessage.getBody(String.class)).thenReturn(msg);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        JMSOutputMessage response = publisher.getResponseSent();
        assertThat(response.message()).contains("InterrogationBatchException").doesNotContain("TECHNICAL_ERROR");
        assertThat(publisher.getReplyQueueUsed()).isEqualTo(replyTo);
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(output).contains("InterrogationBatchException");
    }

    @Test
    @DisplayName("Should map SchemaValidationException to BUSINESS_ERROR and send a response")
    void throwSchemaValidationException(CapturedOutput output) throws JMSException {
        // Given
        interrogationBatchFakeService.setShouldThrowSchemaValidationException(true);
        String msg = String.format(defaultBody, correlationId, additionalFieldCommand, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireId);
        when(commandMessage.getBody(String.class)).thenReturn(msg);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        JMSOutputMessage response = publisher.getResponseSent();
        assertThat(response.message()).contains("SchemaValidationException").doesNotContain("TECHNICAL_ERROR");
        assertThat(publisher.getReplyQueueUsed()).isEqualTo(replyTo);
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(output).contains("SchemaValidationException");
    }

    @Test
    @DisplayName("Should map EntityNotFoundException to BUSINESS_ERROR and send a response")
    void throwEntityNotFoundException(CapturedOutput output) throws JMSException {
        // Given
        interrogationBatchFakeService.setShouldThrowEntityNotFoundException(true);
        String msg = String.format(defaultBody, correlationId, additionalFieldCommand, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireId);
        when(commandMessage.getBody(String.class)).thenReturn(msg);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        JMSOutputMessage response = publisher.getResponseSent();
        assertThat(response.message()).contains("EntityNotFoundException").doesNotContain("TECHNICAL_ERROR");
        assertThat(publisher.getReplyQueueUsed()).isEqualTo(replyTo);
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(output).contains("EntityNotFoundException");
    }
}