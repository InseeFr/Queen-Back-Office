package fr.insee.queen.jms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import fr.insee.queen.infrastructure.db.events.EventsJpaRepository;
import fr.insee.queen.infrastructure.db.events.InboxDB;
import fr.insee.queen.infrastructure.db.events.InboxJpaRepository;
import fr.insee.queen.infrastructure.db.events.OutboxDB;
import fr.insee.queen.infrastructure.jms.configuration.MultimodeProperties;
import jakarta.jms.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

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
@Sql(
    scripts = "/sql/setup-multimode-moved-tests.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
class ActiveMQPublishingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private EventsJpaRepository eventsJpaRepository;

    @Autowired
    private InboxJpaRepository inboxJpaRepository;

    @Autowired
    private MultimodeProperties multimodeProperties;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private StateDataService stateDataService;

    private ObjectMapper objectMapper;
    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    private List<String> receivedMessages;
    private CountDownLatch messageLatch;

    @BeforeEach
    void setUp() throws JMSException {
        objectMapper = new ObjectMapper();
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
        String receivedMessage = receivedMessages.getFirst();
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

        ObjectNode receivedPayload = objectMapper.readValue(receivedMessages.getFirst(), ObjectNode.class);
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
        assertThat(multimodeProperties.getPublisher().isEnabled()).isTrue();
        assertThat(multimodeProperties.getPublisher().getScheduler().getInterval()).isEqualTo(5000);
        assertThat(multimodeProperties.getSubscriber().isEnabled()).isTrue();
    }

    @Test
    void shouldSubscribeToTopicAndStoreInInbox() throws Exception {
        // Given: Insert an event into the outbox
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload(
                "QUESTIONNAIRE_INIT",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "SUB-001");
                    builder.put("mode", "CAWI");
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // When: Wait for the scheduler to publish and subscriber to process
        await()
                .atMost(25, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Then: Verify event was stored in inbox with correlationId
                    var inboxRecord = inboxJpaRepository.findById(eventId);
                    assertThat(inboxRecord).isPresent();
                });

        // Verify inbox record details
        var inboxRecord = inboxJpaRepository.findById(eventId).orElseThrow();
        assertThat(inboxRecord.getId()).isEqualTo(eventId); // correlationId is used as inbox ID
        assertThat(inboxRecord.getPayload()).isNotNull();
        assertThat(inboxRecord.getCreatedDate()).isNotNull();

        // Verify payload content
        ObjectNode inboxPayload = inboxRecord.getPayload();
        assertThat(inboxPayload.get("eventType").asText()).isEqualTo("QUESTIONNAIRE_INIT");
        assertThat(inboxPayload.get("aggregateType").asText()).isEqualTo("QUESTIONNAIRE");
        assertThat(inboxPayload.get("payload").get("interrogationId").asText()).isEqualTo("SUB-001");
        assertThat(inboxPayload.get("payload").get("mode").asText()).isEqualTo("CAWI");
        assertThat(inboxPayload.get("correlationId").asText()).isEqualTo(eventId.toString());

        // Verify outbox event is also marked as processed
        OutboxDB processed = eventsJpaRepository.findById(eventId).orElseThrow();
        assertThat(processed.getProcessedDate()).isNotNull();
    }

    @Test
    void shouldStoreMultipleEventsInInbox() {
        // Given: Insert multiple events
        int eventCount = 3;
        List<UUID> eventIds = new ArrayList<>();

        for (int i = 0; i < eventCount; i++) {
            UUID eventId = UUID.randomUUID();
            eventIds.add(eventId);

            int finalI = i;
            ObjectNode payload = createEventPayload(
                    "QUESTIONNAIRE_INIT",
                    "QUESTIONNAIRE",
                    builder -> {
                        builder.put("interrogationId", "MULTI-" + (finalI + 1));
                        builder.put("mode", "CAPI");
                    }
            );

            OutboxDB outboxEvent = new OutboxDB(eventId, payload);
            outboxEvent.setCreatedDate(LocalDateTime.now().minusSeconds(eventCount - i));
            eventsJpaRepository.save(outboxEvent);
        }

        // When: Wait for all events to be processed
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Then: Verify all events are in inbox
                    List<InboxDB> allInbox =
                        inboxJpaRepository.findAllOrderByCreatedDate();
                    assertThat(allInbox).hasSizeGreaterThanOrEqualTo(eventCount);
                });

        // Verify all event IDs are present in inbox
        for (UUID eventId : eventIds) {
            var inboxRecord = inboxJpaRepository.findById(eventId);
            assertThat(inboxRecord).isPresent();
            assertThat(inboxRecord.get().getId()).isEqualTo(eventId);
        }
    }

    @Test
    void shouldIgnoreDuplicateMessagesInInbox() {
        // Given: Create a manual inbox entry first
        UUID correlationId = UUID.randomUUID();
        ObjectNode existingPayload = createEventPayload(
                "QUESTIONNAIRE_INIT",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "DUP-001");
                    builder.put("mode", "CAWI");
                }
        );

        InboxDB existingInbox = new InboxDB(correlationId, existingPayload);
        inboxJpaRepository.save(existingInbox);

        // Get initial count
        long initialCount = inboxJpaRepository.count();

        // When: Publish the same event to outbox (which will trigger publishing to topic)
        ObjectNode payload = createEventPayload(
                "QUESTIONNAIRE_INIT",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", "DUP-001");
                    builder.put("mode", "CAWI");
                }
        );

        OutboxDB outboxEvent = new OutboxDB(correlationId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // Then: Wait a bit to ensure subscriber had time to process
        await()
                .pollDelay(5, TimeUnit.SECONDS)
                .atMost(20, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Verify the outbox event is marked as processed
                    OutboxDB processed = eventsJpaRepository.findById(correlationId).orElseThrow();
                    assertThat(processed.getProcessedDate()).isNotNull();
                });

        // Verify inbox count hasn't increased (duplicate was ignored)
        long finalCount = inboxJpaRepository.count();
        assertThat(finalCount).isEqualTo(initialCount);

        // Verify only one record exists with this correlationId
        var inboxRecord = inboxJpaRepository.findById(correlationId);
        assertThat(inboxRecord).isPresent();
        assertThat(inboxRecord.get().getPayload().get("payload").get("interrogationId").asText())
                .isEqualTo("DUP-001");
    }

    /**
     * Provides test data for state update events with existing state.
     * Format: eventType, expectedStateDataType, mode
     */
    private static Stream<Arguments> provideStateUpdateEventData() {
        return Stream.of(
                Arguments.of("MULTIMODE_MOVED", StateDataType.IS_MOVED, "CAWI"),
                Arguments.of("QUESTIONNAIRE_COMPLETED", StateDataType.COMPLETED, "CAWI"),
                Arguments.of("QUESTIONNAIRE_VALIDATED", StateDataType.VALIDATED, "CAPI")
        );
    }

    /**
     * Provides test data for state update events without existing state.
     * Format: eventType, expectedStateDataType, mode
     */
    private static Stream<Arguments> provideStateCreationEventData() {
        return Stream.of(
                Arguments.of("MULTIMODE_MOVED", StateDataType.IS_MOVED, "CAPI"),
                Arguments.of("QUESTIONNAIRE_COMPLETED", StateDataType.COMPLETED, "CAWI"),
                Arguments.of("QUESTIONNAIRE_VALIDATED", StateDataType.VALIDATED, "CAPI")
        );
    }

    @ParameterizedTest(name = "{0} should update state to {1}")
    @MethodSource("provideStateUpdateEventData")
    void shouldProcessStateUpdateEventAndUpdateStateData(String eventType, StateDataType expectedStateType, String mode) throws Exception {
        // Given: Interrogation and initial state data are created by SQL script
        String interrogationId = "MOVED-001";

        // Verify initial state from SQL script
        var initialState = stateDataService.findStateData(interrogationId);
        assertThat(initialState).isPresent();
        assertThat(initialState.get().state()).isEqualTo(StateDataType.INIT);

        StateData verifiedInitialStateData = initialState.get();

        // Create and publish an event
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload(
                eventType,
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", interrogationId);
                    builder.put("mode", mode);
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // When: Wait for the event to be processed through the complete flow
        await()
                .atMost(25, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Verify event was stored in inbox
                    var inboxRecord = inboxJpaRepository.findById(eventId);
                    assertThat(inboxRecord).isPresent();

                    // Verify StateData was updated to expected state
                    var updatedState = stateDataService.findStateData(interrogationId);
                    assertThat(updatedState).isPresent();
                    assertThat(updatedState.get().state()).isEqualTo(expectedStateType);
                });

        // Then: Verify final state
        var finalState = stateDataService.findStateData(interrogationId);
        assertThat(finalState).isPresent();
        assertThat(finalState.get().state()).isEqualTo(expectedStateType);
        assertThat(finalState.get().currentPage()).isEqualTo("1");
        assertThat(finalState.get().date()).isGreaterThan(verifiedInitialStateData.date());

        // Verify inbox record
        var inboxRecord = inboxJpaRepository.findById(eventId).orElseThrow();
        assertThat(inboxRecord.getPayload().get("eventType").asText()).isEqualTo(eventType);
        assertThat(inboxRecord.getPayload().get("payload").get("interrogationId").asText()).isEqualTo(interrogationId);

        // Verify outbox event is marked as processed
        OutboxDB processed = eventsJpaRepository.findById(eventId).orElseThrow();
        assertThat(processed.getProcessedDate()).isNotNull();
    }

    @ParameterizedTest(name = "{0} should create state with {1}")
    @MethodSource("provideStateCreationEventData")
    void shouldProcessStateUpdateEventWhenNoExistingState(String eventType, StateDataType expectedStateType, String mode) {
        // Given: Interrogation created by SQL script without state data
        String interrogationId = "MOVED-NEW-001";

        // Verify no initial state exists
        var initialState = stateDataService.findStateData(interrogationId);
        assertThat(initialState).isEmpty();

        // Create and publish an event
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload(
                eventType,
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", interrogationId);
                    builder.put("mode", mode);
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // When: Wait for the event to be processed
        await()
                .atMost(25, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Verify StateData was created with expected state
                    var createdState = stateDataService.findStateData(interrogationId);
                    assertThat(createdState).isPresent();
                    assertThat(createdState.get().state()).isEqualTo(expectedStateType);
                });

        // Then: Verify final state
        var finalState = stateDataService.findStateData(interrogationId);
        assertThat(finalState).isPresent();
        assertThat(finalState.get().state()).isEqualTo(expectedStateType);
        assertThat(finalState.get().currentPage()).isEqualTo("1");
        assertThat(finalState.get().date()).isGreaterThan(0);

        // Verify inbox record
        var inboxRecord = inboxJpaRepository.findById(eventId);
        assertThat(inboxRecord).isPresent();
        assertThat(inboxRecord.get().getPayload().get("eventType").asText()).isEqualTo(eventType);
    }

    @Test
    void shouldProcessQuestionnaireInitEventAndRefreshDate() throws Exception {
        // Given: Interrogation with existing state data
        String interrogationId = "MOVED-001";

        // Verify initial state from SQL script
        var initialState = stateDataService.findStateData(interrogationId);
        assertThat(initialState).isPresent();
        assertThat(initialState.get().state()).isEqualTo(StateDataType.INIT);

        StateData verifiedInitialStateData = initialState.get();
        long initialDate = verifiedInitialStateData.date();

        // Create and publish a QUESTIONNAIRE_INIT event
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload(
                "QUESTIONNAIRE_INIT",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", interrogationId);
                    builder.put("mode", "CAWI");
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // When: Wait for the event to be processed
        await()
                .atMost(25, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Verify event was stored in inbox
                    var inboxRecord = inboxJpaRepository.findById(eventId);
                    assertThat(inboxRecord).isPresent();

                    // Verify StateData date was updated
                    var updatedState = stateDataService.findStateData(interrogationId);
                    assertThat(updatedState).isPresent();
                    assertThat(updatedState.get().date()).isGreaterThan(initialDate);
                });

        // Then: Verify final state
        var finalState = stateDataService.findStateData(interrogationId);
        assertThat(finalState).isPresent();
        assertThat(finalState.get().state()).isEqualTo(StateDataType.INIT); // State preserved
        assertThat(finalState.get().currentPage()).isEqualTo("1"); // Page preserved
        assertThat(finalState.get().date()).isGreaterThan(initialDate); // Date updated

        // Verify inbox record
        var inboxRecord = inboxJpaRepository.findById(eventId).orElseThrow();
        assertThat(inboxRecord.getPayload().get("eventType").asText()).isEqualTo("QUESTIONNAIRE_INIT");
        assertThat(inboxRecord.getPayload().get("payload").get("interrogationId").asText()).isEqualTo(interrogationId);

        // Verify outbox event is marked as processed
        OutboxDB processed = eventsJpaRepository.findById(eventId).orElseThrow();
        assertThat(processed.getProcessedDate()).isNotNull();
    }

    @Test
    void shouldProcessQuestionnaireInitEventWhenNoExistingState() {
        // Given: Interrogation created by SQL script without state data
        String interrogationId = "MOVED-NEW-001";

        // Verify no initial state exists
        var initialState = stateDataService.findStateData(interrogationId);
        assertThat(initialState).isEmpty();

        // Create and publish a QUESTIONNAIRE_INIT event
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload(
                "QUESTIONNAIRE_INIT",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", interrogationId);
                    builder.put("mode", "CAWI");
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // When: Wait for the event to be processed
        await()
                .atMost(25, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Verify StateData was created with INIT state
                    var createdState = stateDataService.findStateData(interrogationId);
                    assertThat(createdState).isPresent();
                    assertThat(createdState.get().state()).isEqualTo(StateDataType.INIT);
                });

        // Then: Verify final state
        var finalState = stateDataService.findStateData(interrogationId);
        assertThat(finalState).isPresent();
        assertThat(finalState.get().state()).isEqualTo(StateDataType.INIT);
        assertThat(finalState.get().currentPage()).isEqualTo("1");
        assertThat(finalState.get().date()).isGreaterThan(0);

        // Verify inbox record
        var inboxRecord = inboxJpaRepository.findById(eventId);
        assertThat(inboxRecord).isPresent();
        assertThat(inboxRecord.get().getPayload().get("eventType").asText()).isEqualTo("QUESTIONNAIRE_INIT");
    }

    @Test
    void shouldProcessQuestionnaireLeafStatesUpdatedEventAndPersistLeafStates() {
        // Given: Interrogation with existing state data (created by SQL script)
        String interrogationId = "MOVED-001";

        // Verify initial state from SQL script
        var initialState = stateDataService.findStateData(interrogationId);
        assertThat(initialState).isPresent();
        assertThat(initialState.get().state()).isEqualTo(StateDataType.INIT);
        assertThat(initialState.get().leafStates()).isEmpty();

        long initialDate = initialState.get().date();

        // Create and publish a QUESTIONNAIRE_LEAF_STATES_UPDATED event with leafStates
        UUID eventId = UUID.randomUUID();
        long leafStateDate1 = System.currentTimeMillis() - 10000;
        long leafStateDate2 = System.currentTimeMillis() - 5000;

        ObjectNode payload = createEventPayload(
                "QUESTIONNAIRE_LEAF_STATES_UPDATED",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", interrogationId);
                    builder.put("mode", "CAWI");

                    // Add leafStates array
                    var leafStatesArray = objectMapper.createArrayNode();

                    var leafState1 = objectMapper.createObjectNode();
                    leafState1.put("state", "INIT");
                    leafState1.put("date", java.time.Instant.ofEpochMilli(leafStateDate1).toString());
                    leafStatesArray.add(leafState1);

                    var leafState2 = objectMapper.createObjectNode();
                    leafState2.put("state", "COMPLETED");
                    leafState2.put("date", java.time.Instant.ofEpochMilli(leafStateDate2).toString());
                    leafStatesArray.add(leafState2);

                    builder.set("leafStates", leafStatesArray);
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // When: Wait for the event to be processed
        await()
                .atMost(25, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Verify event was stored in inbox
                    var inboxRecord = inboxJpaRepository.findById(eventId);
                    assertThat(inboxRecord).isPresent();

                    // Verify StateData was updated with leafStates
                    var updatedState = stateDataService.findStateData(interrogationId);
                    assertThat(updatedState).isPresent();
                    assertThat(updatedState.get().leafStates()).hasSize(2);
                });
    
            // Then: Verify final state
            var finalState = stateDataService.findStateData(interrogationId);
            assertThat(finalState).isPresent();

            // State type should be preserved (INIT)
            assertThat(finalState.get().state()).isEqualTo(StateDataType.INIT);

        // CurrentPage should be preserved
        assertThat(finalState.get().currentPage()).isEqualTo("1");

        // Date should be updated
        assertThat(finalState.get().date()).isGreaterThan(initialDate);

        // LeafStates should be persisted
        var leafStates = finalState.get().leafStates();
        assertThat(leafStates).hasSize(2);

        assertThat(leafStates.get(0).state()).isEqualTo("INIT");
        assertThat(leafStates.get(0).date()).isEqualTo(leafStateDate1);

        assertThat(leafStates.get(1).state()).isEqualTo("COMPLETED");
        assertThat(leafStates.get(1).date()).isEqualTo(leafStateDate2);

        // Verify inbox record
        var finalInboxRecord = inboxJpaRepository.findById(eventId).orElseThrow();
        assertThat(finalInboxRecord.getPayload().get("eventType").asText()).isEqualTo("QUESTIONNAIRE_LEAF_STATES_UPDATED");
        assertThat(finalInboxRecord.getPayload().get("payload").get("interrogationId").asText()).isEqualTo(interrogationId);

        // Verify outbox event is marked as processed
        OutboxDB processed = eventsJpaRepository.findById(eventId).orElseThrow();
        assertThat(processed.getProcessedDate()).isNotNull();
    }

    @Test
    void shouldReplaceExistingLeafStatesWhenNewEventReceived() {
        // Given: Interrogation with existing state data (created by SQL script)
        String interrogationId = "MOVED-001";

        // First, send an event with initial leafStates
        UUID firstEventId = UUID.randomUUID();
        long firstLeafStateDate = System.currentTimeMillis() - 20000;

        ObjectNode firstPayload = createEventPayload(
                "QUESTIONNAIRE_LEAF_STATES_UPDATED",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", interrogationId);
                    var leafStatesArray = objectMapper.createArrayNode();
                    var leafState = objectMapper.createObjectNode();
                    leafState.put("state", "INIT");
                    leafState.put("date", java.time.Instant.ofEpochMilli(firstLeafStateDate).toString());
                    leafStatesArray.add(leafState);
                    builder.set("leafStates", leafStatesArray);
                }
        );

        OutboxDB firstOutboxEvent = new OutboxDB(firstEventId, firstPayload);
        firstOutboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(firstOutboxEvent);

        // Wait for first event to be processed
        await()
                .atMost(25, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var state = stateDataService.findStateData(interrogationId);
                    assertThat(state).isPresent();
                    assertThat(state.get().leafStates()).hasSize(1);
                });

        // When: Send a second event with different leafStates
        UUID secondEventId = UUID.randomUUID();
        long secondLeafStateDate1 = System.currentTimeMillis() - 5000;
        long secondLeafStateDate2 = System.currentTimeMillis() - 2000;
        long secondLeafStateDate3 = System.currentTimeMillis();

        ObjectNode secondPayload = createEventPayload(
                "QUESTIONNAIRE_LEAF_STATES_UPDATED",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", interrogationId);
                    var leafStatesArray = objectMapper.createArrayNode();

                    var leafState1 = objectMapper.createObjectNode();
                    leafState1.put("state", "INIT");
                    leafState1.put("date", java.time.Instant.ofEpochMilli(secondLeafStateDate1).toString());
                    leafStatesArray.add(leafState1);

                    var leafState2 = objectMapper.createObjectNode();
                    leafState2.put("state", "COMPLETED");
                    leafState2.put("date", java.time.Instant.ofEpochMilli(secondLeafStateDate2).toString());
                    leafStatesArray.add(leafState2);

                    var leafState3 = objectMapper.createObjectNode();
                    leafState3.put("state", "COMPLETED");
                    leafState3.put("date", java.time.Instant.ofEpochMilli(secondLeafStateDate3).toString());
                    leafStatesArray.add(leafState3);

                    builder.set("leafStates", leafStatesArray);
                }
        );

        OutboxDB secondOutboxEvent = new OutboxDB(secondEventId, secondPayload);
        secondOutboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(secondOutboxEvent);

        // Then: Wait for second event to be fully processed (inbox + consumer)
        await()
                .atMost(30, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Verify second event is in inbox
                    var inboxRecord = inboxJpaRepository.findById(secondEventId);
                    assertThat(inboxRecord).isPresent();

                    // Verify leafStates are replaced (not appended)
                    var state = stateDataService.findStateData(interrogationId);
                    assertThat(state).isPresent();
                    // Should have 3 leafStates now (replaced, not appended)
                    assertThat(state.get().leafStates()).hasSize(3);
                });

        var finalState = stateDataService.findStateData(interrogationId);
        assertThat(finalState).isPresent();

        var leafStates = finalState.get().leafStates();
        assertThat(leafStates).hasSize(3);
        assertThat(leafStates.get(0).state()).isEqualTo("INIT");
        assertThat(leafStates.get(1).state()).isEqualTo("COMPLETED");
        assertThat(leafStates.get(2).state()).isEqualTo("COMPLETED");
    }

    @Test
    void shouldNotProcessMultimodeMovedEventWhenInterrogationIsLocked() {
        // Given: Interrogation with locked=true (created by SQL script)
        String interrogationId = "MOVED-LOCKED-001";

        // Verify initial state from SQL script
        var initialState = stateDataService.findStateData(interrogationId);
        assertThat(initialState).isPresent();
        assertThat(initialState.get().state()).isEqualTo(StateDataType.INIT);

        // Create and publish a MULTIMODE_MOVED event
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload(
                "MULTIMODE_MOVED",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", interrogationId);
                    builder.put("mode", "CAWI");
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // When: Wait for the event to be processed through the complete flow
        await()
                .atMost(25, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Verify event was stored in inbox (subscriber received it)
                    var inboxRecord = inboxJpaRepository.findById(eventId);
                    assertThat(inboxRecord).isPresent();
                });

        // Then: Verify StateData was NOT updated (still INIT because locked=true)
        var finalState = stateDataService.findStateData(interrogationId);
        assertThat(finalState).isPresent();
        assertThat(finalState.get().state()).isEqualTo(StateDataType.INIT); // State should NOT be changed to IS_MOVED
    }

    @Test
    void shouldProcessMultimodeMovedEventWhenInterrogationIsNotLocked() {
        // Given: Interrogation with locked=false (created by SQL script)
        String interrogationId = "MOVED-UNLOCKED-001";

        // Verify initial state from SQL script
        var initialState = stateDataService.findStateData(interrogationId);
        assertThat(initialState).isPresent();
        assertThat(initialState.get().state()).isEqualTo(StateDataType.INIT);

        // Create and publish a MULTIMODE_MOVED event
        UUID eventId = UUID.randomUUID();
        ObjectNode payload = createEventPayload(
                "MULTIMODE_MOVED",
                "QUESTIONNAIRE",
                builder -> {
                    builder.put("interrogationId", interrogationId);
                    builder.put("mode", "CAWI");
                }
        );

        OutboxDB outboxEvent = new OutboxDB(eventId, payload);
        outboxEvent.setCreatedDate(LocalDateTime.now());
        eventsJpaRepository.save(outboxEvent);

        // When: Wait for the event to be processed through the complete flow
        await()
                .atMost(25, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // Verify event was stored in inbox
                    var inboxRecord = inboxJpaRepository.findById(eventId);
                    assertThat(inboxRecord).isPresent();

                    // Verify StateData was updated to IS_MOVED
                    var updatedState = stateDataService.findStateData(interrogationId);
                    assertThat(updatedState).isPresent();
                    assertThat(updatedState.get().state()).isEqualTo(StateDataType.IS_MOVED);
                });

        // Then: Verify final state
        var finalState = stateDataService.findStateData(interrogationId);
        assertThat(finalState).isPresent();
        assertThat(finalState.get().state()).isEqualTo(StateDataType.IS_MOVED);
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