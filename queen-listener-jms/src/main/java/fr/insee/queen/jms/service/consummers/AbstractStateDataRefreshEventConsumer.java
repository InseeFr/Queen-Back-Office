package fr.insee.queen.jms.service.consummers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import fr.insee.queen.jms.service.EventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * Abstract base class for event consumers that refresh the state data date.
 * This class provides logic for updating only the date of existing state data,
 * or creating new state data with INIT type if none exists.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractStateDataRefreshEventConsumer implements EventConsumer {

    private final StateDataService stateDataService;
    private final Clock clock;

    /**
     * Returns the event type that this consumer should handle.
     */
    protected abstract EventDto.EventTypeEnum getEventType();

    @Override
    public void consume(EventDto eventDto) {
        // Only process events of the specified type
        if (eventDto.getEventType() != getEventType()) {
            return;
        }

        log.info("{}: Processing {} event with correlationId: {}",
            this.getClass().getSimpleName(), getEventType(), eventDto.getCorrelationId());

        try {
            // Extract interrogationId from event payload
            var payload = eventDto.getPayload();
            if (payload == null || payload.getInterrogationId() == null) {
                log.error("{} event missing interrogationId in payload", getEventType());
                return;
            }

            var interrogationId = payload.getInterrogationId();
            log.info("Refreshing state data date for interrogation: {}", interrogationId);

            // Get current date
            Long currentDate = ZonedDateTime.now(clock).toInstant().toEpochMilli();

            // Try to get existing state data
            var existingStateData = stateDataService.findStateData(interrogationId);

            StateData newStateData;
            if (existingStateData.isPresent()) {
                // Keep existing state and currentPage, only update date
                StateData existing = existingStateData.get();
                newStateData = new StateData(
                    existing.state(),
                    currentDate,
                    existing.currentPage()
                );
                log.info("Updated date for existing state {} for interrogation: {}",
                    existing.state(), interrogationId);

                // Save the state data
                stateDataService.saveStateData(interrogationId, newStateData, false);

                log.info("{} event with correlationId {} processed successfully - interrogation {} state data refreshed",
                        getEventType(), eventDto.getCorrelationId(), interrogationId);
            }



        } catch (StateDataInvalidDateException e) {
            log.error("Invalid date when updating state data for {} event with correlationId: {}",
                getEventType(), eventDto.getCorrelationId(), e);
        } catch (Exception e) {
            log.error("Error processing {} event with correlationId: {}",
                getEventType(), eventDto.getCorrelationId(), e);
            throw new RuntimeException("Failed to process " + getEventType() + " event", e);
        }
    }
}