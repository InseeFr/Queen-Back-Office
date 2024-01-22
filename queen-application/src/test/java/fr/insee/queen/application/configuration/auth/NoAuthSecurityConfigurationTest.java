package fr.insee.queen.application.configuration.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class NoAuthSecurityConfigurationTest {
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void init() {
        NoAuthSecurityConfiguration conf = new NoAuthSecurityConfiguration(null);
        restTemplate = conf.restTemplatePilotage();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("when using restTemplate, assure json content type is set to json")
    void testContentTypeIsIntegratedInHttpRequest() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("/")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(headerDoesNotExist("Authorization"))
                .andRespond(withStatus(HttpStatus.OK));

        restTemplate.getForObject("/", Object.class);
        mockServer.verify();
    }
}
