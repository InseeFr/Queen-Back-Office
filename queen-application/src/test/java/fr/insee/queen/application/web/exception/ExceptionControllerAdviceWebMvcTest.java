package fr.insee.queen.application.web.exception;

import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExceptionControllerAdviceWebMvcTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new DummyController())
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("An exception thrown by a controller is intercepted by the advice and serialized as a ProblemDetail")
    void controller_exception_is_mapped_by_advice() throws Exception {
        // Given a controller wired with the advice (see setUp), throwing EntityNotFoundException

        // When the endpoint is hit
        // Then the advice converts the exception into a 404 ProblemDetail
        mockMvc.perform(get("/dummy/throw-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("dummy entity not found"));
    }

    @RestController
    @RequestMapping("/dummy")
    static class DummyController {
        @GetMapping("/throw-not-found")
        public void throwEntityNotFound() {
            throw new EntityNotFoundException("dummy entity not found");
        }
    }
}
