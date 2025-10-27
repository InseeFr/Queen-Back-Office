package fr.insee.queen.infrastructure.broker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPulsarConsumerTest {

    @Mock
    private MessageConsumer messageConsumer1;

    @Mock
    private MessageConsumer messageConsumer2;

    private EventPulsarConsumer eventPulsarConsumer;

    @BeforeEach
    void setUp() {
        eventPulsarConsumer = new EventPulsarConsumer(List.of(messageConsumer1, messageConsumer2));
    }

    @Test
    @DisplayName("Should process valid Debezium message and dispatch to interested consumers")
    void testListenWithValidMessage() {
        String json = """
                {
                    "before": null,
                    "after": {
                        "id": "event-123",
                        "payload": "{\\"type\\":\\"VALIDATED\\",\\"payload\\":{\\"interrogationId\\":\\"interro-456\\",\\"leafStates\\":[]}}",
                        "created_date": 1234567890
                    },
                    "source": {
                        "version": "1.0",
                        "connector": "postgresql",
                        "name": "dbserver1",
                        "ts_ms": 1234567890,
                        "snapshot": "false",
                        "db": "mydb",
                        "schema": "public",
                        "table": "outbox"
                    },
                    "op": "c",
                    "ts_ms": 1234567890
                }
                """;

        when(messageConsumer1.shouldConsume("VALIDATED")).thenReturn(true);
        when(messageConsumer2.shouldConsume("VALIDATED")).thenReturn(false);

        eventPulsarConsumer.listen(json, "key-123");

        verify(messageConsumer1).shouldConsume("VALIDATED");
        verify(messageConsumer1).consume(eq("VALIDATED"), any(BrokerMessage.Payload.class));
        verify(messageConsumer2).shouldConsume("VALIDATED");
        verify(messageConsumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("Should not process message when after is null")
    void testListenWithNullAfter() {
        String json = """
                {
                    "before": null,
                    "after": null,
                    "op": "d",
                    "ts_ms": 1234567890
                }
                """;

        eventPulsarConsumer.listen(json, "key-123");

        verify(messageConsumer1, never()).shouldConsume(any());
        verify(messageConsumer1, never()).consume(any(), any());
        verify(messageConsumer2, never()).shouldConsume(any());
        verify(messageConsumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("Should handle JSON parsing error gracefully")
    void testListenWithInvalidJson() {
        String invalidJson = "{ invalid json }";

        // Should not throw exception
        eventPulsarConsumer.listen(invalidJson, "key-123");

        verify(messageConsumer1, never()).shouldConsume(any());
        verify(messageConsumer1, never()).consume(any(), any());
    }

    @Test
    @DisplayName("Should dispatch to multiple consumers when they are interested")
    void testListenWithMultipleInterestedConsumers() {
        String json = """
                {
                    "before": null,
                    "after": {
                        "id": "event-123",
                        "payload": "{\\"type\\":\\"MULTIMODE_MOVED\\",\\"payload\\":{\\"interrogationId\\":\\"interro-789\\",\\"leafStates\\":[]}}",
                        "created_date": 1234567890
                    },
                    "op": "c",
                    "ts_ms": 1234567890
                }
                """;

        when(messageConsumer1.shouldConsume("MULTIMODE_MOVED")).thenReturn(true);
        when(messageConsumer2.shouldConsume("MULTIMODE_MOVED")).thenReturn(true);

        eventPulsarConsumer.listen(json, "key-123");

        verify(messageConsumer1).shouldConsume("MULTIMODE_MOVED");
        verify(messageConsumer1).consume(eq("MULTIMODE_MOVED"), any(BrokerMessage.Payload.class));
        verify(messageConsumer2).shouldConsume("MULTIMODE_MOVED");
        verify(messageConsumer2).consume(eq("MULTIMODE_MOVED"), any(BrokerMessage.Payload.class));
    }
}
