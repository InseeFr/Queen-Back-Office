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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "feature.cross-environment-communication.emitter=false"
})
@DisplayName("Integration tests when EventsController is disabled")
class EventsControllerDisabledIT {

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

    @Test
    @WithMockUser
    @DisplayName("POST /api/events should return 500 when controller is disabled")
    void testAddEvent_ControllerDisabled() throws Exception {
        // given
        String eventJson = """
                {
                    "type": "test-event",
                    "id": "123"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isInternalServerError());
    }
}