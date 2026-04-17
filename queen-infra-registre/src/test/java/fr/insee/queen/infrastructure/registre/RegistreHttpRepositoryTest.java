package fr.insee.queen.infrastructure.registre;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.registre.model.CollectionInstrument;
import fr.insee.queen.domain.registre.model.CodeList;
import fr.insee.queen.domain.registre.service.exception.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistreHttpRepositoryTest {

    private WireMockServer wireMockServer;
    private RegistreHttpRepository registreHttpRepository;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        RestClient restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        registreHttpRepository = new RegistreHttpRepository(restClient);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    // -------------------------------------------------------------------------
    // findCollectionInstrumentByUrl
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("When finding collection instrument with valid URL, then returns collection instrument")
    void test_find_collection_instrument_success() {
        String url = "http://localhost:" + wireMockServer.port() + "/collection-instruments/SIMPSONS2020X00";
        String responseBody = """
                {
                  "id": "SIMPSONS2020X00",
                  "mode": "CAWI",
                  "content": {"id": "simpsons-questionnaire", "label": "Survey on the Simpsons tv show 2020", "pages": []},
                  "type": "JSON"
                }
                """;

        stubFor(get(urlEqualTo("/collection-instruments/SIMPSONS2020X00"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        CollectionInstrument result = registreHttpRepository.findCollectionInstrumentByUrl(url);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("SIMPSONS2020X00");
    }

    @Test
    @DisplayName("When finding collection instrument with 404, then throws CollectionInstrumentNotFoundException")
    void test_find_collection_instrument_not_found() {
        String url = "http://localhost:" + wireMockServer.port() + "/collection-instruments/UNKNOWN";

        stubFor(get(urlEqualTo("/collection-instruments/UNKNOWN"))
                .willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> registreHttpRepository.findCollectionInstrumentByUrl(url))
                .isInstanceOf(CollectionInstrumentNotFoundException.class)
                .hasMessageContaining("Collection instrument not found");
    }

    @Test
    @DisplayName("When finding collection instrument with 401, then throws RegistreAuthException")
    void test_find_collection_instrument_unauthorized() {
        String url = "http://localhost:" + wireMockServer.port() + "/collection-instruments/SIMPSONS2020X00";

        stubFor(get(urlEqualTo("/collection-instruments/SIMPSONS2020X00"))
                .willReturn(aResponse().withStatus(401)));

        assertThatThrownBy(() -> registreHttpRepository.findCollectionInstrumentByUrl(url))
                .isInstanceOf(RegistreAuthException.class)
                .hasMessageContaining("Unauthorized access");
    }

    @Test
    @DisplayName("When finding collection instrument with 403, then throws RegistreAuthException")
    void test_find_collection_instrument_forbidden() {
        String url = "http://localhost:" + wireMockServer.port() + "/collection-instruments/SIMPSONS2020X00";

        stubFor(get(urlEqualTo("/collection-instruments/SIMPSONS2020X00"))
                .willReturn(aResponse().withStatus(403)));

        assertThatThrownBy(() -> registreHttpRepository.findCollectionInstrumentByUrl(url))
                .isInstanceOf(RegistreAuthException.class)
                .hasMessageContaining("Forbidden access");
    }

    @Test
    @DisplayName("When finding collection instrument with 500, then throws RegistreException")
    void test_find_collection_instrument_server_error() {
        String url = "http://localhost:" + wireMockServer.port() + "/collection-instruments/ERROR";

        stubFor(get(urlEqualTo("/collection-instruments/ERROR"))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> registreHttpRepository.findCollectionInstrumentByUrl(url))
                .isInstanceOf(RegistreException.class)
                .hasMessageContaining("Error accessing registre API");
    }

    // -------------------------------------------------------------------------
    // findCodesListByUrl
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("When finding code lists with valid URL, then returns code lists")
    void test_find_code_lists_success() {
        String url = "http://localhost:" + wireMockServer.port() + "/collection-instruments/SIMPSONS2020X00/codes-lists";
        String responseBody = """
                [
                    {"id": "L_PAYSNAIS", "url": "/codes-lists/L_PAYSNAIS"},
                    {"id": "L_DEPNAIS",  "url": "/codes-lists/L_DEPNAIS"}
                ]
                """;

        stubFor(get(urlEqualTo("/collection-instruments/SIMPSONS2020X00/codes-lists"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        List<CodeList> result = registreHttpRepository.findCodesListByUrl(url);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo("L_PAYSNAIS");
        assertThat(result.get(0).url()).isEqualTo("/codes-lists/L_PAYSNAIS");
        assertThat(result.get(1).id()).isEqualTo("L_DEPNAIS");
        assertThat(result.get(1).url()).isEqualTo("/codes-lists/L_DEPNAIS");
    }

    @Test
    @DisplayName("When finding code lists with 404, then throws CodeListNotFoundException")
    void test_find_code_lists_not_found() {
        String url = "http://localhost:" + wireMockServer.port() + "/collection-instruments/UNKNOWN/codes-lists";

        stubFor(get(urlEqualTo("/collection-instruments/UNKNOWN/codes-lists"))
                .willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> registreHttpRepository.findCodesListByUrl(url))
                .isInstanceOf(CodeListNotFoundException.class)
                .hasMessageContaining("Code list not found");
    }

    @Test
    @DisplayName("When finding code lists with 401, then throws RegistreAuthException")
    void test_find_code_lists_unauthorized() {
        String url = "http://localhost:" + wireMockServer.port() + "/collection-instruments/SIMPSONS2020X00/codes-lists";

        stubFor(get(urlEqualTo("/collection-instruments/SIMPSONS2020X00/codes-lists"))
                .willReturn(aResponse().withStatus(401)));

        assertThatThrownBy(() -> registreHttpRepository.findCodesListByUrl(url))
                .isInstanceOf(RegistreAuthException.class)
                .hasMessageContaining("Unauthorized access");
    }

    @Test
    @DisplayName("When finding code lists with 500, then throws RegistreException")
    void test_find_code_lists_server_error() {
        String url = "http://localhost:" + wireMockServer.port() + "/collection-instruments/ERROR/codes-lists";

        stubFor(get(urlEqualTo("/collection-instruments/ERROR/codes-lists"))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> registreHttpRepository.findCodesListByUrl(url))
                .isInstanceOf(RegistreException.class)
                .hasMessageContaining("Error accessing registre API");
    }

    // -------------------------------------------------------------------------
    // findModalitiesByCodeUrl
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("When finding modalities with valid URL, then returns modalities")
    void test_find_modalities_success() {
        String url = "http://localhost:" + wireMockServer.port() + "/codes-lists/L_PAYSNAIS";
        String responseBody = """
                [
                    {"id": "001", "label": "FRANCAISE"},
                    {"id": "002", "label": "BELGE"},
                    {"id": "003", "label": "NEERLANDAISE, HOLLANDAISE"}
                ]
                """;

        stubFor(get(urlEqualTo("/codes-lists/L_PAYSNAIS"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ArrayNode result = registreHttpRepository.findModalitiesByCodeUrl(url);

        assertThat(result).isNotNull().hasSize(3);
    }

    @Test
    @DisplayName("When finding modalities with 404, then throws CodeModalitiesNotFoundException")
    void test_find_modalities_not_found() {
        String url = "http://localhost:" + wireMockServer.port() + "/codes-lists/UNKNOWN";

        stubFor(get(urlEqualTo("/codes-lists/UNKNOWN"))
                .willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> registreHttpRepository.findModalitiesByCodeUrl(url))
                .isInstanceOf(CodeModalitiesNotFoundException.class)
                .hasMessageContaining("Code modalities not found");
    }

    @Test
    @DisplayName("When finding modalities with 401, then throws RegistreAuthException")
    void test_find_modalities_unauthorized() {
        String url = "http://localhost:" + wireMockServer.port() + "/codes-lists/L_PAYSNAIS";

        stubFor(get(urlEqualTo("/codes-lists/L_PAYSNAIS"))
                .willReturn(aResponse().withStatus(401)));

        assertThatThrownBy(() -> registreHttpRepository.findModalitiesByCodeUrl(url))
                .isInstanceOf(RegistreAuthException.class)
                .hasMessageContaining("Unauthorized access");
    }

    @Test
    @DisplayName("When finding modalities with 500, then throws RegistreException")
    void test_find_modalities_server_error() {
        String url = "http://localhost:" + wireMockServer.port() + "/codes-lists/ERROR";

        stubFor(get(urlEqualTo("/codes-lists/ERROR"))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> registreHttpRepository.findModalitiesByCodeUrl(url))
                .isInstanceOf(RegistreException.class)
                .hasMessageContaining("Error accessing registre API");
    }
}