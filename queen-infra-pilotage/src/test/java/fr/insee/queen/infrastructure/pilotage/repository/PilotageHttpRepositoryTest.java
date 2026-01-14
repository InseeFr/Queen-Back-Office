package fr.insee.queen.infrastructure.pilotage.repository;

import fr.insee.queen.domain.pilotage.model.InputModeEnum;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.model.PilotageInterrogation;
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
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class PilotageHttpRepositoryTest {
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

    @Test
    @DisplayName("On retrieving campaigns return campaigns")
    void testInterviewerCampaigns01() throws URISyntaxException {
        String campaignsResponse = """
                [
                    { "id": "campaign-id-1" },
                    { "id": "campaign-id-2" },
                    { "id": "campaign-id-3" }
                ]""";

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_INTERVIEWER_CAMPAIGNS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(campaignsResponse)
                );

        List<PilotageCampaign> campaigns = pilotageRepository.getInterviewerCampaigns();
        mockServer.verify();

        assertThat(campaigns).hasSize(3);
        assertThat(campaigns.get(0).id()).isEqualTo("campaign-id-1");
        assertThat(campaigns.get(1).id()).isEqualTo("campaign-id-2");
        assertThat(campaigns.get(2).id()).isEqualTo("campaign-id-3");

    }

    @Test
    @DisplayName("On retrieving campaigns, when status not found return empty campaigns")
    void testInterviewerCampaigns02() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_INTERVIEWER_CAMPAIGNS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        List<PilotageCampaign> campaigns = pilotageRepository.getInterviewerCampaigns();
        mockServer.verify();
        assertThat(campaigns).isEmpty();
    }

    @Test
    @DisplayName("On retrieving campaigns, when error status throws Exception")
    void testInterviewerCampaigns03() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_INTERVIEWER_CAMPAIGNS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.getInterviewerCampaigns())
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("Given the server not responding, when retrieving campaings, throw exception")
    @Test
    void testInterviewerCampaignsNetworkError() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_INTERVIEWER_CAMPAIGNS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withException(new IOException("message")));

        assertThatThrownBy(() -> pilotageRepository.getInterviewerCampaigns())
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On checking if campaign closed, return campaign status")
    @ParameterizedTest
    @ValueSource(strings = { "true", "false"})
    void testCampaignIsClosed01(String status) throws URISyntaxException {
        String campaignResponse = "{ \"ongoing\": \"" + status + "\" }";
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_CAMPAIGNS.formatted(campaignId))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(campaignResponse)
                );

        boolean isClosed = pilotageRepository.isClosed(campaignId);
        mockServer.verify();
        assertThat(isClosed).isEqualTo(!Boolean.parseBoolean(status));
    }

    @DisplayName("Given the server not responding, when checking if campaign closed, throw exception")
    @Test
    void testCampaignIsClosedNetworkError() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_CAMPAIGNS.formatted(campaignId))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withException(new IOException("message")));

        assertThatThrownBy(() -> pilotageRepository.isClosed(campaignId))
                .isInstanceOf(PilotageApiException.class);
    }

    @DisplayName("On checking campaign closed, when response is null throw exception")
    @Test
    void testCampaignIsClosed02() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_CAMPAIGNS.formatted(campaignId))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.isClosed(campaignId))
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On checking campaign closed, when check api result in error, then throw exception")
    @Test
    void testCampaignIsClosed03() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_CAMPAIGNS.formatted(campaignId))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.isClosed(campaignId))
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On retrieving interrogations then return interrogations")
    @Test
    void testInterrogations01() throws URISyntaxException {
        String response = """
                [
                    { "campaign": "campaign-id1", "id": "interrogation-id1" },
                    { "campaign": "campaign-id2", "id": "interrogation-id2" },
                    { "campaign": "campaign-id3", "id": "interrogation-id3" }
                ]""";
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_SURVEYUNITS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response)
                );

        List<PilotageInterrogation> interrogations = pilotageRepository.getInterrogations();
        assertThat(interrogations).hasSize(3);
        assertThat(interrogations.get(0).campaign()).isEqualTo("campaign-id1");
        assertThat(interrogations.get(1).campaign()).isEqualTo("campaign-id2");
        assertThat(interrogations.get(2).campaign()).isEqualTo("campaign-id3");
        assertThat(interrogations.get(0).id()).isEqualTo("interrogation-id1");
        assertThat(interrogations.get(1).id()).isEqualTo("interrogation-id2");
        assertThat(interrogations.get(2).id()).isEqualTo("interrogation-id3");
        mockServer.verify();
    }

    @DisplayName("On retrieving interrogations when http error throw exception")
    @Test
    void testInterrogations02() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_SURVEYUNITS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.getInterrogations())
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On retrieving interrogations when status 404 return empty list")
    @Test
    void testInterrogations03() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_SURVEYUNITS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        List<PilotageInterrogation> interrogations = pilotageRepository.getInterrogations();
        assertThat(interrogations).isEmpty();
        mockServer.verify();
    }

    @DisplayName("Given the server not responding, when retrieving interrogations, throw exception")
    @Test
    void testInterrogationsNetworkError() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_SURVEYUNITS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withException(new IOException("message")));

        assertThatThrownBy(() -> pilotageRepository.getInterrogations())
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }
}
