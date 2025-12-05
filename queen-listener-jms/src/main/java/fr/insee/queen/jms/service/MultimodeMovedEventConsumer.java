package fr.insee.queen.jms.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Event consumer that handles MULTIMODE_MOVED events specifically.
 * This consumer updates the state data to IS_MOVED when a MULTIMODE_MOVED event is received.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MultimodeMovedEventConsumer implements EventConsumer {

    private final StateDataService stateDataService;
    private final Clock clock;

    @Override
    public void consume(EventDto eventDto) {
        // Only process MULTIMODE_MOVED events
        if (eventDto.getEventType() != EventDto.EventTypeEnum.MULTIMODE_MOVED) {
            return;
        }

        log.info("MultimodeMovedEventConsumer: Processing MULTIMODE_MOVED event with correlationId: {}",
            eventDto.getCorrelationId());

        try {
            // Extract interrogationId from event payload
            var payload = eventDto.getPayload();
            if (payload == null || payload.getInterrogationId() == null) {
                log.error("MULTIMODE_MOVED event missing interrogationId in payload");
                return;
            }

            var interrogationId = payload.getInterrogationId();
            log.info("Updating state to IS_MOVED for interrogation: {}", interrogationId);

            // Get current state data or create new one
            Optional<StateData> currentStateData = stateDataService.findStateData(interrogationId);
            String currentPage = currentStateData.map(StateData::currentPage).orElse(null);
            Long currentDate = ZonedDateTime.now(clock).toInstant().toEpochMilli();

            // Create new state data with IS_MOVED state
            StateData newStateData = new StateData(
                StateDataType.IS_MOVED,
                currentDate,
                currentPage
            );

            // Save the updated state data
            stateDataService.saveStateData(interrogationId, newStateData, false);

            log.info("MULTIMODE_MOVED event with correlationId {} processed successfully - interrogation {} updated to IS_MOVED",
                eventDto.getCorrelationId(), interrogationId);

        } catch (StateDataInvalidDateException e) {
            log.error("Invalid date when updating state data for MULTIMODE_MOVED event with correlationId: {}",
                eventDto.getCorrelationId(), e);
        } catch (Exception e) {
            log.error("Error processing MULTIMODE_MOVED event with correlationId: {}",
                eventDto.getCorrelationId(), e);
            throw new RuntimeException("Failed to process MULTIMODE_MOVED event", e);
        }
    }
}