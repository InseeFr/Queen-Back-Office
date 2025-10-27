package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.gateway.EventsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class EventsDao implements EventsRepository {
    private final EventsJpaRepository<OutboxDB, UUID> jpaRepository;

    @Override
    public void createEvent(UUID id, ObjectNode paradataValue) {
        jpaRepository.createEvent(id, paradataValue);
    }
}
