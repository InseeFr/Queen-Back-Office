package fr.insee.queen.infrastructure.pilotage.repository;

import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.pilotage.model.CollectionEnvironmentEnum;
import fr.insee.queen.domain.pilotage.model.PermissionEnum;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.infrastructure.pilotage.PilotageHttpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class PilotageHttpRepositoryPermissionTest {

    private PilotageHttpRepository pilotageRepository;
    private final String pilotageUrl = "http://www.pilotage.com";
    private final String alternativeHabilitationServiceURL = "http://www.pilotage-alternative.com";
    private MockRestServiceServer mockServer;

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        String campaignIdRegexWithAlternativeHabilitationService = "((edt)|(EDT))(\\d|\\S){1,}";

        pilotageRepository = new PilotageHttpRepository(
                pilotageUrl,
                alternativeHabilitationServiceURL,
                campaignIdRegexWithAlternativeHabilitationService,
                restTemplate,
                CollectionEnvironmentEnum.WEB
        );
    }

    @Test
    @DisplayName("Should return true when Permission API returns true")
    void testHasPermission_Granted() throws URISyntaxException {
        String interrogationId = "interrogation-id";
        PermissionEnum permission = PermissionEnum.values()[0];

        mockServer.expect(requestTo(new URI(pilotageUrl + "/api/permissions/check?id=" + interrogationId + "&permission=" + permission)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("true"));

        InterrogationSummary summary = new InterrogationSummary(interrogationId, "su-id", "q-id", null);
        boolean result = pilotageRepository.hasPermission(summary, permission);

        mockServer.verify();
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when Permission API returns false")
    void testHasPermission_DeniedByBoolean() throws URISyntaxException {
        String interrogationId = "interrogation-id";
        PermissionEnum permission = PermissionEnum.values()[0];

        mockServer.expect(requestTo(new URI(pilotageUrl + "/api/permissions/check?id=" + interrogationId + "&permission=" + permission)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("false"));

        InterrogationSummary summary = new InterrogationSummary(interrogationId, "su-id", "q-id", null);
        boolean result = pilotageRepository.hasPermission(summary, permission);

        mockServer.verify();
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when Permission API returns 401 Unauthorized")
    void testHasPermission_Unauthorized() throws URISyntaxException {
        String interrogationId = "interrogation-id";
        PermissionEnum permission = PermissionEnum.values()[0];

        mockServer.expect(requestTo(new URI(pilotageUrl + "/api/permissions/check?id=" + interrogationId + "&permission=" + permission)))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        InterrogationSummary summary = new InterrogationSummary(interrogationId, "su-id", "q-id", null);
        boolean result = pilotageRepository.hasPermission(summary, permission);

        mockServer.verify();
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when Permission API returns 403 Forbidden")
    void testHasPermission_Forbidden() throws URISyntaxException {
        String interrogationId = "interrogation-id";
        PermissionEnum permission = PermissionEnum.values()[0];

        mockServer.expect(requestTo(new URI(pilotageUrl + "/api/permissions/check?id=" + interrogationId + "&permission=" + permission)))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        InterrogationSummary summary = new InterrogationSummary(interrogationId, "su-id", "q-id", null);
        boolean result = pilotageRepository.hasPermission(summary, permission);

        mockServer.verify();
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should throw PilotageApiException when Permission API returns 500")
    void testHasPermission_ServerError() throws URISyntaxException {
        String interrogationId = "interrogation-id";
        PermissionEnum permission = PermissionEnum.values()[0];

        mockServer.expect(requestTo(new URI(pilotageUrl + "/api/permissions/check?id=" + interrogationId + "&permission=" + permission)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        InterrogationSummary summary = new InterrogationSummary(interrogationId, "su-id", "q-id", null);

        assertThatThrownBy(() -> pilotageRepository.hasPermission(summary, permission))
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }
}