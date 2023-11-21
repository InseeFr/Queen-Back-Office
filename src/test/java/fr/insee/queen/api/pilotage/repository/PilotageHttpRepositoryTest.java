package fr.insee.queen.api.pilotage.repository;

import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.pilotage.service.model.PilotageSurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class PilotageHttpRepositoryTest {
    private PilotageHttpRepository pilotageRepository;
    private final String pilotageUrl = "http://www.pilotage.com";
    private final String alternativeHabilitationServiceURL = "http://www.pilotage-alternative.com";
    private MockRestServiceServer mockServer;
    private final String accessToken = "access-token";
    private final String campaignId = "campaign-id";

    @BeforeEach
    public void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        String campaignIdRegexWithAlternativeHabilitationService = "((edt)|(EDT))(\\d|\\S){1,}";
        pilotageRepository = new PilotageHttpRepository(pilotageUrl, alternativeHabilitationServiceURL, campaignIdRegexWithAlternativeHabilitationService, restTemplate);
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(campaignsResponse)
                );

        List<PilotageCampaign> campaigns = pilotageRepository.getInterviewerCampaigns(accessToken);
        mockServer.verify();

        assertThat(campaigns).hasSize(3);
        assertThat(campaigns.get(0).getId()).isEqualTo("campaign-id-1");
        assertThat(campaigns.get(1).getId()).isEqualTo("campaign-id-2");
        assertThat(campaigns.get(2).getId()).isEqualTo("campaign-id-3");

    }

    @Test
    @DisplayName("On retrieving campaigns, when status not found return empty campaigns")
    void testInterviewerCampaigns02() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_INTERVIEWER_CAMPAIGNS)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        List<PilotageCampaign> campaigns = pilotageRepository.getInterviewerCampaigns(accessToken);
        mockServer.verify();
        assertThat(campaigns).isEmpty();
    }

    @Test
    @DisplayName("On retrieving campaigns, when error status throws Exception")
    void testInterviewerCampaigns03() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_INTERVIEWER_CAMPAIGNS)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.getInterviewerCampaigns(accessToken))
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(campaignResponse)
                );

        boolean isClosed = pilotageRepository.isClosed(campaignId, accessToken);
        mockServer.verify();
        assertThat(isClosed).isEqualTo(!Boolean.parseBoolean(status));
    }

    @DisplayName("On checking campaign closed, when response is null throw exception")
    @Test
    void testCampaignIsClosed02() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_CAMPAIGNS.formatted(campaignId))))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.isClosed(campaignId, accessToken))
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On checking campaign closed, when check api result in error, then throw exception")
    @Test
    void testCampaignIsClosed03() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_CAMPAIGNS.formatted(campaignId))))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.isClosed(campaignId, accessToken))
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On retrieving survey units then return survey units")
    @Test
    void testSurveyUnits01() throws URISyntaxException {
        String response = """
                [
                    { "campaign": "campaign-id1", "id": "survey-unit-id1" },
                    { "campaign": "campaign-id2", "id": "survey-unit-id2" },
                    { "campaign": "campaign-id3", "id": "survey-unit-id3" }
                ]""";
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_SURVEYUNITS)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response)
                );

        List<PilotageSurveyUnit> surveyUnits = pilotageRepository.getSurveyUnits(accessToken);
        assertThat(surveyUnits).hasSize(3);
        assertThat(surveyUnits.get(0).getCampaign()).isEqualTo("campaign-id1");
        assertThat(surveyUnits.get(1).getCampaign()).isEqualTo("campaign-id2");
        assertThat(surveyUnits.get(2).getCampaign()).isEqualTo("campaign-id3");
        assertThat(surveyUnits.get(0).getId()).isEqualTo("survey-unit-id1");
        assertThat(surveyUnits.get(1).getId()).isEqualTo("survey-unit-id2");
        assertThat(surveyUnits.get(2).getId()).isEqualTo("survey-unit-id3");
        mockServer.verify();
    }

    @DisplayName("On retrieving survey units when http error throw exception")
    @Test
    void testSurveyUnits02() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_SURVEYUNITS)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.getSurveyUnits(accessToken))
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On retrieving survey units when status 404 return empty list")
    @Test
    void testSurveyUnits03() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_SURVEYUNITS)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        List<PilotageSurveyUnit> surveyUnits = pilotageRepository.getSurveyUnits(accessToken);
        assertThat(surveyUnits).isEmpty();
        mockServer.verify();
    }

    @DisplayName("On checking habilitation, return habilitation")
    @ParameterizedTest
    @ValueSource(strings = { "true", "false"})
    void testHabilitation01(String status) {
        String idSu = "id-su";
        String idep = "idep";
        PilotageRole role = PilotageRole.INTERVIEWER;
        SurveyUnitSummary surveyUnit = new SurveyUnitSummary(idSu, "questionnaire-id", campaignId);

        String habilitationResponse = "{ \"habilitated\": \"" + status + "\" }";
        mockServer.expect(request ->
                        assertThat(pilotageUrl).isEqualTo(request.getURI().getScheme() + "://" + request.getURI().getHost()))
                .andExpect(request ->
                        assertThat(PilotageHttpRepository.API_HABILITATION).isEqualTo(request.getURI().getPath()))
                .andExpect(queryParam("id", idSu))
                .andExpect(queryParam("role", role.getExpectedRole()))
                .andExpect(queryParam("campaign", campaignId))
                .andExpect(queryParam("idep", idep))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(habilitationResponse)
                );

        boolean hasHabilitation = pilotageRepository.hasHabilitation(surveyUnit, role, idep, accessToken);
        mockServer.verify();
        assertThat(hasHabilitation).isEqualTo(Boolean.parseBoolean(status));
    }

    @DisplayName("On checking habilitation, when regex for alternate pilotage url match, return habilitation from alternate pilotage url")
    @Test
    void testHabilitation02() {
        String idSu = "id-su";
        String idep = "idep";
        String matchedRegexCampaignId = "EDT-campaign-id";
        PilotageRole role = PilotageRole.REVIEWER;
        SurveyUnitSummary surveyUnit = new SurveyUnitSummary(idSu, "questionnaire-id", matchedRegexCampaignId);

        String habilitationResponse = "{ \"habilitated\": \"true\" }";
        mockServer.expect(request ->
                        assertThat(alternativeHabilitationServiceURL)
                                .isEqualTo(request.getURI().getScheme() + "://" + request.getURI().getHost() + request.getURI().getPath()))
                .andExpect(queryParam("id", idSu))
                .andExpect(queryParam("role", role.getExpectedRole()))
                .andExpect(queryParam("campaign", matchedRegexCampaignId))
                .andExpect(queryParam("idep", idep))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(habilitationResponse)
                );

        boolean hasHabilitation = pilotageRepository.hasHabilitation(surveyUnit, role, idep, accessToken);
        mockServer.verify();
        assertThat(hasHabilitation).isTrue();
    }

    @DisplayName("On checking habilitation, when http response body is empty throw exception")
    @Test
    void testHabilitation03() {
        String idSu = "id-su";
        String idep = "idep";
        PilotageRole role = PilotageRole.INTERVIEWER;
        SurveyUnitSummary surveyUnit = new SurveyUnitSummary(idSu, "questionnaire-id", campaignId);

        mockServer.expect(request ->
                        assertThat(pilotageUrl).isEqualTo(request.getURI().getScheme() + "://" + request.getURI().getHost()))
                .andExpect(request ->
                        assertThat(PilotageHttpRepository.API_HABILITATION).isEqualTo(request.getURI().getPath()))
                .andExpect(queryParam("id", idSu))
                .andExpect(queryParam("role", role.getExpectedRole()))
                .andExpect(queryParam("campaign", campaignId))
                .andExpect(queryParam("idep", idep))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.hasHabilitation(surveyUnit, role, idep, accessToken))
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On checking habilitation, when http response status is unauthorized, habilitation is false")
    @Test
    void testHabilitation04() {
        String idSu = "id-su";
        String idep = "idep";
        PilotageRole role = PilotageRole.INTERVIEWER;
        SurveyUnitSummary surveyUnit = new SurveyUnitSummary(idSu, "questionnaire-id", campaignId);

        mockServer.expect(request ->
                        assertThat(pilotageUrl).isEqualTo(request.getURI().getScheme() + "://" + request.getURI().getHost()))
                .andExpect(request ->
                        assertThat(PilotageHttpRepository.API_HABILITATION).isEqualTo(request.getURI().getPath()))
                .andExpect(queryParam("id", idSu))
                .andExpect(queryParam("role", role.getExpectedRole()))
                .andExpect(queryParam("campaign", campaignId))
                .andExpect(queryParam("idep", idep))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        boolean hasHabilitation = pilotageRepository.hasHabilitation(surveyUnit, role, idep, accessToken);
        mockServer.verify();
        assertThat(hasHabilitation).isFalse();
    }

    @DisplayName("On checking habilitation, when http response status is an error status (other than 401), throw exception")
    @Test
    void testHabilitation05() {
        String idSu = "id-su";
        String idep = "idep";
        PilotageRole role = PilotageRole.INTERVIEWER;
        SurveyUnitSummary surveyUnit = new SurveyUnitSummary(idSu, "questionnaire-id", campaignId);

        mockServer.expect(request ->
                        assertThat(pilotageUrl).isEqualTo(request.getURI().getScheme() + "://" + request.getURI().getHost()))
                .andExpect(request ->
                        assertThat(PilotageHttpRepository.API_HABILITATION).isEqualTo(request.getURI().getPath()))
                .andExpect(queryParam("id", idSu))
                .andExpect(queryParam("role", role.getExpectedRole()))
                .andExpect(queryParam("campaign", campaignId))
                .andExpect(queryParam("idep", idep))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header("Authorization", "Bearer "+ accessToken))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.hasHabilitation(surveyUnit, role, idep, accessToken))
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }
}
