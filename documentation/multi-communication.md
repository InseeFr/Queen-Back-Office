# Cross-Environment Communication - Architecture and Configuration

## Overview

The cross-environment communication system enables event synchronization between different instances of the Queen application via ActiveMQ Topics. It implements the **Outbox/Inbox pattern** to guarantee reliable message delivery.

### Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                        Environment A                             │
│  ┌──────────┐    ┌───────────┐    ┌───────────────────┐          │
│  │ Business │───▶│  Outbox   │───▶│ OutboxScheduler   │          │
│  │  Logic   │    │   Table   │    │  (Publisher)      │          │
│  └──────────┘    └───────────┘    └──────────┬────────┘          │
│                                              │                   │
└──────────────────────────────────────────────┼───────────────────┘
                                               │
                                               ▼
                                    ┌─────────────────────┐
                                    │   ActiveMQ Topic    │
                                    │  multimode_events   │
                                    └──────────┬──────────┘
                                               │
┌──────────────────────────────────────────────┼───────────────────┐
│                        Environment B         │                   │
│                                              ▼                   │
│                              ┌──────────────────────┐            │
│                              │ MultimodeSubscriber  │            │
│                              │    (Consumer)        │            │
│                              └──────────┬───────────┘            │
│                                         │                        │
│                                         ▼                        │
│  ┌─────────────┐            ┌───────────────┐                    │
│  │    Inbox    │◀───────────│  Buisiness    │                    │
│  │    Table    │   (R/W)    │    Logic      │                    │
│  └─────────────┘            └───────────────┘                    │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

## Components

### 1. Database Tables

#### `outbox` Table
Stores events to be published to the ActiveMQ topic.

**Structure:**
- `id` (UUID): Unique event identifier (used as correlationId)
- `payload` (JSONB): Event content (serialized EventDto)
- `created_date` (TIMESTAMP): Creation date
- `processed_date` (TIMESTAMP): Processing date (NULL if unprocessed)

**Changelog:** `queen-infra-db/src/main/resources/db/changelog/700_events.xml`

**Liquibase Context:** `cross-env-emitter`

#### `inbox` Table
Stores events received from the ActiveMQ topic.

**Structure:**
- `id` (UUID): Unique identifier (= event correlationId)
- `payload` (JSONB): Received event content
- `created_date` (TIMESTAMP): Reception date

**Changelog:** `queen-infra-db/src/main/resources/db/changelog/720_inbox.xml`

**Liquibase Context:** `cross-env-consumer`

### 2. JPA Entities

#### OutboxDB
**Package:** `fr.insee.queen.infrastructure.db.events`

**File:** `queen-infra-db/src/main/java/fr/insee/queen/infrastructure/db/events/OutboxDB.java`

Represents a record in the outbox table.

#### InboxDB
**Package:** `fr.insee.queen.infrastructure.db.events`

**File:** `queen-infra-db/src/main/java/fr/insee/queen/infrastructure/db/events/InboxDB.java`

Represents a record in the inbox table.

### 3. Repositories

#### EventsJpaRepository
**Interface:** `fr.insee.queen.infrastructure.db.events.EventsJpaRepository`

**Main methods:**
- `createEvent(UUID id, ObjectNode event)`: Inserts an event into outbox
- `findUnprocessedEvents()`: Retrieves unprocessed events
- `markAsProcessed(UUID id, LocalDateTime processedDate)`: Marks an event as processed

#### InboxJpaRepository
**Interface:** `fr.insee.queen.infrastructure.db.events.InboxJpaRepository`

**Main methods:**
- `save(InboxDB inbox)`: Saves an event in inbox
- `findById(UUID id)`: Finds an event by its ID
- `existsById(UUID id)`: Checks event existence (for idempotence)
- `findAllOrderByCreatedDate()`: Lists all received events

### 4. Publisher (Emitter)

#### MultimodePublisher
**Package:** `fr.insee.queen.jms.service`

**File:** `queen-listener-jms/src/main/java/fr/insee/queen/jms/service/MultimodePublisher.java`

**Role:** Publishes EventDto to the ActiveMQ topic.

**Activation:** `@ConditionalOnProperty(prefix = "feature.multimode.publisher", name = "enabled", havingValue = "true")`

**Methods:**
- `publishEvent(EventDto eventDto, UUID correlationId)`: Publishes an event with its correlationId

#### OutboxScheduler
**Package:** `fr.insee.queen.jms.service`

**File:** `queen-listener-jms/src/main/java/fr/insee/queen/jms/service/OutboxScheduler.java`

**Role:** Scheduler that periodically retrieves unprocessed events from the outbox table and publishes them via MultimodePublisher.

**Configuration:**
- Default interval: 300 seconds (5 minutes)
- Configurable via `feature.multimode.publisher.scheduler.interval`

**Process:**
1. Retrieves unprocessed events (`processedDate IS NULL`)
2. Deserializes payload to EventDto
3. Publishes to topic with correlationId
4. Marks event as processed

### 5. Subscriber (Consumer)

#### MultimodeSubscriber
**Package:** `fr.insee.queen.jms.service`

**File:** `queen-listener-jms/src/main/java/fr/insee/queen/jms/service/MultimodeSubscriber.java`

**Role:** Listens to the ActiveMQ topic and saves received events in the inbox table.

**Activation:** `@ConditionalOnProperty(prefix = "feature.multimode.subscriber", name = "enabled", havingValue = "true")`

**Process:**
1. Receives a message from the topic
2. Deserializes to EventDto
3. Extracts the correlationId
4. **Checks idempotence**: if the event already exists in inbox, ignores the message
5. Otherwise, saves the event in inbox with correlationId as ID

**Idempotence:** The subscriber guarantees that an event is saved only once, even in case of duplicate messages.

### 6. JMS Configuration

#### JMSConfiguration
**Package:** `fr.insee.queen.jms.configuration`

**File:** `queen-listener-jms/src/main/java/fr/insee/queen/jms/configuration/JMSConfiguration.java`

**Configured Beans:**

1. **jmsListenerFactory**: Factory for queues (point-to-point)
2. **jmsQueuePublisher**: JMS template for publishing to queues
3. **topicJmsTemplate**: JMS template for publishing to topics (pub/sub)
4. **topicJmsListenerContainerFactory**: Factory for listening to topics (pub/sub)

### 7. Liquibase Context Management

#### LiquibaseContextCustomizer
**Package:** `fr.insee.queen.application.configuration`

**File:** `queen-application/src/main/java/fr/insee/queen/application/configuration/LiquibaseContextCustomizer.java`

**Role:** Dynamically configures Liquibase contexts based on configuration properties.

**Behavior:**
- If `feature.cross-environment-communication.emitter=true` → Activates `cross-env-emitter` context → Creates `outbox` table
- If `feature.cross-environment-communication.consumer=true` → Activates `cross-env-consumer` context → Creates `inbox` table

## Configuration

### Configuration Properties

#### queen-application Module

**File:** `queen-application/src/main/resources/application.yml`

```yaml
feature:
  cross-environment-communication:
    emitter: false    # Enables outbox table creation
    consumer: false   # Enables inbox table creation
```

#### queen-listener-jms Module

**File:** `queen-listener-jms/src/main/resources/application.yml`

```yaml
broker:
  name: artemis  # Uses ActiveMQ Artemis

feature:
  multimode:
    publisher:
      enabled: false
      scheduler:
        interval: 300000  # 5 minutes in milliseconds
        initialDelay: 1000
    subscriber:
      enabled: false
    topic: multimode_events
```

### Configuration Scenarios

#### Scenario 1: Publisher Instance Only
```yaml
# queen-application
feature:
  cross-environment-communication:
    emitter: true
    consumer: false

# queen-listener-jms
feature:
  multimode:
    publisher:
      enabled: true
    subscriber:
      enabled: false
```

**Result:**
- `outbox` table created
- Events published to topic
- No event reception

#### Scenario 2: Subscriber Instance Only
```yaml
# queen-application
feature:
  cross-environment-communication:
    emitter: false
    consumer: true

# queen-listener-jms
feature:
  multimode:
    publisher:
      enabled: false
    subscriber:
      enabled: true
```

**Result:**
- `inbox` table created
- Event reception from topic
- No event publication

#### Scenario 3: Bidirectional Instance
```yaml
# queen-application
feature:
  cross-environment-communication:
    emitter: true
    consumer: true

# queen-listener-jms
feature:
  multimode:
    publisher:
      enabled: true
    subscriber:
      enabled: true
```

**Result:**
- Both `outbox` and `inbox` tables created
- Event publication AND reception
- Enables bidirectional communication

## Message Format

### EventDto
Exchanged messages follow the `EventDto` structure from the `modelefiliere` module:

```json
{
  "eventType": "QUESTIONNAIRE_INIT",
  "aggregateType": "QUESTIONNAIRE",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "payload": {
    "interrogationId": "INT-001",
    "mode": "CAPI",
    "...": "..."
  }
}
```

**Fields:**
- `eventType`: Event type (QUESTIONNAIRE_INIT, QUESTIONNAIRE_LEAF_STATES_UPDATED, etc.)
- `aggregateType`: Concerned aggregate type (QUESTIONNAIRE, etc.)
- `correlationId`: Unique identifier used for tracking and idempotence
- `payload`: Event-specific data

## Complete Flow

### Event Publication

1. **Business Logic** → Creates a record in the `outbox` table
2. **OutboxScheduler** (every 5 minutes):
   - Retrieves unprocessed events
   - Calls `MultimodePublisher.publishEvent()`
3. **MultimodePublisher**:
   - Serializes EventDto to JSON
   - Publishes to ActiveMQ topic with correlationId
   - Returns to scheduler
4. **OutboxScheduler**:
   - Marks event as processed (`processedDate` set)

### Event Reception

1. **ActiveMQ Topic** → Broadcasts message to all subscribers
2. **MultimodeSubscriber**:
   - Receives the message
   - Deserializes to EventDto
   - Extracts correlationId
   - Checks if event already exists in inbox
   - If new: saves in `inbox` table with correlationId as ID
   - If existing: ignores (idempotence)
3. **Business Logic** → Can query the `inbox` table to process received events

## Tests

### Testcontainers Integration Tests

**File:** `queen-listener-jms/src/test/java/fr/insee/queen/jms/integration/ActiveMQPublishingIntegrationTest.java`

**Infrastructure:**
- PostgreSQL 14.15 (via Testcontainers)
- ActiveMQ Artemis 2.32.0 (via Testcontainers)

**Implemented tests:**

1. **shouldPublishMessageToActiveMQTopic**
   - Verifies message publication to topic

2. **shouldPublishMultipleMessagesToTopic**
   - Verifies multiple messages publication

3. **shouldPublishEventWithLeafStates**
   - Verifies publication of events with leaf states

4. **shouldVerifyActiveMQTopicConfiguration**
   - Verifies topic and properties configuration

5. **shouldSubscribeToTopicAndStoreInInbox**
   - **Complete bidirectional flow test**
   - Verifies: outbox → scheduler → topic → subscriber → inbox
   - Validates correlationId and payload content

6. **shouldStoreMultipleEventsInInbox**
   - Verifies reception of multiple events

7. **shouldIgnoreDuplicateMessagesInInbox**
   - **Idempotence test**
   - Verifies that a duplicate message is ignored

### Configuration Tests

**Files:**
- `queen-application/src/test/java/fr/insee/queen/application/configuration/OutboxTableCreatedWhenEmitterEnabledTest.java`
- `queen-application/src/test/java/fr/insee/queen/application/configuration/OutboxTableNotCreatedWhenEmitterDisabledTest.java`
- `queen-application/src/test/java/fr/insee/queen/application/configuration/InboxTableCreatedWhenConsumerEnabledTest.java`
- `queen-application/src/test/java/fr/insee/queen/application/configuration/InboxTableNotCreatedWhenConsumerDisabledTest.java`

**Tests:**
- Verify that tables are created/not created according to configuration
- Use Testcontainers with PostgreSQL
- Verify Liquibase contexts behavior

## Monitoring and Logs

### Publisher Logs

```
[INFO] Starting outbox scheduler - checking for unprocessed events
[INFO] Found 3 unprocessed events in outbox
[INFO] Publishing event to topic: multimode_events with correlationId: 550e8400-e29b-41d4-a716-446655440000
[INFO] Event successfully published to topic: multimode_events with correlationId: 550e8400-e29b-41d4-a716-446655440000
[INFO] Outbox scheduler completed - processed 3 events
```

### Subscriber Logs

```
[INFO] Received message from topic: multimode_events
[INFO] Processing event with correlationId: 550e8400-e29b-41d4-a716-446655440000
[INFO] Event with correlationId 550e8400-e29b-41d4-a716-446655440000 successfully stored in inbox
```

Or in case of duplicate:
```
[INFO] Received message from topic: multimode_events
[INFO] Processing event with correlationId: 550e8400-e29b-41d4-a716-446655440000
[INFO] Event with correlationId 550e8400-e29b-41d4-a716-446655440000 already exists in inbox, ignoring duplicate message
```

## Complete Sequence Diagram

```
┌─────────┐    ┌────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐       ┌────────┐
│Business │    │ Outbox │    │Scheduler │    │ActiveMQ  │    │Subscriber│       │  Inbox │
│ Logic   │    │  DB    │    │          │    │  Topic   │    │          │       │   DB   │
└────┬────┘    └───┬────┘    └────┬─────┘    └────┬─────┘    └────┬─────┘       └───┬────┘
     │             │              │               │               │                 │
     │─Insert─────▶│              │               │               │                 │
     │             │              │               │               │                 │
     │             │◀─────Poll────│               │               │                 │
     │             │──Events─────▶│               │               │                 │
     │             │              │               │               │                 │
     │             │              │──Publish─────▶│               │                 │
     │             │              │               │               │                 │
     │             │◀─Mark Processed──────────────│               │                 │
     │             │              │               │               │                 │
     │             │              │               │──Broadcast───▶│                 │
     │             │              │               │               │                 │
     │             │              │               │               │──Check Exists──▶│
     │             │              │               │               │◀─No─────────────│
     │             │              │               │               │──Insert────────▶│
     │             │              │               │               │                 │
```

## References

- **ActiveMQ Artemis**: https://activemq.apache.org/components/artemis/
- **Outbox Pattern**: https://microservices.io/patterns/data/transactional-outbox.html
- **Spring JMS**: https://docs.spring.io/spring-framework/reference/integration/jms.html
- **Liquibase Contexts**: https://docs.liquibase.com/concepts/changelogs/attributes/contexts.html

