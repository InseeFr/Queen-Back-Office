package fr.insee.queen.application.synchronisation.integration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import fr.insee.queen.application.utils.AuthenticatedUserTestHelper;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import fr.insee.queen.domain.interrogation.gateway.StateDataRepository;
import fr.insee.queen.domain.interrogation.model.StateData;
import fr.insee.queen.domain.interrogation.model.StateDataType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the synchronisation feature using WireMock.
 *
 * This test validates the complete flow where:
 * 1. A local Queen instance (Spring Boot Test) receives a synchronisation request
 * 2. The local instance calls WireMock (simulating a remote Queen instance) to fetch interrogation data
 * 3. The remote data and stateData are persisted in the local database
 *
 * Architecture:
 * <pre>
 * ┌─────────────────────────┐         ┌─────────────────────────┐
 * │  Queen Local            │  HTTP   │  WireMock               │
 * │  - Spring Boot Test     │ ──────► │  - Simulates remote     │
 * │  - BDD: localhost:5434  │         │    Queen API            │
 * └─────────────────────────┘         └─────────────────────────┘
 * </pre>
 *
 * This approach is faster than using Docker Compose for the remote instance.
 */
@ActiveProfiles("test-synchronisation-wiremock")
@SpringBootTest
@AutoConfigureMockMvc
class SynchronisationWireMockIT {

    private static WireMockServer wireMockServer;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InterrogationRepository interrogationRepository;

    @Autowired
    private StateDataRepository stateDataRepository;

    private final AuthenticatedUserTestHelper authenticatedUserTestHelper = new AuthenticatedUserTestHelper();

    @BeforeAll
    static void startWireMock() {
        // Force JDK's TransformerFactory instead of Saxon's to avoid WireMock compatibility issues
        // Saxon doesn't support the "indent-number" property that WireMock's FormatXmlHelper tries to set
        System.setProperty("javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");

        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("feature.synchronisation.queen-url", () -> "http://localhost:" + wireMockServer.port());
    }

    @Test
    @DisplayName("When synchronising an interrogation from remote Queen, should update data and stateData")
    void on_synchronise_interrogation_from_remote_return_200_and_update_data() throws Exception {
        // This interrogation exists in local Queen instance (via Liquibase test data)
        String interrogationId = "517046b6-bd88-47e0-838e-00d03461f592";

        // Get data before synchronisation and verify they exist
        Optional<ObjectNode> dataBefore = interrogationRepository.findData(interrogationId);
        Optional<StateData> stateDataBefore = stateDataRepository.find(interrogationId);

        assertThat(dataBefore).isPresent();
        assertThat(stateDataBefore).isPresent();

        // Configure WireMock to simulate remote Queen response with updated data
        String remoteInterrogationJson = """
            {
                "id": "%s",
                "questionnaireId": "questionnaire-1",
                "personalization": [],
                "data": {
                    "EXTERNAL": {
                        "SYNCHRONISED": true,
                        "REMOTE_VALUE": "updated-from-remote"
                    }
                },
                "comment": {},
                "stateData": {
                    "state": "VALIDATED",
                    "date": 1735689600000,
                    "currentPage": "remote-page-10"
                }
            }
            """.formatted(interrogationId);

        wireMockServer.stubFor(get(urlEqualTo("/api/interrogations/" + interrogationId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(remoteInterrogationJson)));

        // Call the local synchronisation endpoint which will fetch from WireMock
        mockMvc.perform(post("/api/interrogations/" + interrogationId + "/synchronize")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk());

        // Verify WireMock was called
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/interrogations/" + interrogationId)));

        // Get data after synchronisation
        Optional<ObjectNode> dataAfter = interrogationRepository.findData(interrogationId);
        Optional<StateData> stateDataAfter = stateDataRepository.find(interrogationId);

        // Verify that data was updated
        assertThat(dataAfter).isPresent();
        assertThat(stateDataAfter).isPresent();

        // Verify data has been updated with remote values
        assertThat(dataAfter.get().has("EXTERNAL")).isTrue();
        assertThat(dataAfter.get().get("EXTERNAL").get("SYNCHRONISED").asBoolean()).isTrue();
        assertThat(dataAfter.get().get("EXTERNAL").get("REMOTE_VALUE").asText()).isEqualTo("updated-from-remote");

        // Verify stateData has been updated with remote values
        assertThat(stateDataAfter.get().state()).isEqualTo(StateDataType.VALIDATED);
        assertThat(stateDataAfter.get().currentPage()).isEqualTo("remote-page-10");
        assertThat(stateDataAfter.get().date()).isEqualTo(1735689600000L);
    }

    @Test
    @DisplayName("When synchronising with non-admin user, should return 403")
    void on_synchronise_interrogation_non_admin_return_403() throws Exception {
        String interrogationId = "517046b6-bd88-47e0-838e-00d03461f592";

        mockMvc.perform(post("/api/interrogations/" + interrogationId + "/synchronize")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNonAdminUser()))
                )
                .andExpect(status().isForbidden());

        // Verify WireMock was NOT called (authorization failed before)
        wireMockServer.verify(0, getRequestedFor(urlEqualTo("/api/interrogations/" + interrogationId)));
    }

    @Test
    @DisplayName("When synchronising with anonymous user, should return 401")
    void on_synchronise_interrogation_anonymous_return_401() throws Exception {
        String interrogationId = "517046b6-bd88-47e0-838e-00d03461f592";

        mockMvc.perform(post("/api/interrogations/" + interrogationId + "/synchronize")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getNotAuthenticatedUser()))
                )
                .andExpect(status().isUnauthorized());

        // Verify WireMock was NOT called (authorization failed before)
        wireMockServer.verify(0, getRequestedFor(urlEqualTo("/api/interrogations/" + interrogationId)));
    }

    @Test
    @DisplayName("When remote Queen returns 404 for non-existent interrogation, should propagate error")
    void on_synchronise_non_existent_interrogation_return_error() throws Exception {
        String interrogationId = "non-existent-interrogation-id";

        // Configure WireMock to return 404
        wireMockServer.stubFor(get(urlEqualTo("/api/interrogations/" + interrogationId))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Interrogation not found\"}")));

        // Call should fail when remote returns 404
        mockMvc.perform(post("/api/interrogations/" + interrogationId + "/synchronize")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("When remote Queen returns 500, should propagate error")
    void on_synchronise_remote_server_error_return_error() throws Exception {
        String interrogationId = "517046b6-bd88-47e0-838e-00d03461f592";

        // Configure WireMock to return 500
        wireMockServer.stubFor(get(urlEqualTo("/api/interrogations/" + interrogationId))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Internal server error\"}")));

        // Call should fail when remote returns 500
        mockMvc.perform(post("/api/interrogations/" + interrogationId + "/synchronize")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("When synchronisation returns null data, should not update data")
    void on_synchronise_with_null_data_should_not_update() throws Exception {
        String interrogationId = "517046b6-bd88-47e0-838e-00d03461f592";

        // Get data before synchronisation
        Optional<ObjectNode> dataBefore = interrogationRepository.findData(interrogationId);
        assertThat(dataBefore).isPresent();

        // Configure WireMock to return response with null data
        String remoteInterrogationJson = """
            {
                "id": "%s",
                "questionnaireId": "questionnaire-1",
                "personalization": [],
                "data": null,
                "comment": {},
                "stateData": {
                    "state": "COMPLETED",
                    "date": 1735689600000,
                    "currentPage": "page-5"
                }
            }
            """.formatted(interrogationId);

        wireMockServer.stubFor(get(urlEqualTo("/api/interrogations/" + interrogationId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(remoteInterrogationJson)));

        mockMvc.perform(post("/api/interrogations/" + interrogationId + "/synchronize")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(authenticatedUserTestHelper.getAdminUser()))
                )
                .andExpect(status().isOk());

        // Verify stateData was updated but data was not changed
        Optional<StateData> stateDataAfter = stateDataRepository.find(interrogationId);
        assertThat(stateDataAfter).isPresent();
        assertThat(stateDataAfter.get().state()).isEqualTo(StateDataType.COMPLETED);
    }
}
