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
 * Abstract base class for event consumers that update state data.
 * This class provides common logic for processing events and updating state data,
 * while allowing subclasses to specify which event type to handle and which state to set.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractStateDataEventConsumer implements EventConsumer {

    private final StateDataService stateDataService;
    private final Clock clock;

    /**
     * Returns the event type that this consumer should handle.
     */
    protected abstract EventDto.EventTypeEnum getEventType();

    /**
     * Returns the state data type to set when processing the event.
     */
    protected abstract StateDataType getStateDataType();

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

            // Check if the event can be consumed
            if (!canConsume(interrogationId)) {
                log.info("{} event with correlationId {} skipped - interrogation {} cannot be consumed",
                    getEventType(), eventDto.getCorrelationId(), interrogationId);
                return;
            }

            log.info("Updating state to {} for interrogation: {}", getStateDataType(), interrogationId);

            // Get current date
            Long currentDate = ZonedDateTime.now(clock).toInstant().toEpochMilli();

            // Create new state data with specified state type
            StateData newStateData = new StateData(
                getStateDataType(),
                currentDate,
                "1"
            );

            // Save the updated state data
            stateDataService.saveStateData(interrogationId, newStateData, false);

            log.info("{} event with correlationId {} processed successfully - interrogation {} updated to {}",
                getEventType(), eventDto.getCorrelationId(), interrogationId, getStateDataType());

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