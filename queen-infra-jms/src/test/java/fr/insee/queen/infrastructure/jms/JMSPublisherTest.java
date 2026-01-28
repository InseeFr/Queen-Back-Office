package fr.insee.queen.infrastructure.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadDto;
import fr.insee.modelefiliere.ModeDto;
import fr.insee.queen.infrastructure.jms.configuration.MultimodeProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JMSPublisherTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private MultimodeProperties multimodeProperties;

    private JMSPublisher jmsPublisher;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        jmsPublisher = new JMSPublisher(jmsTemplate, multimodeProperties, objectMapper);
    }

    @Test
    void shouldPublishEventSuccessfully() {
        // Given
        String topicName = "test_topic";
        UUID correlationId = UUID.randomUUID();

        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId("123");
        payload.setMode(ModeDto.CAPI);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        eventDto.setAggregateType(EventDto.AggregateTypeEnum.QUESTIONNAIRE);
        eventDto.setPayload(payload);

        when(multimodeProperties.getTopic()).thenReturn(topicName);

        // When
        jmsPublisher.publish(eventDto, correlationId);

        // Then
        verify(jmsTemplate, times(1)).convertAndSend(eq(topicName), anyString());
        verify(multimodeProperties, times(1)).getTopic();
    }

    @Test
    void shouldThrowExceptionWhenPublishFails() {
        // Given
        String topicName = "test_topic";
        UUID correlationId = UUID.randomUUID();

        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId("123");
        payload.setMode(ModeDto.CAPI);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        eventDto.setAggregateType(EventDto.AggregateTypeEnum.QUESTIONNAIRE);
        eventDto.setPayload(payload);

        when(multimodeProperties.getTopic()).thenReturn(topicName);
        doThrow(new RuntimeException("JMS error")).when(jmsTemplate).convertAndSend(anyString(), anyString());

        // When/Then
        assertThatThrownBy(() -> jmsPublisher.publish(eventDto, correlationId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to publish event to topic");

        verify(jmsTemplate, times(1)).convertAndSend(eq(topicName), anyString());
    }

    @Test
    void shouldPublishEmptyPayload() {
        // Given
        String topicName = "test_topic";
        UUID correlationId = UUID.randomUUID();

        EventPayloadDto payload = new EventPayloadDto();

        EventDto eventDto = new EventDto();
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        eventDto.setAggregateType(EventDto.AggregateTypeEnum.QUESTIONNAIRE);
        eventDto.setPayload(payload);

        when(multimodeProperties.getTopic()).thenReturn(topicName);

        // When
        jmsPublisher.publish(eventDto, correlationId);

        // Then
        verify(jmsTemplate, times(1)).convertAndSend(eq(topicName), anyString());
    }
}
