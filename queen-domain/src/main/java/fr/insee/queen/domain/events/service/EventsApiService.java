package fr.insee.queen.domain.events.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.gateway.EventsRepository;
import fr.insee.queen.domain.events.model.Event;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EventsApiService implements EventsService {
    private final EventsRepository eventsRepository;
    private final EventBroker eventBroker;

    @Override
    public void createEvent(ObjectNode event) {
        eventsRepository.createEvent(UUID.randomUUID(), event);
    }

    @Override
    public List<Event> getAllNewEvents() {
        return eventsRepository.getAllNewEvents().get();
    }

    @Override
    public void ackEvent(UUID id) {
        eventsRepository.ack(id);
    }

    @Override
    public void publishEvent(ObjectNode value) {
        eventBroker.publishEvent(value);
    }
}
