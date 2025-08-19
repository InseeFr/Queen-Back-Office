package fr.insee.queen.domain.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.service.gateway.EventsFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventsApiServiceTest {

    private EventsFakeRepository eventsRepository;
    private EventsApiService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init(){
        eventsRepository = new EventsFakeRepository();
        service = new EventsApiService(eventsRepository);
    }

    @Test
    @DisplayName("On creating event createEvent call is triggered")
    void addEvent() throws JsonProcessingException {
        ObjectNode event = mapper.readValue("""
                {"type": "QUESTIONNAIRE_INIT", "payload": {}}
                """, ObjectNode.class);
        service.createEvent(event);
        assertThat(eventsRepository.isCreated()).isTrue();
    }
}