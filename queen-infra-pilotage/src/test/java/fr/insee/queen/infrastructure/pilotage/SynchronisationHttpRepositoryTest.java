package fr.insee.queen.infrastructure.pilotage;

import fr.insee.queen.domain.interrogation.model.Interrogation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class SynchronisationHttpRepositoryTest {

    private SynchronisationHttpRepository synchronisationRepository;
    private final String queenUrl = "http://www.queen.com";
    private MockRestServiceServer mockServer;

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        synchronisationRepository = new SynchronisationHttpRepository(queenUrl, restTemplate);
    }

    @Test
    @DisplayName("On synchronising interrogation, return interrogation")
    void testSynchronise01() throws URISyntaxException {
        String interrogationId = "interrogation-id";
        String interrogationResponse = """
                {
                    "id": "interrogation-id",
                    "questionnaireId": "questionnaire-id",
                    "personalization": [],
                    "data": {"field": "value"},
                    "comment": {},
                    "stateData": {
                        "state": "INIT",
                        "date": 1234567890,
                        "currentPage": "current-page"
                    }
                }""";

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(queenUrl + "/api/interrogations/" + interrogationId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(interrogationResponse)
                );

        Interrogation interrogation = synchronisationRepository.synchronise(interrogationId);
        mockServer.verify();

        assertThat(interrogation).isNotNull();
        assertThat(interrogation.id()).isEqualTo("interrogation-id");
        assertThat(interrogation.questionnaireId()).isEqualTo("questionnaire-id");
    }

    @Test
    @DisplayName("On synchronising interrogation, when status not found, throw exception")
    void testSynchronise02() throws URISyntaxException {
        String interrogationId = "interrogation-id";

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(queenUrl + "/api/interrogations/" + interrogationId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> synchronisationRepository.synchronise(interrogationId))
                .isInstanceOf(RestClientException.class);
        mockServer.verify();
    }

    @Test
    @DisplayName("On synchronising interrogation, when server error, throw exception")
    void testSynchronise03() throws URISyntaxException {
        String interrogationId = "interrogation-id";

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(queenUrl + "/api/interrogations/" + interrogationId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> synchronisationRepository.synchronise(interrogationId))
                .isInstanceOf(RestClientException.class);
        mockServer.verify();
    }

    @Test
    @DisplayName("Given the server not responding, when synchronising, throw exception")
    void testSynchroniseNetworkError() throws URISyntaxException {
        String interrogationId = "interrogation-id";

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(queenUrl + "/api/interrogations/" + interrogationId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withException(new IOException("Network error")));

        assertThatThrownBy(() -> synchronisationRepository.synchronise(interrogationId))
                .isInstanceOf(RestClientException.class);
        mockServer.verify();
    }
}
