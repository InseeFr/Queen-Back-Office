package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.events.gateway.EventsRepository;
import fr.insee.queen.domain.events.model.Event;
import fr.insee.queen.infrastructure.db.campaign.entity.CampaignDB;
import fr.insee.queen.infrastructure.db.campaign.entity.QuestionnaireModelDB;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class EventsDao implements EventsRepository {
    private final EventsJpaRepository jpaRepository;

    @Override
    public void createEvent(UUID id, ObjectNode paradataValue) {
        jpaRepository.createEvent(id, paradataValue);
    }

    @Override
    public Optional<List<Event>> getAllNewEvents() {
        List<EventsDB> events = jpaRepository.findByUpdatedDateIsNull();


        return Optional.of(
                events.stream().map(e -> new Event(
                        e.getId(),
                        e.getValue())
        ).toList());
    }

    @Override
    public void ack(UUID id) {
        jpaRepository.setUpdatedDateToNow(id);
    }
}
