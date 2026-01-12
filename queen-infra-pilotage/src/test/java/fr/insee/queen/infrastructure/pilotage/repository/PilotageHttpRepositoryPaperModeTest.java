package fr.insee.queen.infrastructure.pilotage.repository;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.pilotage.model.InputModeEnum;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.infrastructure.pilotage.PilotageHttpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class PilotageHttpRepositoryPaperModeTest {

    private PilotageHttpRepository repository;
    private MockRestServiceServer mockServer;

    private final String pilotageUrl = "http://www.pilotage.com";

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        repository = new PilotageHttpRepository(
                pilotageUrl,
                "http://unused",
                "unused",
                restTemplate
        );

        ReflectionTestUtils.setField(repository, "inputMode", InputModeEnum.PAPER);
    }

    @ParameterizedTest
    @ValueSource(strings = { "true", "false" })
    void hasHabilitation_paper_ok(String status) {
        InterrogationSummary interrogation = interrogation();

        mockServer.expect(requestTo(pilotageUrl +
                        "/api/permissions/check?id=" + interrogation.id() +
                        "&permission=INTERROGATION_PAPER_DATA_EDIT"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(status));

        boolean result = repository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep");

        mockServer.verify();
        assertThat(result).isEqualTo(Boolean.parseBoolean(status));
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 403})
    void hasHabilitation_paper_unauthorized_returns_false(int status) {
        InterrogationSummary interrogation = interrogation();

        mockServer.expect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.valueOf(status)));

        boolean result = repository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep");

        mockServer.verify();
        assertThat(result).isFalse();
    }

    @Test
    void hasHabilitation_paper_server_error_throw_exception() {
        InterrogationSummary interrogation = interrogation();

        mockServer.expect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() ->
                repository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep"))
                .isInstanceOf(PilotageApiException.class);
    }

    @Test
    void hasHabilitation_paper_network_error_throw_exception() {
        InterrogationSummary interrogation = interrogation();

        mockServer.expect(method(HttpMethod.GET))
                .andRespond(withException(new IOException("network")));

        assertThatThrownBy(() ->
                repository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep"))
                .isInstanceOf(PilotageApiException.class);
    }

    private InterrogationSummary interrogation() {
        return new InterrogationSummary(
                "interrogation-id",
                "su-id",
                "q-id",
                new CampaignSummary("campaign-id", "label", CampaignSensitivity.NORMAL)
        );
    }

}
