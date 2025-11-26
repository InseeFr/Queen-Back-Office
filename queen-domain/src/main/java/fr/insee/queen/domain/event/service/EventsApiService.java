package fr.insee.queen.domain.event.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.event.gateway.EventsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class EventsApiService implements EventService {

    private final EventsRepository eventsRepository;

    @Override
    public void saveEvent(ObjectNode event) {
        eventsRepository.createEvent(UUID.randomUUID(), event);
    }
}
