package fr.insee.queen.jms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.jms.model.JMSOutputMessage;
import fr.insee.queen.jms.model.ResponseCode;
import fr.insee.queen.jms.service.stub.InterrogationBatchFakeService;
import fr.insee.queen.jms.service.stub.InterrogationCommandFakeService;
import fr.insee.queen.jms.service.stub.InterrogationFakePublisher;
import fr.insee.queen.jms.service.utils.PropertyValidator;
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

import java.util.UUID;

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

    private final String correlationId = "123456";
    private final UUID interrogationId = UUID.randomUUID();
    private final String questionnaireId = "questionnaire-id";
    private final UUID surveyUnitId = UUID.randomUUID();
//    private final String defaultBody = """
//        {
//            "replyTo": "queueResponse",
//            "correlationID": "%s",
//            "payload": {
//                "interrogationId": "%s",
//                "questionnaires": [
//                    {
//                        "questionnaireModelId": "%s"
//                    }
//                ],
//                "surveyUnitId": "%s"
//            }
//        }""";
    private final String defaultBody = """
        {
            "_id": {"$oid": "68daa3467d1bf0d2ab4b8a35"},
            "processInstanceID": "efa34060-177e-4a34-9383-926f18a92492",
            "inProgress": false,
            "payload": "{\\"partitionId\\": \\"e96d4234-c986-457a-aac5-11063025c215\\", \\"interrogationId\\": \\"0d3e6e43-b442-4add-bea1-d4735e28c843\\", \\"surveyUnitId\\": \\"3a8ac558-09a5-4135-9db3-c6965e45b31c\\", \\"originId\\": \\"890779946\\", \\"displayName\\": \\"890779946\\", \\"corporateName\\": \\"ACME 01 Industrie SAS\\", \\"unitLabel\\": \\"entreprise\\", \\"ape\\": \\"70.22Z\\", \\"legalCategory\\": \\"5710\\", \\"turnover\\": \\"40 627 281\\", \\"workforce\\": \\"221\\", \\"managementId\\": \\"OPALE-DEM-133\\", \\"ssech\\": \\"A1\\", \\"noGrap\\": \\"EEC-2025-096\\", \\"noLog\\": \\"14\\", \\"comment\\": \\"Données générées pour tests le 2025-09-16.\\", \\"cityCode\\": \\"69383\\", \\"questionnaires\\": [{\\"questionnaireModelId\\": \\"quest_model_entreprise_generique_2025\\", \\"questionningData\\": {\\"prefill\\": {\\"year\\": 2025, \\"period\\": \\"Q2\\", \\"siren\\": \\"890779946\\"}, \\"collection\\": {\\"mode\\": \\"PAPIER\\", \\"openingDate\\": \\"2025-04-17\\", \\"closingDate\\": \\"2025-06-24\\"}}}], \\"address\\": {\\"streetNumber\\": \\"7\\", \\"repetitionIndex\\": \\"TER\\", \\"streetType\\": \\"AVENUE\\", \\"streetName\\": \\"DE LA REPUBLIQUE\\", \\"addressSupplement\\": \\"Bâtiment B\\", \\"cityName\\": \\"LYON\\", \\"zipCode\\": \\"69003\\", \\"cedexCode\\": \\"\\", \\"cedexName\\": \\"\\", \\"specialDistribution\\": \\"\\", \\"countryCode\\": \\"FR\\", \\"countryName\\": \\"FRANCE\\"}, \\"addressComplement\\": {\\"building\\": \\"D\\", \\"floor\\": \\"2\\", \\"staircase\\": \\"\\", \\"door\\": \\"3C\\", \\"elevator\\": false, \\"cityPriorityDistrict\\": false}, \\"contacts\\": [{\\"contactId\\": \\"225831a9-16b8-4bda-9418-90ddc7e7521e\\", \\"gender\\": \\"M\\", \\"firstName\\": \\"Camille\\", \\"lastName\\": \\"Moreau\\", \\"dateOfBirth\\": \\"\\", \\"function\\": \\"DG adjoint(e)\\", \\"businessName\\": \\"ACME 01 Industrie SAS\\", \\"contactRank\\": 1, \\"phoneNumbers\\": [{\\"source\\": \\"ADMINISTRATIVE\\", \\"favorite\\": true, \\"number\\": \\"+33 1 37 53 23 21\\"}], \\"email\\": \\"camille.moreau@acme.example\\", \\"address\\": {\\"streetNumber\\": \\"98\\", \\"repetitionIndex\\": \\"\\", \\"streetType\\": \\"BOULEVARD\\", \\"streetName\\": \\"DE LA GARE\\", \\"addressSupplement\\": \\"Esc. 2, 3e étage\\", \\"cityName\\": \\"LYON\\", \\"zipCode\\": \\"69003\\", \\"cedexCode\\": \\"\\", \\"cedexName\\": \\"\\", \\"specialDistribution\\": \\"\\", \\"countryCode\\": \\"FR\\", \\"countryName\\": \\"FRANCE\\"}, \\"webConnectionId\\": \\"\\"}]}",
            "CampaignID": "GEN2025A00",
            "correlationID": "ae1e01ba-b334-45c8-b9b0-99d5d25a46ff",
            "questionnaireID": "quest_model_entreprise_generique_2025",
            "done": false,
            "dateCreation": { "$date": "2025-09-16T12:00:00Z" },
            "replyTo": "reply-queue-ue"
        }
        """;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        interrogationBatchFakeService = new InterrogationBatchFakeService();
        publisher = new InterrogationFakePublisher();
        consumer = new InterrogationQueueConsumer(mapper, publisher, interrogationBatchFakeService);
    }

    @Disabled
    @Test
    @DisplayName("Should create interrogation when message is valid")
    void ok() throws JMSException {
        // Given
        when(commandMessage.getBody(String.class)).thenReturn(defaultBody);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        // command is created
        Interrogation interrogationBatchUsed = interrogationBatchFakeService.getInterrogationBatchUsed();
        assertThat(interrogationBatchUsed.correlationId()).isEqualTo("ae1e01ba-b334-45c8-b9b0-99d5d25a46ff");
        assertThat(interrogationBatchUsed.questionnaireId()).isEqualTo(questionnaireId);
        assertThat(interrogationBatchUsed.id()).isEqualTo(interrogationId.toString());
        assertThat(interrogationBatchUsed.surveyUnitId()).isEqualTo(surveyUnitId.toString());
        assertThat(interrogationBatchUsed.data()).isEqualTo(JsonNodeFactory.instance.objectNode());
        assertThat(interrogationBatchUsed.personalization()).isEqualTo(JsonNodeFactory.instance.arrayNode());

        // response publisher is called
        JMSOutputMessage responseMessage = publisher.getResponseSent();
        String replyQueue = publisher.getReplyQueueUsed();
        assertThat(publisher.getCorrelationIdUsed()).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("queueResponse");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.CREATED.getCode());
        assertThat(responseMessage.message()).isEqualTo(ResponseCode.CREATED.name());
    }

    @Disabled
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

    @Disabled
    @Test
    @DisplayName("Should log error when no reply to in command message")
    void shouldLogErrorWhenNoReplyTo(CapturedOutput output) throws JMSException, JsonProcessingException {
        String message = """
        {
            "_id": {"$oid": "68daa3467d1bf0d2ab4b8a35"},
            "processInstanceID": "efa34060-177e-4a34-9383-926f18a92492",
            "inProgress": false,
            "payload": "{\\"partitionId\\": \\"e96d4234-c986-457a-aac5-11063025c215\\", \\"interrogationId\\": \\"0d3e6e43-b442-4add-bea1-d4735e28c843\\", \\"surveyUnitId\\": \\"3a8ac558-09a5-4135-9db3-c6965e45b31c\\", \\"originId\\": \\"890779946\\", \\"displayName\\": \\"890779946\\", \\"corporateName\\": \\"ACME 01 Industrie SAS\\", \\"unitLabel\\": \\"entreprise\\", \\"ape\\": \\"70.22Z\\", \\"legalCategory\\": \\"5710\\", \\"turnover\\": \\"40 627 281\\", \\"workforce\\": \\"221\\", \\"managementId\\": \\"OPALE-DEM-133\\", \\"ssech\\": \\"A1\\", \\"noGrap\\": \\"EEC-2025-096\\", \\"noLog\\": \\"14\\", \\"comment\\": \\"Données générées pour tests le 2025-09-16.\\", \\"cityCode\\": \\"69383\\", \\"questionnaires\\": [{\\"questionnaireModelId\\": \\"quest_model_entreprise_generique_2025\\", \\"questionningData\\": {\\"prefill\\": {\\"year\\": 2025, \\"period\\": \\"Q2\\", \\"siren\\": \\"890779946\\"}, \\"collection\\": {\\"mode\\": \\"PAPIER\\", \\"openingDate\\": \\"2025-04-17\\", \\"closingDate\\": \\"2025-06-24\\"}}}], \\"address\\": {\\"streetNumber\\": \\"7\\", \\"repetitionIndex\\": \\"TER\\", \\"streetType\\": \\"AVENUE\\", \\"streetName\\": \\"DE LA REPUBLIQUE\\", \\"addressSupplement\\": \\"Bâtiment B\\", \\"cityName\\": \\"LYON\\", \\"zipCode\\": \\"69003\\", \\"cedexCode\\": \\"\\", \\"cedexName\\": \\"\\", \\"specialDistribution\\": \\"\\", \\"countryCode\\": \\"FR\\", \\"countryName\\": \\"FRANCE\\"}, \\"addressComplement\\": {\\"building\\": \\"D\\", \\"floor\\": \\"2\\", \\"staircase\\": \\"\\", \\"door\\": \\"3C\\", \\"elevator\\": false, \\"cityPriorityDistrict\\": false}, \\"contacts\\": [{\\"contactId\\": \\"225831a9-16b8-4bda-9418-90ddc7e7521e\\", \\"gender\\": \\"M\\", \\"firstName\\": \\"Camille\\", \\"lastName\\": \\"Moreau\\", \\"dateOfBirth\\": \\"\\", \\"function\\": \\"DG adjoint(e)\\", \\"businessName\\": \\"ACME 01 Industrie SAS\\", \\"contactRank\\": 1, \\"phoneNumbers\\": [{\\"source\\": \\"ADMINISTRATIVE\\", \\"favorite\\": true, \\"number\\": \\"+33 1 37 53 23 21\\"}], \\"email\\": \\"camille.moreau@acme.example\\", \\"address\\": {\\"streetNumber\\": \\"98\\", \\"repetitionIndex\\": \\"\\", \\"streetType\\": \\"BOULEVARD\\", \\"streetName\\": \\"DE LA GARE\\", \\"addressSupplement\\": \\"Esc. 2, 3e étage\\", \\"cityName\\": \\"LYON\\", \\"zipCode\\": \\"69003\\", \\"cedexCode\\": \\"\\", \\"cedexName\\": \\"\\", \\"specialDistribution\\": \\"\\", \\"countryCode\\": \\"FR\\", \\"countryName\\": \\"FRANCE\\"}, \\"webConnectionId\\": \\"\\"}]}",
            "CampaignID": "GEN2025A00",
            "correlationID": "ae1e01ba-b334-45c8-b9b0-99d5d25a46ff",
            "questionnaireID": "quest_model_entreprise_generique_2025",
            "done": false,
            "dateCreation": { "$date": "2025-09-16T12:00:00Z" },
            "replyTo": "reply-queue-ue"
        }
        """;
        String noReplyToMessage = String.format(message, correlationId, interrogationId, questionnaireId, surveyUnitId);

        checkInvalidMessageError(noReplyToMessage, "replyTo", output);
    }

    private void checkInvalidMessageError(String invalidMessage, String invalidPropertyName, CapturedOutput output) throws JMSException {
        // Given
        when(commandMessage.getBody(String.class)).thenReturn(invalidMessage);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        String expectedLogMessage = String.format(PropertyValidator.PROPERTY_NOT_EMPTY, invalidPropertyName);
        assertThat(output).contains(expectedLogMessage);
    }

    @Test
    @DisplayName("Should log error when invalid json in command message")
    void shouldLogErrorWhenInvalidJsonMessageCommandMessage(CapturedOutput output) throws JMSException {
        // Given
        String messagesBroker = """
        {
            "_id": {"$oid": "68daa3467d1bf0d2ab4b8a35"},
            "processInstanceID-KO": "efa34060-177e-4a34-9383-926f18a92492",
            "inProgress": false,
            "payload": "{\\"partitionId\\": \\"e96d4234-c986-457a-aac5-11063025c215\\", \\"interrogationId\\": \\"0d3e6e43-b442-4add-bea1-d4735e28c843\\", \\"surveyUnitId\\": \\"3a8ac558-09a5-4135-9db3-c6965e45b31c\\", \\"originId\\": \\"890779946\\", \\"displayName\\": \\"890779946\\", \\"corporateName\\": \\"ACME 01 Industrie SAS\\", \\"unitLabel\\": \\"entreprise\\", \\"ape\\": \\"70.22Z\\", \\"legalCategory\\": \\"5710\\", \\"turnover\\": \\"40 627 281\\", \\"workforce\\": \\"221\\", \\"managementId\\": \\"OPALE-DEM-133\\", \\"ssech\\": \\"A1\\", \\"noGrap\\": \\"EEC-2025-096\\", \\"noLog\\": \\"14\\", \\"comment\\": \\"Données générées pour tests le 2025-09-16.\\", \\"cityCode\\": \\"69383\\", \\"questionnaires\\": [{\\"questionnaireModelId\\": \\"quest_model_entreprise_generique_2025\\", \\"questionningData\\": {\\"prefill\\": {\\"year\\": 2025, \\"period\\": \\"Q2\\", \\"siren\\": \\"890779946\\"}, \\"collection\\": {\\"mode\\": \\"PAPIER\\", \\"openingDate\\": \\"2025-04-17\\", \\"closingDate\\": \\"2025-06-24\\"}}}], \\"address\\": {\\"streetNumber\\": \\"7\\", \\"repetitionIndex\\": \\"TER\\", \\"streetType\\": \\"AVENUE\\", \\"streetName\\": \\"DE LA REPUBLIQUE\\", \\"addressSupplement\\": \\"Bâtiment B\\", \\"cityName\\": \\"LYON\\", \\"zipCode\\": \\"69003\\", \\"cedexCode\\": \\"\\", \\"cedexName\\": \\"\\", \\"specialDistribution\\": \\"\\", \\"countryCode\\": \\"FR\\", \\"countryName\\": \\"FRANCE\\"}, \\"addressComplement\\": {\\"building\\": \\"D\\", \\"floor\\": \\"2\\", \\"staircase\\": \\"\\", \\"door\\": \\"3C\\", \\"elevator\\": false, \\"cityPriorityDistrict\\": false}, \\"contacts\\": [{\\"contactId\\": \\"225831a9-16b8-4bda-9418-90ddc7e7521e\\", \\"gender\\": \\"M\\", \\"firstName\\": \\"Camille\\", \\"lastName\\": \\"Moreau\\", \\"dateOfBirth\\": \\"\\", \\"function\\": \\"DG adjoint(e)\\", \\"businessName\\": \\"ACME 01 Industrie SAS\\", \\"contactRank\\": 1, \\"phoneNumbers\\": [{\\"source\\": \\"ADMINISTRATIVE\\", \\"favorite\\": true, \\"number\\": \\"+33 1 37 53 23 21\\"}], \\"email\\": \\"camille.moreau@acme.example\\", \\"address\\": {\\"streetNumber\\": \\"98\\", \\"repetitionIndex\\": \\"\\", \\"streetType\\": \\"BOULEVARD\\", \\"streetName\\": \\"DE LA GARE\\", \\"addressSupplement\\": \\"Esc. 2, 3e étage\\", \\"cityName\\": \\"LYON\\", \\"zipCode\\": \\"69003\\", \\"cedexCode\\": \\"\\", \\"cedexName\\": \\"\\", \\"specialDistribution\\": \\"\\", \\"countryCode\\": \\"FR\\", \\"countryName\\": \\"FRANCE\\"}, \\"webConnectionId\\": \\"\\"}]}",
            "CampaignID": "GEN2025A00",
            "correlationID-KO": "ae1e01ba-b334-45c8-b9b0-99d5d25a46ff",
            "questionnaireID": "quest_model_entreprise_generique_2025",
            "done": false,
            "dateCreation": { "$date": "2025-09-16T12:00:00Z" },
            "replyTo": "reply-queue-ue"
        }
        """;

        when(commandMessage.getBody(String.class)).thenReturn(messagesBroker);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(output).containsAnyOf(
                "ERROR fr.insee.queen.jms.service.InterrogationQueueConsumer -- JsonSchemaValidator",
                "ERROR fr.insee.queen.jms.service.InterrogationQueueConsumer -- IOException"
        );
    }

    @Test
    @DisplayName("Should log error when invalid json in interrogation")
    void shouldLogErrorWhenInvalidJsonMessageInterrogation(CapturedOutput output) throws JMSException {
        // Given
        String messagesBroker = """
        {
            "_id": {"$oid": "68daa3467d1bf0d2ab4b8a35"},
            "processInstanceID": "efa34060-177e-4a34-9383-926f18a92492",
            "inProgress": false,
            "payload": "{\\"partitionId\\": \\"e96d4234-c986-457a-aac5-11063025c215\\", \\"interrogationId-KO\\": \\"0d3e6e43-b442-4add-bea1-d4735e28c843\\", \\"surveyUnitId-KO\\": \\"3a8ac558-09a5-4135-9db3-c6965e45b31c\\", \\"originId\\": \\"890779946\\", \\"displayName\\": \\"890779946\\", \\"corporateName\\": \\"ACME 01 Industrie SAS\\", \\"unitLabel\\": \\"entreprise\\", \\"ape\\": \\"70.22Z\\", \\"legalCategory\\": \\"5710\\", \\"turnover\\": \\"40 627 281\\", \\"workforce\\": \\"221\\", \\"managementId\\": \\"OPALE-DEM-133\\", \\"ssech\\": \\"A1\\", \\"noGrap\\": \\"EEC-2025-096\\", \\"noLog\\": \\"14\\", \\"comment\\": \\"Données générées pour tests le 2025-09-16.\\", \\"cityCode\\": \\"69383\\", \\"questionnaires\\": [{\\"questionnaireModelId\\": \\"quest_model_entreprise_generique_2025\\", \\"questionningData\\": {\\"prefill\\": {\\"year\\": 2025, \\"period\\": \\"Q2\\", \\"siren\\": \\"890779946\\"}, \\"collection\\": {\\"mode\\": \\"PAPIER\\", \\"openingDate\\": \\"2025-04-17\\", \\"closingDate\\": \\"2025-06-24\\"}}}], \\"address\\": {\\"streetNumber\\": \\"7\\", \\"repetitionIndex\\": \\"TER\\", \\"streetType\\": \\"AVENUE\\", \\"streetName\\": \\"DE LA REPUBLIQUE\\", \\"addressSupplement\\": \\"Bâtiment B\\", \\"cityName\\": \\"LYON\\", \\"zipCode\\": \\"69003\\", \\"cedexCode\\": \\"\\", \\"cedexName\\": \\"\\", \\"specialDistribution\\": \\"\\", \\"countryCode\\": \\"FR\\", \\"countryName\\": \\"FRANCE\\"}, \\"addressComplement\\": {\\"building\\": \\"D\\", \\"floor\\": \\"2\\", \\"staircase\\": \\"\\", \\"door\\": \\"3C\\", \\"elevator\\": false, \\"cityPriorityDistrict\\": false}, \\"contacts\\": [{\\"contactId\\": \\"225831a9-16b8-4bda-9418-90ddc7e7521e\\", \\"gender\\": \\"M\\", \\"firstName\\": \\"Camille\\", \\"lastName\\": \\"Moreau\\", \\"dateOfBirth\\": \\"\\", \\"function\\": \\"DG adjoint(e)\\", \\"businessName\\": \\"ACME 01 Industrie SAS\\", \\"contactRank\\": 1, \\"phoneNumbers\\": [{\\"source\\": \\"ADMINISTRATIVE\\", \\"favorite\\": true, \\"number\\": \\"+33 1 37 53 23 21\\"}], \\"email\\": \\"camille.moreau@acme.example\\", \\"address\\": {\\"streetNumber\\": \\"98\\", \\"repetitionIndex\\": \\"\\", \\"streetType\\": \\"BOULEVARD\\", \\"streetName\\": \\"DE LA GARE\\", \\"addressSupplement\\": \\"Esc. 2, 3e étage\\", \\"cityName\\": \\"LYON\\", \\"zipCode\\": \\"69003\\", \\"cedexCode\\": \\"\\", \\"cedexName\\": \\"\\", \\"specialDistribution\\": \\"\\", \\"countryCode\\": \\"FR\\", \\"countryName\\": \\"FRANCE\\"}, \\"webConnectionId-KO\\": \\"\\"}]}",
            "CampaignID": "GEN2025A00",
            "correlationID": "ae1e01ba-b334-45c8-b9b0-99d5d25a46ff",
            "questionnaireID": "quest_model_entreprise_generique_2025",
            "done": false,
            "dateCreation": { "$date": "2025-09-16T12:00:00Z" },
            "replyTo": "reply-queue-ue"
        }
        """;

        when(commandMessage.getBody(String.class)).thenReturn(messagesBroker);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(output).containsAnyOf(
                "ERROR fr.insee.queen.jms.service.InterrogationQueueConsumer -- JsonSchemaValidator",
                "ERROR fr.insee.queen.jms.service.InterrogationQueueConsumer -- IOException"
        );
    }

    @Test
    @DisplayName("Should log when valid json in command message and interrogation")
    void shouldLogWhenValidJsonMessage(CapturedOutput output) throws JMSException {
        // Given
        String messagesBroker = """
        {
            "_id": {"$oid": "68daa3467d1bf0d2ab4b8a35"},
            "processInstanceID": "efa34060-177e-4a34-9383-926f18a92492",
            "inProgress": false,
            "payload": "{\\"partitionId\\": \\"e96d4234-c986-457a-aac5-11063025c215\\", \\"interrogationId\\": \\"0d3e6e43-b442-4add-bea1-d4735e28c843\\", \\"surveyUnitId\\": \\"3a8ac558-09a5-4135-9db3-c6965e45b31c\\", \\"originId\\": \\"890779946\\", \\"displayName\\": \\"890779946\\", \\"corporateName\\": \\"ACME 01 Industrie SAS\\", \\"unitLabel\\": \\"entreprise\\", \\"ape\\": \\"70.22Z\\", \\"legalCategory\\": \\"5710\\", \\"turnover\\": \\"40 627 281\\", \\"workforce\\": \\"221\\", \\"managementId\\": \\"OPALE-DEM-133\\", \\"ssech\\": \\"A1\\", \\"noGrap\\": \\"EEC-2025-096\\", \\"noLog\\": \\"14\\", \\"comment\\": \\"Données générées pour tests le 2025-09-16.\\", \\"cityCode\\": \\"69383\\", \\"questionnaires\\": [{\\"questionnaireModelId\\": \\"quest_model_entreprise_generique_2025\\", \\"questionningData\\": {\\"prefill\\": {\\"year\\": 2025, \\"period\\": \\"Q2\\", \\"siren\\": \\"890779946\\"}, \\"collection\\": {\\"mode\\": \\"PAPIER\\", \\"openingDate\\": \\"2025-04-17\\", \\"closingDate\\": \\"2025-06-24\\"}}}], \\"address\\": {\\"streetNumber\\": \\"7\\", \\"repetitionIndex\\": \\"TER\\", \\"streetType\\": \\"AVENUE\\", \\"streetName\\": \\"DE LA REPUBLIQUE\\", \\"addressSupplement\\": \\"Bâtiment B\\", \\"cityName\\": \\"LYON\\", \\"zipCode\\": \\"69003\\", \\"cedexCode\\": \\"\\", \\"cedexName\\": \\"\\", \\"specialDistribution\\": \\"\\", \\"countryCode\\": \\"FR\\", \\"countryName\\": \\"FRANCE\\"}, \\"addressComplement\\": {\\"building\\": \\"D\\", \\"floor\\": \\"2\\", \\"staircase\\": \\"\\", \\"door\\": \\"3C\\", \\"elevator\\": false, \\"cityPriorityDistrict\\": false}, \\"contacts\\": [{\\"contactId\\": \\"225831a9-16b8-4bda-9418-90ddc7e7521e\\", \\"gender\\": \\"M\\", \\"firstName\\": \\"Camille\\", \\"lastName\\": \\"Moreau\\", \\"dateOfBirth\\": \\"\\", \\"function\\": \\"DG adjoint(e)\\", \\"businessName\\": \\"ACME 01 Industrie SAS\\", \\"contactRank\\": 1, \\"phoneNumbers\\": [{\\"source\\": \\"ADMINISTRATIVE\\", \\"favorite\\": true, \\"number\\": \\"+33 1 37 53 23 21\\"}], \\"email\\": \\"camille.moreau@acme.example\\", \\"address\\": {\\"streetNumber\\": \\"98\\", \\"repetitionIndex\\": \\"\\", \\"streetType\\": \\"BOULEVARD\\", \\"streetName\\": \\"DE LA GARE\\", \\"addressSupplement\\": \\"Esc. 2, 3e étage\\", \\"cityName\\": \\"LYON\\", \\"zipCode\\": \\"69003\\", \\"cedexCode\\": \\"\\", \\"cedexName\\": \\"\\", \\"specialDistribution\\": \\"\\", \\"countryCode\\": \\"FR\\", \\"countryName\\": \\"FRANCE\\"}, \\"webConnectionId\\": \\"\\"}]}",
            "CampaignID": "GEN2025A00",
            "correlationID": "ae1e01ba-b334-45c8-b9b0-99d5d25a46ff",
            "questionnaireID": "quest_model_entreprise_generique_2025",
            "done": false,
            "dateCreation": { "$date": "2025-09-16T12:00:00Z" },
            "replyTo": "reply-queue-ue"
        }
        """;

        when(commandMessage.getBody(String.class)).thenReturn(messagesBroker);

        // When
        consumer.createInterrogation(commandMessage, session);

        // Then
        assertThat(output).contains(
                "JSON conforme au schéma :"
        );
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
    @DisplayName("Should send business error when questionnaire id is invalid")
    void shouldLogErrorWhenInvalidQuestionnaireId() throws JMSException {
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
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(correlationPublisherId).isEqualTo(correlationId);
        assertThat(replyQueue).isEqualTo("12345");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.BUSINESS_ERROR.getCode());
        String expectedMessage = String.format(PropertyValidator.PROPERTY_NOT_EMPTY, "questionnaireModelId");
        assertThat(responseMessage.message()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Should publisher send business error when survey unit id is invalid")
    void shouldLogErrorWhenInvalidSurveyUnitId() throws JMSException {
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
        assertThat(interrogationBatchFakeService.getInterrogationBatchUsed()).isNull();
        assertThat(correlationPublisherId).isEqualTo("123456");
        assertThat(replyQueue).isEqualTo("12345");
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
    }

    @Test
    @DisplayName("Should publisher send business error when interrogation command exception")
    void shouldSendBusinessErrorWhenSurveyUnitCommandException() throws JMSException {
        // Given
        interrogationBatchFakeService.setShouldThrowInterrogationBatchException(true);
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
        assertThat(responseMessage.code()).isEqualTo(ResponseCode.TECHNICAL_ERROR.getCode());
    }

    @Disabled
    @Test
    @DisplayName("Should publisher send business error when interrogation command exception")
    void shouldSendTechnicalErrorWhenRuntimeException() throws JMSException {
        // Given
        interrogationBatchFakeService.setShouldThrowRuntimeException(true);
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

