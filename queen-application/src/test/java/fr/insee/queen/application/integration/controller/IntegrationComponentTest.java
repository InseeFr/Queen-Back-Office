package fr.insee.queen.application.integration.controller;

import fr.insee.queen.application.configuration.properties.ApplicationProperties;
import fr.insee.queen.application.configuration.properties.AuthEnumProperties;
import fr.insee.queen.application.integration.controller.builder.dummy.CampaignFakeBuilder;
import fr.insee.queen.application.integration.controller.builder.dummy.NomenclatureFakeBuilder;
import fr.insee.queen.application.integration.controller.builder.dummy.QuestionnaireFakeBuilder;
import fr.insee.queen.application.integration.component.IntegrationComponent;
import fr.insee.queen.application.integration.component.exception.IntegrationComponentException;
import fr.insee.queen.application.integration.dto.output.IntegrationResultsDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class IntegrationComponentTest {

    private IntegrationComponent integrationComponent;
    private QuestionnaireFakeBuilder questionnaireBuilder;
    private NomenclatureFakeBuilder nomenclatureBuilder;
    private CampaignFakeBuilder campaignBuilder;

    @BeforeEach
    void init() {
        questionnaireBuilder = new QuestionnaireFakeBuilder();
        nomenclatureBuilder = new NomenclatureFakeBuilder();
        campaignBuilder = new CampaignFakeBuilder();
        ApplicationProperties applicationProperties = new ApplicationProperties(null, null, null, null, null, System.getProperty("java.io.tmpdir"), AuthEnumProperties.NOAUTH);
        integrationComponent = new IntegrationComponent(nomenclatureBuilder, campaignBuilder, questionnaireBuilder, applicationProperties);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("on integration, when file is not a zip, throw exception")
    void integrate01(boolean isXmlIntegration) {
        MultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.APPLICATION_JSON_VALUE, "Hello, World!".getBytes()
        );
        assertThatThrownBy(() -> integrationComponent.integrateContext(uploadedFile, isXmlIntegration)).isInstanceOf(IntegrationComponentException.class);
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on integration, return an integration result list")
    void integrate02(String path, boolean isXmlIntegration) throws IOException {
        InputStream zipInputStream = getClass().getClassLoader().getResourceAsStream("data/integration" + path + "/integration-component.zip");
        MultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.APPLICATION_JSON_VALUE, zipInputStream);
        IntegrationResultsDto result = integrationComponent.integrateContext(uploadedFile, isXmlIntegration);
        assertThat(result.getCampaign()).isEqualTo(campaignBuilder.getResultSuccess());
        assertThat(result.getNomenclatures()).isEqualTo(nomenclatureBuilder.getResults());
        assertThat(result.getQuestionnaireModels()).isEqualTo(questionnaireBuilder.getResults());
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("when integrating campaign result in errors, then do not process questionnaires")
    void integrate03(String path, boolean isXmlIntegration) throws IOException {
        campaignBuilder.setResultIsInErrorState(true);
        InputStream zipInputStream = getClass().getClassLoader().getResourceAsStream("data/integration" + path + "/integration-component.zip");
        MultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.APPLICATION_JSON_VALUE, zipInputStream);
        IntegrationResultsDto result = integrationComponent.integrateContext(uploadedFile, isXmlIntegration);
        assertThat(result.getCampaign()).isEqualTo(campaignBuilder.getResultError());
        assertThat(result.getNomenclatures()).isEqualTo(nomenclatureBuilder.getResults());
        assertThat(result.getQuestionnaireModels()).isNull();
    }

    private static Stream<Arguments> xmlIntegrationWithPaths() {
        return Stream.of(
                Arguments.of("/json", false),
                Arguments.of("/xml", true)
        );
    }
}
