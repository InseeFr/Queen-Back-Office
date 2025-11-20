package fr.insee.queen.application.crossenvironmentcommunication.controller;

import fr.insee.queen.domain.event.service.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "feature.cross-environment-communication.emitter=true"
})
@DisplayName("Integration tests when EventsController is enabled")
class EventsControllerEnabledIT {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public EventService eventService() {
            return Mockito.mock(EventService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventService eventService;

    @Test
    @WithMockUser
    @DisplayName("POST /api/events should return 201 CREATED when event is valid")
    void testAddEvent_Success() throws Exception {
        // given
        String eventJson = """
                {
                    "type": "survey-unit-created",
                    "id": "123",
                    "data": {
                        "name": "Test Survey"
                    }
                }
                """;

        // when & then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isCreated());

        verify(eventService).saveEvent(any());
    }
}