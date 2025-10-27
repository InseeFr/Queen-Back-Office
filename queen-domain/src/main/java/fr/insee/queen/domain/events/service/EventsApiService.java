package fr.insee.queen.domain.events.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.gateway.EventsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class EventsApiService implements EventsService {
    private final EventsRepository eventsRepository;

    @Override
    public void createEvent(ObjectNode event) {
        eventsRepository.createEvent(UUID.randomUUID(), event);
    }
}
