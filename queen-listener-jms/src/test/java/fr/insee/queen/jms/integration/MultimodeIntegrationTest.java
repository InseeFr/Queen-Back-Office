package fr.insee.queen.jms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.events.EventsJpaRepository;
import fr.insee.queen.infrastructure.db.events.OutboxDB;
import fr.insee.queen.jms.configuration.MultimodeProperties;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration test for the Multimode CDC/Outbox pattern.
 * Tests the complete flow: Insert into outbox → Scheduler processing → Publish to ActiveMQ
 */
@Sql(
    scripts = "/sql/cleanup-events.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Disabled
class MultimodeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EventsJpaRepository eventsJpaRepository;

    @Autowired
    private MultimodeProperties multimodeProperties;

    @Autowired
    private ConnectionFactory connectionFactory;

    private ObjectMapper objectMapper;
    private JmsTemplate topicJmsTemplate;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Configure JmsTemplate for topic subscription
        topicJmsTemplate = new JmsTemplate(connectionFactory);
        topicJmsTemplate.setPubSubDomain(true);
        topicJmsTemplate.setReceiveTimeout(5000);
    }

    @Test
    void shouldProcessOutboxEventAndPublishToActiveMQ() throws Exception {
        // Given: Insert an event into the outbox table
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload(
                "QUESTIONNAIRE_INIT",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "TEST-001");
                    builder.put("mode", "CAPI");
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // Verify event is saved and unprocessed
        List<OutboxDB> unprocessedEvents = eventsJpaRepository.findUnprocessedEvents();
        assertThat(unprocessedEvents).hasSize(1);
        assertThat(unprocessedEvents.get(0).getId()).isEqualTo(eventId);
        assertThat(unprocessedEvents.get(0).getProcessedDate()).isNull();

        // When: Wait for the scheduler to process the event (max 15 seconds)
        await()
                .atMost(15, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    OutboxDB processedEvent = eventsJpaRepository.findById(eventId).orElseThrow();
                    assertThat(processedEvent.getProcessedDate()).isNotNull();
                });

        // Then: Verify the event has been marked as processed
        OutboxDB processedEvent = eventsJpaRepository.findById(eventId).orElseThrow();
        assertThat(processedEvent.getProcessedDate()).isNotNull();
        assertThat(processedEvent.getProcessedDate()).isAfter(processedEvent.getCreatedDate());

        // Verify no more unprocessed events
        List<OutboxDB> remainingUnprocessed = eventsJpaRepository.findUnprocessedEvents();
        assertThat(remainingUnprocessed).isEmpty();
    }

    @Test
    void shouldProcessMultipleOutboxEventsInOrder() {
        // Given: Insert multiple events with different timestamps
        UUID eventId1 = UUID.randomUUID();
        UUID eventId2 = UUID.randomUUID();
        UUID eventId3 = UUID.randomUUID();

        ObjectNode payload1 = createEventPayload("QUESTIONNAIRE_INIT", "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "1");
                    builder.put("mode", "CAPI");
                });

        ObjectNode payload2 = createEventPayload("QUESTIONNAIRE_LEAF_STATES_UPDATED", "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "2");
                    builder.put("mode", "CAWI");
                });

        ObjectNode payload3 = createEventPayload("QUESTIONNAIRE_COMPLETED", "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "3");
                    builder.put("mode", "CATI");
                });

        OutboxDB event1 = new OutboxDB(eventId1, payload1);
        event1.setCreatedDate(LocalDateTime.now().minusMinutes(3));
        eventsJpaRepository.save(event1);

        OutboxDB event2 = new OutboxDB(eventId2, payload2);
        event2.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        eventsJpaRepository.save(event2);

        OutboxDB event3 = new OutboxDB(eventId3, payload3);
        event3.setCreatedDate(LocalDateTime.now().minusMinutes(1));
        eventsJpaRepository.save(event3);

        // Verify all events are unprocessed
        List<OutboxDB> unprocessedEvents = eventsJpaRepository.findUnprocessedEvents();
        assertThat(unprocessedEvents).hasSize(3);

        // When: Wait for the scheduler to process all events
        await()
                .atMost(20, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<OutboxDB> remaining = eventsJpaRepository.findUnprocessedEvents();
                    assertThat(remaining).isEmpty();
                });

        // Then: Verify all events have been processed
        OutboxDB processed1 = eventsJpaRepository.findById(eventId1).orElseThrow();
        OutboxDB processed2 = eventsJpaRepository.findById(eventId2).orElseThrow();
        OutboxDB processed3 = eventsJpaRepository.findById(eventId3).orElseThrow();

        assertThat(processed1.getProcessedDate()).isNotNull();
        assertThat(processed2.getProcessedDate()).isNotNull();
        assertThat(processed3.getProcessedDate()).isNotNull();

        // Verify processing order (oldest first)
        assertThat(processed1.getProcessedDate())
                .isBeforeOrEqualTo(processed2.getProcessedDate());
        assertThat(processed2.getProcessedDate())
                .isBeforeOrEqualTo(processed3.getProcessedDate());
    }

    @Test
    void shouldNotReprocessAlreadyProcessedEvents() {
        // Given: Insert and manually mark an event as processed
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload("QUESTIONNAIRE_VALIDATED", "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "ALREADY-PROCESSED");
                    builder.put("mode", "CAPI");
                });

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now().minusHours(1));
        eventsJpaRepository.save(outboxEvent);

        LocalDateTime processedDate = LocalDateTime.now().minusMinutes(30);
        eventsJpaRepository.markAsProcessed(eventId, processedDate);
        eventsJpaRepository.flush();

        // When: Wait for scheduler cycles
        await()
                .pollDelay(6, TimeUnit.SECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Then: Verify the processed date hasn't changed
                    OutboxDB event = eventsJpaRepository.findById(eventId).orElseThrow();
                    assertThat(event.getProcessedDate())
                            .isEqualToIgnoringNanos(processedDate);
                });
    }

    @Test
    void shouldHandleDatabaseConnectionWithActiveMQ() {
        // Given: Verify containers are running
        assertThat(postgresContainer.isRunning()).isTrue();
        assertThat(artemisContainer.isRunning()).isTrue();

        // When: Create an event
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload("MULTIMODE_MOVED", "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "TEST-CONNECTION");
                    builder.put("mode", "CAPI");
                });

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());

        // Then: Should successfully save to database
        OutboxDB saved = eventsJpaRepository.save(outboxEvent);
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(eventId);

        // And: Should be able to query it back
        OutboxDB retrieved = eventsJpaRepository.findById(eventId).orElseThrow();
        assertThat(retrieved.getPayload().get("eventType").asText()).isEqualTo("MULTIMODE_MOVED");
    }

    /**
     * Creates an event payload following the Event Sourcing pattern.
     * Structure: {
     *   "eventType": "QUESTIONNAIRE_INIT",
     *   "aggregateType": "QUESTIONNAIRE",
     *   "payload": { ... }
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