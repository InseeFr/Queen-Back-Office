package fr.insee.queen.jms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.events.EventsJpaRepository;
import fr.insee.queen.infrastructure.db.events.OutboxDB;
import fr.insee.queen.jms.configuration.MultimodeProperties;
import jakarta.jms.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration test verifying that messages are correctly published to ActiveMQ topics
 * and can be consumed by subscribers.
 */
class ActiveMQPublishingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EventsJpaRepository eventsJpaRepository;

    @Autowired
    private MultimodeProperties multimodeProperties;

    @Autowired
    private ConnectionFactory connectionFactory;

    private ObjectMapper objectMapper;
    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    private List<String> receivedMessages;
    private CountDownLatch messageLatch;

    @BeforeEach
    void setUp() throws JMSException {
        objectMapper = new ObjectMapper();
        eventsJpaRepository.deleteAll();
        receivedMessages = new ArrayList<>();

        // Setup JMS consumer for the multimode topic
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        String topicName = multimodeProperties.getTopic();
        Topic topic = session.createTopic(topicName);
        consumer = session.createConsumer(topic);

        // Message listener to capture published messages
        messageLatch = new CountDownLatch(1);
        consumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage textMessage) {
                    String text = textMessage.getText();
                    receivedMessages.add(text);
                    messageLatch.countDown();
                }
            } catch (JMSException e) {
                throw new RuntimeException("Error processing message", e);
            }
        });

        connection.start();
    }

    @AfterEach
    void tearDown() throws JMSException {
        if (consumer != null) {
            consumer.close();
        }
        if (session != null) {
            session.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void shouldPublishMessageToActiveMQTopic() throws Exception {
        // Given: Insert an event into the outbox
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload(
                "QUESTIONNAIRE_INIT",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "INT-001");
                    builder.put("mode", "CAPI");
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // When: Wait for the scheduler to process and publish
        boolean messageReceived = messageLatch.await(20, TimeUnit.SECONDS);

        // Then: Verify message was received
        assertThat(messageReceived).isTrue();
        assertThat(receivedMessages).hasSize(1);

        // Verify message content
        String receivedMessage = receivedMessages.get(0);
        ObjectNode receivedPayload = objectMapper.readValue(receivedMessage, ObjectNode.class);

        assertThat(receivedPayload.get("eventType").asText()).isEqualTo("QUESTIONNAIRE_INIT");
        assertThat(receivedPayload.get("aggregateType").asText()).isEqualTo("QUESTIONNAIRE");
        assertThat(receivedPayload.get("payload").get("interrogationId").asText()).isEqualTo("INT-001");
        assertThat(receivedPayload.get("payload").get("mode").asText()).isEqualTo("CAPI");

        // Verify event is marked as processed in database
        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    OutboxDB processed = eventsJpaRepository.findById(eventId).orElseThrow();
                    assertThat(processed.getProcessedDate()).isNotNull();
                });
    }

    @Test
    void shouldPublishMultipleMessagesToTopic() throws Exception {
        // Given: Insert multiple events
        int messageCount = 3;
        messageLatch = new CountDownLatch(messageCount);

        List<String> interrogationIds = new ArrayList<>();
        String[] modes = {"CAPI", "CAWI", "CATI"};

        for (int i = 0; i < messageCount; i++) {
            UUID eventId = UUID.randomUUID();
            String interrogationId = "INT-" + (i + 1);
            interrogationIds.add(interrogationId);

            int finalI = i;
            ObjectNode payload = createEventPayload(
                    "QUESTIONNAIRE_LEAF_STATES_UPDATED",
                    "QUESTIONNAIRE",
                    builder -> {
                        builder.put("interrogationId", interrogationId);
                        builder.put("mode", modes[finalI]);
                    }
            );

            OutboxDB outboxEvent = new OutboxDB(eventId, payload);
            outboxEvent.setCreatedDate(LocalDateTime.now().minusSeconds(messageCount - i));
            eventsJpaRepository.save(outboxEvent);
        }

        // When: Wait for all messages to be received
        boolean allMessagesReceived = messageLatch.await(30, TimeUnit.SECONDS);

        // Then: Verify all messages were received
        assertThat(allMessagesReceived).isTrue();
        assertThat(receivedMessages).hasSize(messageCount);

        // Verify all interrogation IDs are present in received messages
        for (String interrogationId : interrogationIds) {
            boolean found = receivedMessages.stream()
                    .anyMatch(msg -> msg.contains(interrogationId));
            assertThat(found).isTrue();
        }

        // Verify all events are marked as processed
        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<OutboxDB> unprocessed = eventsJpaRepository.findUnprocessedEvents();
                    assertThat(unprocessed).isEmpty();
                });
    }

    @Test
    void shouldPublishEventWithLeafStates() throws Exception {
        // Given: Create an event with leaf states
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload(
                "QUESTIONNAIRE_LEAF_STATES_UPDATED",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "LEAF-001");
                    builder.put("mode", "CAWI");
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // When: Wait for message to be published
        boolean messageReceived = messageLatch.await(20, TimeUnit.SECONDS);

        // Then: Verify payload is correctly published
        assertThat(messageReceived).isTrue();
        assertThat(receivedMessages).hasSize(1);

        ObjectNode receivedPayload = objectMapper.readValue(receivedMessages.get(0), ObjectNode.class);
        assertThat(receivedPayload.get("eventType").asText()).isEqualTo("QUESTIONNAIRE_LEAF_STATES_UPDATED");
        assertThat(receivedPayload.get("aggregateType").asText()).isEqualTo("QUESTIONNAIRE");

        ObjectNode receivedInnerPayload = (ObjectNode) receivedPayload.get("payload");
        assertThat(receivedInnerPayload.get("interrogationId").asText()).isEqualTo("LEAF-001");
        assertThat(receivedInnerPayload.get("mode").asText()).isEqualTo("CAWI");
    }

    @Test
    void shouldVerifyActiveMQTopicConfiguration() {
        // Given/When: Verify multimode configuration
        String topicName = multimodeProperties.getTopic();

        // Then: Verify topic is correctly configured
        assertThat(topicName).isNotNull();
        assertThat(topicName).isEqualTo("multimode_events_test");
        assertThat(multimodeProperties.isEnabled()).isTrue();
        assertThat(multimodeProperties.getScheduler().getInterval()).isEqualTo(5000);
    }

    /**
     * Creates an event payload following the Event Sourcing pattern.
     * Structure: {
     *   "eventType": "QUESTIONNAIRE_INIT",
     *   "aggregateType": "QUESTIONNAIRE",
     *   "payload": {
     *     "interrogationId": "1",
     *     "mode": "CAPI"
     *   }
     * }
     */
    private ObjectNode createEventPayload(String eventType, String aggregateType, PayloadBuilder payloadBuilder) {
        ObjectNode event = objectMapper.createObjectNode();
        event.put("eventType", eventType);
        event.put("aggregateType", aggregateType);

        ObjectNode payloadNode = objectMapper.createObjectNode();
        payloadBuilder.build(payloadNode);

        event.set("payload", payloadNode);
        return event;
    }

    @FunctionalInterface
    private interface PayloadBuilder {
        void build(ObjectNode builder);
    }
}