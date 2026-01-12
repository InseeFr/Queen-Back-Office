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


class PilotageHttpRepositoryWebModeTest {

    private PilotageHttpRepository repository;
    private MockRestServiceServer mockServer;

    private final String pilotageUrl = "http://www.pilotage.com";
    private final String alternativeUrl = "http://www.pilotage-alternative.com";
    private final String regex = "((edt)|(EDT))(\\d|\\S){1,}";
    private final String campaignId = "campaign-id";

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        repository = new PilotageHttpRepository(
                pilotageUrl,
                alternativeUrl,
                regex,
                restTemplate
        );

        ReflectionTestUtils.setField(repository, "inputMode", InputModeEnum.WEB);
    }

    @ParameterizedTest
    @ValueSource(strings = { "true", "false" })
    void hasHabilitation_web_ok(String status) {
        InterrogationSummary interrogation = interrogation(campaignId);
        PilotageRole role = PilotageRole.INTERVIEWER;

        mockServer.expect(requestTo(pilotageUrl + PilotageHttpRepository.API_HABILITATION +
                        "?id=" + interrogation.id() +
                        "&role=" + role.getExpectedRole() +
                        "&idep=idep"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"habilitated\":\"" + status + "\"}"));

        boolean result = repository.hasHabilitation(interrogation, role, "idep");

        mockServer.verify();
        assertThat(result).isEqualTo(Boolean.parseBoolean(status));
    }

    @Test
    void hasHabilitation_web_alternative_url() {
        String edtCampaign = "EDT-2024";
        InterrogationSummary interrogation = interrogation(edtCampaign);

        mockServer.expect(requestTo(alternativeUrl +
                        "?id=" + interrogation.id() +
                        "&role=" + PilotageRole.REVIEWER.getExpectedRole() +
                        "&campaign=" + edtCampaign +
                        "&idep=idep"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"habilitated\":\"true\"}"));

        boolean result = repository.hasHabilitation(interrogation, PilotageRole.REVIEWER, "idep");

        mockServer.verify();
        assertThat(result).isTrue();
    }

    @Test
    void hasHabilitation_web_body_null_throw_exception() {
        InterrogationSummary interrogation = interrogation(campaignId);

        mockServer.expect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        assertThatThrownBy(() ->
                repository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep"))
                .isInstanceOf(PilotageApiException.class);
    }

    @Test
    void hasHabilitation_web_unauthorized_returns_false() {
        InterrogationSummary interrogation = interrogation(campaignId);

        mockServer.expect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        boolean result = repository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep");

        mockServer.verify();
        assertThat(result).isFalse();
    }

    @Test
    void hasHabilitation_web_server_error_throw_exception() {
        InterrogationSummary interrogation = interrogation(campaignId);

        mockServer.expect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() ->
                repository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep"))
                .isInstanceOf(PilotageApiException.class);
    }

    @Test
    void hasHabilitation_web_network_error_throw_exception() {
        InterrogationSummary interrogation = interrogation(campaignId);

        mockServer.expect(method(HttpMethod.GET))
                .andRespond(withException(new IOException("network")));

        assertThatThrownBy(() ->
                repository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep"))
                .isInstanceOf(PilotageApiException.class);
    }

    private InterrogationSummary interrogation(String campaignId) {
        return new InterrogationSummary(
                "interrogation-id",
                "su-id",
                "q-id",
                new CampaignSummary(campaignId, "label", CampaignSensitivity.NORMAL)
        );
    }
}
