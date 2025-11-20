package fr.insee.queen.application.crossenvironmentcommunication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.crossenvironmentcommunication.service.dummy.EventFakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventsControllerTest {

    private EventFakeService eventService;
    private EventsController controller;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        eventService = new EventFakeService();
        controller = new EventsController(eventService);
    }

    @Test
    @DisplayName("On creating event when event is valid then save is triggered")
    void testAddEvent_Success() throws JsonProcessingException {
        // given
        ObjectNode event = mapper.readValue("""
                {"type": "survey-unit-created", "id": "123", "data": {"name": "Test Survey"}}
                """, ObjectNode.class);

        // when
        controller.addEvent(event);

        // then
        assertThat(eventService.isSaved()).isTrue();
        assertThat(eventService.getSavedEvent()).isEqualTo(event);
    }
}