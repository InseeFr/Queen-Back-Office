package fr.insee.queen.jms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.infrastructure.db.events.InboxDB;
import fr.insee.queen.infrastructure.db.events.InboxJpaRepository;
import fr.insee.queen.jms.configuration.MultimodeProperties;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "feature.multimode.subscriber", name = "enabled", havingValue = "true")
public class MultimodeSubscriber {

    private final InboxJpaRepository inboxJpaRepository;
    private final ObjectMapper objectMapper;
    private final MultimodeProperties multimodeProperties;

    @JmsListener(
            destination = "${feature.multimode.topic}",
            containerFactory = "topicJmsListenerContainerFactory"
    )
    public void onMessage(Message message) throws JMSException {
        try {
            String jsonMessage = message.getBody(String.class);
            log.info("Received message from topic: {}", multimodeProperties.getTopic());
            log.debug("Message content: {}", jsonMessage);

            // Deserialize JSON to EventDto
            EventDto eventDto = objectMapper.readValue(jsonMessage, EventDto.class);

            // Extract correlationId as the inbox ID
            UUID correlationId = eventDto.getCorrelationId();
            if (correlationId == null) {
                log.error("Received event without correlationId, skipping");
                return;
            }

            log.debug("Processing event with correlationId: {}", correlationId);

            // Check if event already exists in inbox (idempotence)
            if (inboxJpaRepository.existsById(correlationId)) {
                log.info("Event with correlationId {} already exists in inbox, ignoring duplicate message", correlationId);
                return;
            }

            // Store the message in inbox table
            InboxDB inboxRecord = new InboxDB(
                    correlationId,
                    objectMapper.valueToTree(eventDto)
            );

            inboxJpaRepository.save(inboxRecord);

            log.info("Event with correlationId {} successfully stored in inbox", correlationId);

        } catch (Exception e) {
            log.error("Error processing message from topic {}", multimodeProperties.getTopic(), e);
            throw new RuntimeException("Failed to process message from topic", e);
        }
    }
}