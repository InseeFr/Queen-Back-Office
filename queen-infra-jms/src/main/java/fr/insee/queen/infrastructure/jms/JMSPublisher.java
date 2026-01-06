package fr.insee.queen.infrastructure.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.domain.messaging.port.serverside.Publisher;
import fr.insee.queen.infrastructure.jms.configuration.MultimodeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "feature.multimode.publisher", name = "enabled", havingValue = "true")
public class JMSPublisher implements Publisher {

    private final JmsTemplate topicJmsTemplate;
    private final MultimodeProperties multimodeProperties;
    private final ObjectMapper objectMapper;

    public JMSPublisher(@Qualifier("topicJmsTemplate") JmsTemplate topicJmsTemplate,
                        MultimodeProperties multimodeProperties,
                        ObjectMapper objectMapper) {
        this.topicJmsTemplate = topicJmsTemplate;
        this.multimodeProperties = multimodeProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Publishes an EventDto to ActiveMQ topic, adding the correlationId.
     *
     * @param eventDto      The event to publish
     * @param correlationId The correlation ID (typically the outbox record UUID)
     */
    @Override
    public void publish(EventDto eventDto, UUID correlationId) {
        String topicName = multimodeProperties.getTopic();
        log.info("Publishing event to topic: {} with correlationId: {}", topicName, correlationId);

        try {
            // Set the correlationId before publishing
            eventDto.setCorrelationId(correlationId);

            // Serialize to JSON
            String jsonMessage = objectMapper.writeValueAsString(eventDto);

            // Publish to ActiveMQ
            topicJmsTemplate.convertAndSend(topicName, jsonMessage);

            log.info("Event successfully published to topic: {} with correlationId: {}", topicName, correlationId);
        } catch (JsonProcessingException e) {
            log.error("Error serializing event to JSON: {}", eventDto, e);
            throw new RuntimeException("Failed to serialize event to JSON", e);
        } catch (Exception e) {
            log.error("Error publishing event to topic: {}", topicName, e);
            throw new RuntimeException("Failed to publish event to topic: " + topicName, e);
        }
    }
}
