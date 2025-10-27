package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventsDaoTest {
    private EventsJpaFakeRepository eventsJpaRepository;
    private EventsDao dao;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init(){
        eventsJpaRepository = new EventsJpaFakeRepository();
        dao = new EventsDao(eventsJpaRepository);
    }

    @Test
    @DisplayName("On creating event createEvent call is triggered")
    void addEvent() throws JsonProcessingException {
        ObjectNode event = mapper.readValue("""
                {"type": "QUESTIONNAIRE_INIT", "payload": {}}
                """, ObjectNode.class);
        dao.createEvent(UUID.randomUUID(), event);
        assertThat(eventsJpaRepository.isCreated()).isTrue();
    }
}