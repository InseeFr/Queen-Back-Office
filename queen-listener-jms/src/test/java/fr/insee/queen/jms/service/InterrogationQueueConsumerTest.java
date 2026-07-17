package fr.insee.queen.jms.service;

import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.jms.mapper.PersonalizationMapper;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import fr.insee.queen.jms.service.stub.InterrogationBatchFakeService;
import fr.insee.queen.jms.service.stub.InterrogationFakePublisher;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import tools.jackson.databind.json.JsonMapper;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
@ExtendWith(MockitoExtension.class)
class InterrogationQueueConsumerTest {
    private PersonalizationMapper personalizationMapper;
    private InterrogationQueueConsumer consumer;
    private InterrogationFakePublisher publisher;
    private final JsonMapper mapper = new JsonMapper();
    private InterrogationBatchFakeService interrogationBatchFakeService;
    @Mock
    private Message commandMessage;

    private String additionalFieldCommand = "";
    private String additionalFieldInterrogation = "";
    private final String interrogationId = "a1b2c3d4-e5f6-4789-abcd-112233445566";
    private final String surveyUnitId = "0f1e2d3c-4b5a-6978-9123-abcdefabcdef";
    private final String questionnaireId = "questionnaire-id";
    private final UUID correlationId = UUID.fromString("c7f0a0b1-9d8c-4e7f-b6a5-1234567890ab");
    private final String replyTo = "queueResponse";
    private final String questionnaireData = "{}";

    private final String defaultBody = """
    {
      %s
      "correlationId": "%s",
      "replyTo": "%s",
      "processInstanceId": "019a5931-5951-7dd2-aa53-28c0cd92d569",
      "target": "QUESTIONNAIRE-API-WEB",
      "operation": "CREATE",
      "payload": {
        %s
        "partitionId": "123e4567-e89b-12d3-a456-426614174000",
        "id": "%s",
        "technicalSurveyUnitId": "123e4567-e89b-12d3-a456-426614174002",
        "originId": "410241145",
        "parentId": "123e4567-e89b-12d3-a456-426614174003",
        "childIds": [
          "123e4567-e89b-12d3-a456-426614174004",
          "123e4567-e89b-12d3-a456-426614174005"
        ],
        "usualSurveyUnitId": "%s",
        "corporateName": "CAFET'INSEE LILLE",
        "unitLabel": "établissement",
        "ape": "9499Z",
        "legalCategory": "Association déclarée",
        "turnover": "",
        "workforce": "",
        "managementId": "82",
        "subSampleIdentifier": "02",
        "clusterIdentifier": "",
        "dwellingIdentifier": "",
        "comment": "Cho n'marche po, pis in n'sait po pourquo",
        "cityCode": "92049",
        "extCoverPageData": {
          "whoAnswers1": "Pierre",
          "whoAnswers2": "Paul",
          "whoAnswers3": "Jack"
        },
        "extPostCollectionData": {
          "someKey": "someValue"
        },
        "extFaData": {
          "surface": "149",
          "identifiant_strate": "up_01031_NP",
          "statut_occupation": "P",
          "nb_pers_log": "4",
          "someKey": "someValue"
        },
        "questionnaires": [
          {
            "collectionInstrumentId": "%s",
            "mode": "CAWI",
            "data": %s
          }
        ],
        "personalizedCommunications": [
          {
            "communicationId": "68893406-b88f-49bb-badd-677c4e73324f",
            "extCommunicationData": [
              {
                "key": "noticeLetterIntroduction",
                "type": "string",
                "value": "Pierre est sélectionné"
              }
            ]
          }
        ],
        "address": {
          "streetNumber": "130",
          "repetitionIndex": "",
          "streetType": "avenue",
          "streetName": "du president John Fitzgerald Kennedy",
          "addressSupplement": "",
          "cityName": "Lille",
          "zipCode": "59800",
          "businessZipCode": "",
          "businessZipCodeName": "",
          "specialDistribution": "",
          "countryCode": "",
          "countryName": "France"
        },
        "addressComplement": {
          "building": "Bâtiment A",
          "floor": "3ème",
          "staircase": "Escalier B",
          "door": "Porte 2",
          "elevator": true,
          "cityPriorityDistrict": false
        },
        "contacts": [
          {
            "id": "123e4567-e89b-12d3-a456-426614174006",
            "gender": "M",
            "firstName": "Jean",
            "lastName": "Dupont",
            "dateOfBirth": "1985-06-15",
            "function": "Comptable",
            "businessName": "Raison sociale contact 1",
            "rank": 1,
            "phoneNumbers": [
              {
                "source": "DIRECTORY",
                "favorite": false,
                "number": "+33123456789"
              },
              {
                "source": "INTERVIEWER",
                "favorite": true,
                "number": "+33198765432"
              }
            ],
            "email": "jean.dupont@example.com",
            "address": {
              "streetNumber": "88",
              "repetitionIndex": "",
              "streetType": "avenue",
              "streetName": "Verdier",
              "addressSupplement": "",
              "cityName": "Montrouge",
              "zipCode": "92120",
              "businessZipCode": "",
              "businessZipCodeName": "",
              "specialDistribution": "",
              "countryCode": "",
              "countryName": "France"
            },
            "webConnectionId": "CMPTCT1"
          },
          {
            "id": "123e4567-e89b-12d3-a456-426614174007",
            "gender": "MME",
            "firstName": "Marie",
            "lastName": "Durand",
            "dateOfBirth": "1990-08-20",
            "function": "Assistante",
            "businessName": "Raison sociale contact 2",
            "rank": 2,
            "phoneNumbers": [
              {
                "source": "ADMINISTRATIVE",
                "favorite": false,
                "number": "+33234567890"
              },
              {
                "source": "DIRECTORY",
                "favorite": true,
                "number": "+33209876543"
              }
            ],
            "email": "marie.durand@example.com",
            "address": {
              "streetNumber": "15",
              "repetitionIndex": "",
              "streetType": "rue",
              "streetName": "du general Hulot",
              "addressSupplement": "",
              "cityName": "Nancy",
              "zipCode": "54000",
              "businessZipCode": "",
              "businessZipCodeName": "",
              "specialDistribution": "",
              "countryCode": "",
              "countryName": "France"
            },
            "webConnectionId": "CMPTCT2"
          }
        ]
      },
      "aggregateType": "INTERROGATION"
    }""";

    @BeforeEach
    void setup() {
        personalizationMapper = new PersonalizationMapper(mapper);
        Locale.setDefault(Locale.US);
        interrogationBatchFakeService = new InterrogationBatchFakeService();
        publisher = new InterrogationFakePublisher();
        consumer = new InterrogationQueueConsumer(mapper, publisher, interrogationBatchFakeService, personalizationMapper);
    }

    @Test
    @DisplayName("Should create interrogation when message is valid")
    void ok() throws JMSException {
        // Given
        String ok = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData);
        when(commandMessage.getBody(String.class)).thenReturn(ok);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        Interrogation interrogationBatchUsed = interrogationBatchFakeService.getInterrogationBatchUsed();
        assertThat(interrogationBatchUsed.id()).isEqualTo("a1b2c3d4-e5f6-4789-abcd-112233445566");
        assertThat(interrogationBatchUsed.surveyUnitId()).isEqualTo("0f1e2d3c-4b5a-6978-9123-abcdefabcdef");
        assertThat(interrogationBatchUsed.questionnaireId()).isNull();
        assertThat(interrogationBatchUsed.correlationId()).isNull();

        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.CREATED.getCode());
        assertThat(responseMessage.message()).isEqualTo(ResponseCode.CREATED.name());
    }

    @Test
    @DisplayName("Should log error when no correlation id in command message")
    void shouldLogErrorWhenNoCorrelationId(CapturedOutput output) throws JMSException {
        // Given
        String invalidMessage = defaultBody.formatted(additionalFieldCommand, null, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData);
        // When and Then
        checkInvalidMessageError(invalidMessage, "Cannot process message !!! Exception : Missing or null field : 'correlationId'", output);
    }

    @Test
    @DisplayName("Should log error when no reply to in command message")
    void shouldLogErrorWhenNoReplyTo(CapturedOutput output) throws JMSException {
        // Given
        String noReplyToMessage = defaultBody.formatted(additionalFieldCommand, correlationId, null, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData);
        // When and Then
        checkInvalidMessageError(noReplyToMessage, "Cannot process message !!! Exception : Missing or null field : 'replyTo'", output);
    }

    @Test
    @DisplayName("Should log error when invalid json in command message")
    void shouldLogErrorWhenInvalidJsonMessageCommandMessage(CapturedOutput output) throws JMSException {
        // Given
        String messagesBroker = defaultBody.formatted(additionalFieldCommand, correlationId + "\\", replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData);
        when(commandMessage.getBody(String.class)).thenReturn(messagesBroker);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(output).contains("ERROR fr.insee.queen.jms.service.InterrogationQueueConsumer -- Cannot process message !!!");
    }

    @Test
    @DisplayName("Should log error when invalid json in interrogation")
    void shouldLogErrorWhenInvalidJsonMessageInterrogation(CapturedOutput output) throws JMSException {
        // Given
        String messagesBroker = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId + "\\", questionnaireData);
        when(commandMessage.getBody(String.class)).thenReturn(messagesBroker);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(output).contains("ERROR fr.insee.queen.jms.service.InterrogationQueueConsumer -- Cannot process message !!!");
    }

    private void checkInvalidMessageError(String invalidMessage, String invalidPropertyName, CapturedOutput output) throws JMSException {
        // Given
        when(commandMessage.getBody(String.class)).thenReturn(invalidMessage);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        String expectedLogMessage = invalidPropertyName.formatted();
        assertThat(output).contains(expectedLogMessage);
    }

    @Test
    @DisplayName("Should map InterrogationBatchException to BUSINESS_ERROR and send a response")
    void throwInterrogationBatchException(CapturedOutput output) throws JMSException {
        // Given
        interrogationBatchFakeService.setShouldThrowInterrogationBatchException(true);
        String msg = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData);
        when(commandMessage.getBody(String.class)).thenReturn(msg);

        // When
        consumer.createInterrogation(commandMessage);

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
        String msg = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData);
        when(commandMessage.getBody(String.class)).thenReturn(msg);

        // When
        consumer.createInterrogation(commandMessage);

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
        String msg = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData);
        when(commandMessage.getBody(String.class)).thenReturn(msg);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        JMSOutputMessage response = publisher.getResponseSent();
        assertThat(response.message()).contains("EntityNotFoundException").doesNotContain("TECHNICAL_ERROR");
        assertThat(publisher.getReplyQueueUsed()).isEqualTo(replyTo);
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(output).contains("EntityNotFoundException");
    }

    @Test
    @DisplayName("Should send BUSINESS_ERROR when no CAWI questionnaire is present")
    void shouldSendBusinessErrorWhenNoCawiQuestionnaire() throws JMSException {
        // Given
        String body = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData)
                .replace("\"mode\": \"CAWI\"", "\"mode\": \"CAPI\"");
        when(commandMessage.getBody(String.class)).thenReturn(body);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        JMSOutputMessage response = publisher.getResponseSent();
        assertThat(response.code()).isEqualTo(ResponseCode.BUSINESS_ERROR.getCode());
        assertThat(response.message()).contains("has no CAWI questionnaire");
        assertThat(publisher.getReplyQueueUsed()).isEqualTo(replyTo);
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
    }

    @Test
    @DisplayName("Should send BUSINESS_ERROR when multiple CAWI questionnaires are present")
    void shouldSendBusinessErrorWhenMultipleCawiQuestionnaires() throws JMSException {
        // Given: prepend a second CAWI questionnaire before the existing one
        String secondCawi = "{\"collectionInstrumentId\": \"other-id\", \"mode\": \"CAWI\", \"data\": {}},";
        String body = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData)
                .replace("\"questionnaires\": [\n", "\"questionnaires\": [\n" + secondCawi + "\n");
        when(commandMessage.getBody(String.class)).thenReturn(body);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        JMSOutputMessage response = publisher.getResponseSent();
        assertThat(response.code()).isEqualTo(ResponseCode.BUSINESS_ERROR.getCode());
        assertThat(response.message()).contains("should not have 2 CAWI questionnaires");
        assertThat(publisher.getReplyQueueUsed()).isEqualTo(replyTo);
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
    }

    @Test
    @DisplayName("Should send TECHNICAL_ERROR when interrogationId is absent in payload")
    void shouldSendTechnicalErrorWhenInterrogationIdIsNull(CapturedOutput output) throws JMSException {
        // Given — id absent (not required by schema) → null after deserialization → triggers toInterrogation guard
        String body = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData)
                .replace("\"id\": \"" + interrogationId + "\",", "");
        when(commandMessage.getBody(String.class)).thenReturn(body);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        JMSOutputMessage response = publisher.getResponseSent();
        assertThat(response.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
        assertThat(output).contains("InterrogationId is null");
    }

    @Test
    @DisplayName("Should send TECHNICAL_ERROR when partitionId is null in payload")
    void shouldSendTechnicalErrorWhenPartitionIdIsNull(CapturedOutput output) throws JMSException {
        // Given — partitionId is required+uuid in schema, null fails type check → SchemaValidationException
        String body = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData)
                .replace("\"partitionId\": \"123e4567-e89b-12d3-a456-426614174000\"", "\"partitionId\": null");
        when(commandMessage.getBody(String.class)).thenReturn(body);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        JMSOutputMessage response = publisher.getResponseSent();
        assertThat(response.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
        assertThat(output).contains("JsonSchemaValidator");
    }

    @Test
    @DisplayName("Should send TECHNICAL_ERROR when questionnaire data is absent")
    void shouldSendTechnicalErrorWhenDataIsNotAnObject(CapturedOutput output) throws JMSException {
        // Given — data absent (optional in schema) → getData() returns null → fails instanceof ObjectNode in toInterrogation
        String body = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData)
                .replaceAll(",\\s*\"data\":\\s*\\{\\}", "");
        when(commandMessage.getBody(String.class)).thenReturn(body);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        JMSOutputMessage response = publisher.getResponseSent();
        assertThat(response.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
        assertThat(output).contains("interrogation data is malformed");
    }

    @Test
    @DisplayName("Should send TECHNICAL_ERROR when an unexpected runtime exception is thrown")
    void shouldSendTechnicalErrorOnUnexpectedException(CapturedOutput output) throws JMSException {
        // Given
        interrogationBatchFakeService.setShouldThrowRuntimeException(true);
        String msg = defaultBody.formatted(additionalFieldCommand, correlationId, replyTo, additionalFieldInterrogation, interrogationId, surveyUnitId, questionnaireId, questionnaireData);
        when(commandMessage.getBody(String.class)).thenReturn(msg);

        // When
        consumer.createInterrogation(commandMessage);

        // Then
        JMSOutputMessage response = publisher.getResponseSent();
        assertThat(response.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
        assertThat(publisher.getReplyQueueUsed()).isEqualTo(replyTo);
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(output).contains("Exception :");
    }
}
