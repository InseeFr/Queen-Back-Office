package fr.insee.queen.infrastructure.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.infrastructure.broker.dto.EventDto;
import fr.insee.queen.infrastructure.broker.dto.EventPayloadDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class QueueConsumerTest {

    private QueueConsumer queueConsumer;
    private MessageConsumer consumer1;
    private MessageConsumer consumer2;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        consumer1 = mock(MessageConsumer.class);
        consumer2 = mock(MessageConsumer.class);
        objectMapper = new ObjectMapper();
        queueConsumer = new QueueConsumer(List.of(consumer1, consumer2));
    }

    @Test
    @DisplayName("On receiving valid message when consumer matches then event is consumed")
    void testListen_ValidMessage_ConsumerMatches() {
        // given
        when(consumer1.shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT)).thenReturn(true);
        when(consumer2.shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT)).thenReturn(false);

        String json = """
                {
                    "before": null,
                    "after": {
                        "id": "123",
                        "payload": "{\\"eventType\\":\\"QUESTIONNAIRE_INIT\\",\\"aggregateType\\":\\"QUESTIONNAIRE\\",\\"payload\\":{\\"interrogationId\\":\\"1\\",\\"mode\\":\\"CAPI\\"}}",
                        "created_date": 1234567890
                    },
                    "source": null,
                    "op": "c",
                    "ts_ms": 1234567890
                }
                """;

        // when
        queueConsumer.listen(json, "key");

        // then
        verify(consumer1, times(1)).shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        verify(consumer1, times(1)).consume(eq(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT), any(EventPayloadDto.class));
        verify(consumer2, times(1)).shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        verify(consumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("On receiving valid message when multiple consumers match then all consume event")
    void testListen_ValidMessage_MultipleConsumersMatch() {
        // given
        when(consumer1.shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT)).thenReturn(true);
        when(consumer2.shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT)).thenReturn(true);

        String json = """
                {
                    "before": null,
                    "after": {
                        "id": "123",
                        "payload": "{\\"eventType\\":\\"QUESTIONNAIRE_INIT\\",\\"aggregateType\\":\\"QUESTIONNAIRE\\",\\"payload\\":{\\"interrogationId\\":\\"1\\",\\"mode\\":\\"CAPI\\"}}",
                        "created_date": 1234567890
                    },
                    "source": null,
                    "op": "c",
                    "ts_ms": 1234567890
                }
                """;

        // when
        queueConsumer.listen(json, "key");

        // then
        verify(consumer1, times(1)).consume(eq(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT), any(EventPayloadDto.class));
        verify(consumer2, times(1)).consume(eq(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT), any(EventPayloadDto.class));
    }

    @Test
    @DisplayName("On receiving valid message when no consumer matches then no consumption occurs")
    void testListen_ValidMessage_NoConsumerMatches() {
        // given
        when(consumer1.shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT)).thenReturn(false);
        when(consumer2.shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT)).thenReturn(false);

        String json = """
                {
                    "before": null,
                    "after": {
                        "id": "123",
                        "payload": "{\\"eventType\\":\\"QUESTIONNAIRE_INIT\\",\\"aggregateType\\":\\"QUESTIONNAIRE\\",\\"payload\\":{\\"interrogationId\\":\\"1\\",\\"mode\\":\\"CAPI\\"}}",
                        "created_date": 1234567890
                    },
                    "source": null,
                    "op": "c",
                    "ts_ms": 1234567890
                }
                """;

        // when
        queueConsumer.listen(json, "key");

        // then
        verify(consumer1, never()).consume(any(), any());
        verify(consumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("On receiving message when envelope is null then no consumption occurs")
    void testListen_NullEnvelope() {
        // given
        String json = "null";

        // when
        queueConsumer.listen(json, "key");

        // then
        verify(consumer1, never()).shouldConsume(any());
        verify(consumer2, never()).shouldConsume(any());
        verify(consumer1, never()).consume(any(), any());
        verify(consumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("On receiving message when after field is null then no consumption occurs")
    void testListen_NullAfterField() {
        // given
        String json = """
                {
                    "before": null,
                    "after": null,
                    "source": null,
                    "op": "d",
                    "ts_ms": 1234567890
                }
                """;

        // when
        queueConsumer.listen(json, "key");

        // then
        verify(consumer1, never()).shouldConsume(any());
        verify(consumer2, never()).shouldConsume(any());
        verify(consumer1, never()).consume(any(), any());
        verify(consumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("On receiving invalid JSON then error is logged and no consumption occurs")
    void testListen_InvalidJson() {
        // given
        String invalidJson = "{ invalid json }";

        // when
        queueConsumer.listen(invalidJson, "key");

        // then
        verify(consumer1, never()).shouldConsume(any());
        verify(consumer2, never()).shouldConsume(any());
        verify(consumer1, never()).consume(any(), any());
        verify(consumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("On receiving message with invalid event payload then error is logged and no consumption occurs")
    void testListen_InvalidEventPayload() {
        // given
        String json = """
                {
                    "before": null,
                    "after": {
                        "id": "123",
                        "payload": "{ invalid event json }",
                        "created_date": 1234567890
                    },
                    "source": null,
                    "op": "c",
                    "ts_ms": 1234567890
                }
                """;

        // when
        queueConsumer.listen(json, "key");

        // then
        verify(consumer1, never()).shouldConsume(any());
        verify(consumer2, never()).shouldConsume(any());
        verify(consumer1, never()).consume(any(), any());
        verify(consumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("On receiving valid message then payload is correctly passed to consumer")
    void testListen_PayloadCorrectlyPassed() {
        // given
        when(consumer1.shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED)).thenReturn(true);

        String json = """
                {
                    "before": null,
                    "after": {
                        "id": "456",
                        "payload": "{\\"eventType\\":\\"QUESTIONNAIRE_COMPLETED\\",\\"aggregateType\\":\\"QUESTIONNAIRE\\",\\"payload\\":{\\"interrogationId\\":\\"2\\",\\"mode\\":\\"CAWI\\"}}",
                        "created_date": 1234567890
                    },
                    "source": null,
                    "op": "c",
                    "ts_ms": 1234567890
                }
                """;

        // when
        queueConsumer.listen(json, "key");

        // then
        ArgumentCaptor<EventPayloadDto> payloadCaptor = ArgumentCaptor.forClass(EventPayloadDto.class);
        verify(consumer1).consume(eq(EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED), payloadCaptor.capture());

        EventPayloadDto capturedPayload = payloadCaptor.getValue();
        assertThat(capturedPayload).isNotNull();
        assertThat(capturedPayload.getInterrogationId()).isEqualTo("2");
        assertThat(capturedPayload.getMode().getValue()).isEqualTo("CAWI");
    }

    @Test
    @DisplayName("On receiving message when empty consumer list then no error occurs")
    void testListen_EmptyConsumerList() {
        // given
        QueueConsumer emptyConsumerQueue = new QueueConsumer(List.of());

        String json = """
                {
                    "before": null,
                    "after": {
                        "id": "123",
                        "payload": "{\\"eventType\\":\\"QUESTIONNAIRE_INIT\\",\\"aggregateType\\":\\"QUESTIONNAIRE\\",\\"payload\\":{\\"interrogationId\\":\\"1\\",\\"mode\\":\\"CAPI\\"}}",
                        "created_date": 1234567890
                    },
                    "source": null,
                    "op": "c",
                    "ts_ms": 1234567890
                }
                """;

        // when & then (should not throw exception)
        emptyConsumerQueue.listen(json, "key");
    }
}
