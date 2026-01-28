package fr.insee.queen.jms.service.consummers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadLeafStatesInnerDto;
import fr.insee.queen.domain.interrogation.model.LeafState;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.service.StateDataService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;
import fr.insee.queen.jms.service.EventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionnaireLeafStatesUpdatedEventConsumer implements EventConsumer {

    private final StateDataService stateDataService;
    private final Clock clock;

    @Override
    public void consume(EventDto eventDto) {
        if (eventDto.getEventType() != EventDto.EventTypeEnum.QUESTIONNAIRE_LEAF_STATES_UPDATED) {
            return;
        }

        log.info("QuestionnaireLeafStatesUpdatedEventConsumer: Processing QUESTIONNAIRE_LEAF_STATES_UPDATED event with correlationId: {}",
                eventDto.getCorrelationId());

        try {
            var payload = eventDto.getPayload();
            if (payload == null || payload.getInterrogationId() == null) {
                log.error("QUESTIONNAIRE_LEAF_STATES_UPDATED event missing interrogationId in payload");
                return;
            }

            var interrogationId = payload.getInterrogationId();
            log.info("Updating leaf states for interrogation: {}", interrogationId);

            Long currentDate = ZonedDateTime.now(clock).toInstant().toEpochMilli();

            var existingStateData = stateDataService.findStateData(interrogationId);

            if (existingStateData.isPresent()) {
                StateData existing = existingStateData.get();

                List<LeafState> leafStates = convertLeafStates(payload.getLeafStates());

                StateData newStateData = new StateData(
                        existing.state(),
                        currentDate,
                        existing.currentPage(),
                        leafStates
                );

                stateDataService.saveStateData(interrogationId, newStateData, false);

                log.info("QUESTIONNAIRE_LEAF_STATES_UPDATED event with correlationId {} processed successfully - interrogation {} updated with {} leaf states",
                        eventDto.getCorrelationId(), interrogationId, leafStates.size());
            } else {
                log.warn("No existing state data found for interrogation: {}, skipping leaf states update", interrogationId);
            }

        } catch (StateDataInvalidDateException e) {
            log.error("Invalid date when updating state data for QUESTIONNAIRE_LEAF_STATES_UPDATED event with correlationId: {}",
                    eventDto.getCorrelationId(), e);
        } catch (Exception e) {
            log.error("Error processing QUESTIONNAIRE_LEAF_STATES_UPDATED event with correlationId: {}",
                    eventDto.getCorrelationId(), e);
            throw new RuntimeException("Failed to process QUESTIONNAIRE_LEAF_STATES_UPDATED event", e);
        }
    }

    private List<LeafState> convertLeafStates(List<EventPayloadLeafStatesInnerDto> dtoLeafStates) {
        if (dtoLeafStates == null || dtoLeafStates.isEmpty()) {
            return List.of();
        }

        return dtoLeafStates.stream()
                .map(dto -> new LeafState(
                        dto.getState() != null ? dto.getState().getValue() : null,
                        dto.getDate() != null ? dto.getDate().toEpochMilli() : null
                ))
                .toList();
    }
}
