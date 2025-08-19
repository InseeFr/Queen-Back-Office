package fr.insee.queen.application.events.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.events.service.dummy.EventFakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventsControllerTest {
    private EventFakeService eventsService;
    private EventsController eventsController;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init(){
        eventsService = new EventFakeService();
        eventsController = new EventsController(eventsService);
    }

    @Test
    @DisplayName("On creating event createEvent call is triggered")
    void addEvent() throws JsonProcessingException {
        ObjectNode event = mapper.readValue("""
                {"type": "QUESTIONNAIRE_INIT", "payload": {}}
                """, ObjectNode.class);
        eventsController.addEvent(event);
        assertThat(eventsService.isCreated()).isTrue();
    }
}