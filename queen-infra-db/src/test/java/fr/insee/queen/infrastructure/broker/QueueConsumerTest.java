package fr.insee.queen.infrastructure.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.infrastructure.broker.debezium.CDCEnvelope;
import fr.insee.queen.infrastructure.broker.debezium.Value;
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
class QueueConsumerTest {

    @Mock
    private MessageConsumer consumer1;

    @Mock
    private MessageConsumer consumer2;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("When receiving a valid message, then consumers are notified")
    void testListen_ValidMessage_ConsumersNotified() throws JsonProcessingException {
        // given
        String keyJson = "key";

        BrokerMessage.Payload payload = new BrokerMessage.Payload("interrogation-123", null);
        BrokerMessage brokerMessage = new BrokerMessage("QUESTIONNAIRE_INIT", payload);

        ObjectMapper realObjectMapper = new ObjectMapper();

        when(consumer1.shouldConsume("QUESTIONNAIRE_INIT")).thenReturn(true);
        when(consumer2.shouldConsume("QUESTIONNAIRE_INIT")).thenReturn(false);

        // when
        String actualPayload = realObjectMapper.writeValueAsString(brokerMessage);

        QueueConsumer consumerWithRealMapper = new QueueConsumer(List.of(consumer1, consumer2));
        CDCEnvelope testEnvelope = new CDCEnvelope(null, new Value("id-123", actualPayload, 123456L), null, "c", 123456L, null);
        String testJson = realObjectMapper.writeValueAsString(testEnvelope);

        consumerWithRealMapper.listen(testJson, keyJson);

        // then
        verify(consumer1).shouldConsume("QUESTIONNAIRE_INIT");
        verify(consumer1).consume(eq("QUESTIONNAIRE_INIT"), any(BrokerMessage.Payload.class));
        verify(consumer2).shouldConsume("QUESTIONNAIRE_INIT");
        verify(consumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("When receiving a message with null record, then no consumer is called")
    void testListen_NullRecord_NoConsumerCalled() {
        // given
        String json = "null";
        String keyJson = "key";

        QueueConsumer consumerWithRealMapper = new QueueConsumer(List.of(consumer1, consumer2));

        // when
        consumerWithRealMapper.listen(json, keyJson);

        // then
        verify(consumer1, never()).shouldConsume(any());
        verify(consumer1, never()).consume(any(), any());
        verify(consumer2, never()).shouldConsume(any());
        verify(consumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("When receiving a message with null after, then no consumer is called")
    void testListen_NullAfter_NoConsumerCalled() throws JsonProcessingException {
        // given
        CDCEnvelope envelope = new CDCEnvelope(null, null, null, "c", 123456L, null);
        ObjectMapper realObjectMapper = new ObjectMapper();
        String json = realObjectMapper.writeValueAsString(envelope);
        String keyJson = "key";

        QueueConsumer consumerWithRealMapper = new QueueConsumer(List.of(consumer1, consumer2));

        // when
        consumerWithRealMapper.listen(json, keyJson);

        // then
        verify(consumer1, never()).shouldConsume(any());
        verify(consumer1, never()).consume(any(), any());
        verify(consumer2, never()).shouldConsume(any());
        verify(consumer2, never()).consume(any(), any());
    }

    @Test
    @DisplayName("When multiple consumers should consume, then all are notified")
    void testListen_MultipleConsumers_AllNotified() throws JsonProcessingException {
        // given
        BrokerMessage.Payload payload = new BrokerMessage.Payload("interrogation-456", null);
        BrokerMessage brokerMessage = new BrokerMessage("QUESTIONNAIRE_COMPLETE", payload);

        ObjectMapper realObjectMapper = new ObjectMapper();
        String actualPayload = realObjectMapper.writeValueAsString(brokerMessage);
        CDCEnvelope envelope = new CDCEnvelope(null, new Value("id-456", actualPayload, 123456L), null, "c", 123456L, null);
        String json = realObjectMapper.writeValueAsString(envelope);
        String keyJson = "key";

        when(consumer1.shouldConsume("QUESTIONNAIRE_COMPLETE")).thenReturn(true);
        when(consumer2.shouldConsume("QUESTIONNAIRE_COMPLETE")).thenReturn(true);

        QueueConsumer consumerWithRealMapper = new QueueConsumer(List.of(consumer1, consumer2));

        // when
        consumerWithRealMapper.listen(json, keyJson);

        // then
        verify(consumer1).shouldConsume("QUESTIONNAIRE_COMPLETE");
        verify(consumer1).consume(eq("QUESTIONNAIRE_COMPLETE"), any(BrokerMessage.Payload.class));
        verify(consumer2).shouldConsume("QUESTIONNAIRE_COMPLETE");
        verify(consumer2).consume(eq("QUESTIONNAIRE_COMPLETE"), any(BrokerMessage.Payload.class));
    }

    @Test
    @DisplayName("When no consumer should consume, then no consume is called")
    void testListen_NoConsumerShouldConsume_NoConsumeCall() throws JsonProcessingException {
        // given
        BrokerMessage.Payload payload = new BrokerMessage.Payload("interrogation-789", null);
        BrokerMessage brokerMessage = new BrokerMessage("UNKNOWN_EVENT", payload);

        ObjectMapper realObjectMapper = new ObjectMapper();
        String actualPayload = realObjectMapper.writeValueAsString(brokerMessage);
        CDCEnvelope envelope = new CDCEnvelope(null, new Value("id-789", actualPayload, 123456L), null, "c", 123456L, null);
        String json = realObjectMapper.writeValueAsString(envelope);
        String keyJson = "key";

        when(consumer1.shouldConsume("UNKNOWN_EVENT")).thenReturn(false);
        when(consumer2.shouldConsume("UNKNOWN_EVENT")).thenReturn(false);

        QueueConsumer consumerWithRealMapper = new QueueConsumer(List.of(consumer1, consumer2));

        // when
        consumerWithRealMapper.listen(json, keyJson);

        // then
        verify(consumer1).shouldConsume("UNKNOWN_EVENT");
        verify(consumer1, never()).consume(any(), any());
        verify(consumer2).shouldConsume("UNKNOWN_EVENT");
        verify(consumer2, never()).consume(any(), any());
    }
}
