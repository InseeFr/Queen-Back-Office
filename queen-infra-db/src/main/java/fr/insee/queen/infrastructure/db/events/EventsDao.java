package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.event.gateway.EventsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class EventsDao implements EventsRepository {
    private final EventsJpaRepository jpaRepository;

    @Override
    public void createEvent(UUID id, ObjectNode event) {
        jpaRepository.createEvent(id, event);
    }
}