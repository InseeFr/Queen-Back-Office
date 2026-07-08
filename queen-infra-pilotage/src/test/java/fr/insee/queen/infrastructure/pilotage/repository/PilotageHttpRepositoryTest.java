package fr.insee.queen.infrastructure.pilotage.repository;

import fr.insee.queen.domain.pilotage.model.CollectionEnvironmentEnum;
import fr.insee.queen.domain.pilotage.model.PilotageGroup;
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
    private final String ongoingPath =  "/campaigns/%s/ongoing";
    private MockRestServiceServer mockServer;
    private final String groupId = "group-id";

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        String groupIdRegexWithAlternativeHabilitationService = "((edt)|(EDT))(\\d|\\S){1,}";

        pilotageRepository = new PilotageHttpRepository(
                pilotageUrl,
                alternativeHabilitationServiceURL,
                groupIdRegexWithAlternativeHabilitationService,
                restTemplate,
                CollectionEnvironmentEnum.WEB
        );
    }

    @Test
    @DisplayName("On retrieving groups return groups")
    void testInterviewerGroups01() throws URISyntaxException {
        String groupsResponse = """
                [
                    { "id": "group-id-1" },
                    { "id": "group-id-2" },
                    { "id": "group-id-3" }
                ]""";

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_INTERVIEWER_CAMPAIGNS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(groupsResponse)
                );

        List<PilotageGroup> groups = pilotageRepository.getInterviewerGroups();
        mockServer.verify();

        assertThat(groups).hasSize(3);
        assertThat(groups.get(0).id()).isEqualTo("group-id-1");
        assertThat(groups.get(1).id()).isEqualTo("group-id-2");
        assertThat(groups.get(2).id()).isEqualTo("group-id-3");

    }

    @Test
    @DisplayName("On retrieving groups, when status not found return empty groups")
    void testInterviewerGroups02() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_INTERVIEWER_CAMPAIGNS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        List<PilotageGroup> groups = pilotageRepository.getInterviewerGroups();
        mockServer.verify();
        assertThat(groups).isEmpty();
    }

    @Test
    @DisplayName("On retrieving groups, when error status throws Exception")
    void testInterviewerGroups03() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_INTERVIEWER_CAMPAIGNS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.getInterviewerGroups())
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("Given the server not responding, when retrieving campaings, throw exception")
    @Test
    void testInterviewerGroupsNetworkError() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + PilotageHttpRepository.API_PEARLJAM_INTERVIEWER_CAMPAIGNS)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withException(new IOException("message")));

        assertThatThrownBy(() -> pilotageRepository.getInterviewerGroups())
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On checking if group closed, return group status")
    @ParameterizedTest
    @ValueSource(strings = { "true", "false"})
    void testGroupIsClosed01(String status) throws URISyntaxException {
        String groupResponse = "{ \"ongoing\": \"" + status + "\" }";
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + ongoingPath.formatted(groupId))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(groupResponse)
                );

        boolean isClosed = pilotageRepository.isClosed(groupId);
        mockServer.verify();
        assertThat(isClosed).isEqualTo(!Boolean.parseBoolean(status));
    }

    @DisplayName("Given the server not responding, when checking if group closed, throw exception")
    @Test
    void testGroupIsClosedNetworkError() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + ongoingPath.formatted(groupId))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withException(new IOException("message")));

        assertThatThrownBy(() -> pilotageRepository.isClosed(groupId))
                .isInstanceOf(PilotageApiException.class);
    }

    @DisplayName("On checking group closed, when response is null throw exception")
    @Test
    void testGroupIsClosed02() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + ongoingPath.formatted(groupId))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.isClosed(groupId))
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On checking group closed, when check api result in error, then throw exception")
    @Test
    void testGroupIsClosed03() throws URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(pilotageUrl + ongoingPath.formatted(groupId))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        assertThatThrownBy(() -> pilotageRepository.isClosed(groupId))
                .isInstanceOf(PilotageApiException.class);
        mockServer.verify();
    }

    @DisplayName("On retrieving interrogations then return interrogations")
    @Test
    void testInterrogations01() throws URISyntaxException {
        String response = """
                [
                    { "group": "group-id1", "id": "interrogation-id1" },
                    { "group": "group-id2", "id": "interrogation-id2" },
                    { "group": "group-id3", "id": "interrogation-id3" }
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
        assertThat(interrogations.get(0).group()).isEqualTo("group-id1");
        assertThat(interrogations.get(1).group()).isEqualTo("group-id2");
        assertThat(interrogations.get(2).group()).isEqualTo("group-id3");
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
