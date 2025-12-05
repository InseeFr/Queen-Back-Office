package fr.insee.queen.jms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.infrastructure.db.events.EventsJpaRepository;
import fr.insee.queen.infrastructure.db.events.OutboxDB;
import fr.insee.queen.jms.configuration.MultimodeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "feature.multimode.publisher", name = "enabled", havingValue = "true")
public class OutboxScheduler {

    private final EventsJpaRepository eventsJpaRepository;
    private final MultimodePublisher multimodePublisher;
    private final MultimodeProperties multimodeProperties;
    private final ObjectMapper objectMapper;

    @Scheduled(
            fixedDelayString = "${feature.multimode.publisher.scheduler.interval}",
            initialDelayString = "${feature.multimode.publisher.scheduler.initialDelay:1000}"
    )
    public void processOutboxEvents() {
        log.info("Starting outbox scheduler - checking for unprocessed events");

        try {
            List<OutboxDB> unprocessedEvents = eventsJpaRepository.findUnprocessedEvents();

            if (unprocessedEvents.isEmpty()) {
                log.debug("No unprocessed events found in outbox");
                return;
            }

            log.info("Found {} unprocessed events in outbox", unprocessedEvents.size());

            for (OutboxDB event : unprocessedEvents) {
                try {
                    log.debug("Processing event with id: {}", event.getId());

                    // Deserialize JSON payload to EventDto
                    EventDto eventDto = objectMapper.treeToValue(event.getPayload(), EventDto.class);

                    // Publish event to ActiveMQ with correlationId = outbox record UUID
                    multimodePublisher.publishEvent(eventDto, event.getId());

                    // Mark event as processed
                    eventsJpaRepository.markAsProcessed(event.getId(), LocalDateTime.now());

                    log.debug("Event {} successfully processed and marked", event.getId());

                } catch (Exception e) {
                    log.error("Error processing event with id: {}", event.getId(), e);
                    // Continue processing other events even if one fails
                }
            }

            log.info("Outbox scheduler completed - processed {} events", unprocessedEvents.size());

        } catch (Exception e) {
            log.error("Error during outbox scheduler execution", e);
        }
    }
}