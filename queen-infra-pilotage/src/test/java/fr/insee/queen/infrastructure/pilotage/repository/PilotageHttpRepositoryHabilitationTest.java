package fr.insee.queen.infrastructure.pilotage.repository;

import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.pilotage.model.InputModeEnum;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.infrastructure.pilotage.PilotageHttpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class PilotageHttpRepositoryHabilitationTest {

    private PilotageHttpRepository pilotageRepository;
    private final String pilotageUrl = "http://www.pilotage.com";
    private final String alternativeHabilitationServiceURL = "http://www.pilotage-alternative.com";
    private MockRestServiceServer mockServer;
    private final String campaignId = "campaign-id";

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
                InputModeEnum.WEB
        );
    }

    @DisplayName("On checking habilitation, return habilitation")
    @ParameterizedTest
    @ValueSource(strings = { "true", "false"})
    void testHabilitation01(String status) {
        String interrogationId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01";
        String idep = "idep";
        PilotageRole role = PilotageRole.INTERVIEWER;
        InterrogationSummary interrogation = new InterrogationSummary(interrogationId, "su-id", "questionnaire-id", new CampaignSummary(campaignId, "label", CampaignSensitivity.NORMAL));

        String habilitationResponse = "{ \"habilitated\": \"" + status + "\" }";
        mockServer.expect(request ->
                        assertThat(pilotageUrl).isEqualTo(request.getURI().getScheme() + "://" + request.getURI().getHost()))
                .andExpect(request ->
                        assertThat(PilotageHttpRepository.API_HABILITATION).isEqualTo(request.getURI().getPath()))
                .andExpect(queryParam("id", interrogationId))
                .andExpect(queryParam("role", role.getExpectedRole()))
                .andExpect(queryParam("idep", idep))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(habilitationResponse)
                );

        boolean hasHabilitation = pilotageRepository.hasHabilitation(interrogation, role, idep);
        mockServer.verify();
        assertThat(hasHabilitation).isEqualTo(Boolean.parseBoolean(status));
    }

    @DisplayName("On checking habilitation, when regex for alternate pilotage url match, return habilitation from alternate pilotage url")
    @Test
    void testHabilitation02() {
        String interrogationId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01";
        String matchedRegexCampaignId = "EDT-campaign-id";
        String idep = "idep";
        PilotageRole role = PilotageRole.REVIEWER;
        InterrogationSummary interrogation = new InterrogationSummary(interrogationId, "su-id", "q-id", new CampaignSummary(matchedRegexCampaignId, "label", CampaignSensitivity.NORMAL));

        mockServer.expect(request ->
                        assertThat(alternativeHabilitationServiceURL)
                                .isEqualTo(request.getURI().getScheme() + "://" + request.getURI().getHost() + request.getURI().getPath()))
                .andExpect(queryParam("id", interrogationId))
                .andExpect(queryParam("role", role.getExpectedRole()))
                .andExpect(queryParam("campaign", matchedRegexCampaignId))
                .andExpect(queryParam("idep", idep))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{ \"habilitated\": \"true\" }"));

        boolean hasHabilitation = pilotageRepository.hasHabilitation(interrogation, role, idep);
        mockServer.verify();
        assertThat(hasHabilitation).isTrue();
    }

    @DisplayName("On checking habilitation, when http response body is empty throw exception")
    @Test
    void testHabilitation03() {
        String interrogationId = "id-1";
        InterrogationSummary interrogation = new InterrogationSummary(interrogationId, "su-id", "q-id", new CampaignSummary(campaignId, "label", CampaignSensitivity.NORMAL));

        mockServer.expect(anything())
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> pilotageRepository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep"))
                .isInstanceOf(PilotageApiException.class);
    }

    @DisplayName("On checking habilitation, when http response status is unauthorized, habilitation is false")
    @Test
    void testHabilitation04() {
        InterrogationSummary interrogation = new InterrogationSummary("id-1", "su-id", "q-id", new CampaignSummary(campaignId, "label", CampaignSensitivity.NORMAL));

        mockServer.expect(anything())
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON));

        boolean hasHabilitation = pilotageRepository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep");
        mockServer.verify();
        assertThat(hasHabilitation).isFalse();
    }

    @DisplayName("On checking habilitation, when http response status is error, throw exception")
    @Test
    void testHabilitation05() {
        InterrogationSummary interrogation = new InterrogationSummary("id-1", "su-id", "q-id", new CampaignSummary(campaignId, "label", CampaignSensitivity.NORMAL));

        mockServer.expect(anything())
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> pilotageRepository.hasHabilitation(interrogation, PilotageRole.INTERVIEWER, "idep"))
                .isInstanceOf(PilotageApiException.class);
    }
}