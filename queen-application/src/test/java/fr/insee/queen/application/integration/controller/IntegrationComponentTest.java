package fr.insee.queen.application.integration.controller;

import fr.insee.queen.application.configuration.properties.ApplicationProperties;
import fr.insee.queen.application.integration.controller.builder.dummy.CampaignFakeBuilder;
import fr.insee.queen.application.integration.controller.builder.dummy.NomenclatureFakeBuilder;
import fr.insee.queen.application.integration.controller.builder.dummy.QuestionnaireFakeBuilder;
import fr.insee.queen.application.integration.component.IntegrationComponent;
import fr.insee.queen.application.integration.component.exception.IntegrationComponentException;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.integration.dto.output.IntegrationResultsDto;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

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
        ApplicationProperties applicationProperties = new ApplicationProperties(null, null, null, null, null, System.getProperty("java.io.tmpdir"));
        integrationComponent = new IntegrationComponent(nomenclatureBuilder, campaignBuilder, questionnaireBuilder, applicationProperties);
    }

    @Test
    @DisplayName("on integration, when file is not a zip, throw exception")
    void integrate01() {
        MultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.APPLICATION_JSON_VALUE, "Hello, World!".getBytes());
        assertThatThrownBy(() -> integrationComponent.integrateContext(uploadedFile)).isInstanceOf(IntegrationComponentException.class);
    }

    @Test
    @DisplayName("on integration, return an integration result list")
    void integrate02() throws IOException {
        InputStream zipInputStream = getClass().getClassLoader().getResourceAsStream("data/integration/json/integration-component.zip");
        MultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.APPLICATION_JSON_VALUE, zipInputStream);
        IntegrationResultsDto result = integrationComponent.integrateContext(uploadedFile);
        assertThat(result.getCampaign()).isEqualTo(campaignBuilder.getResultSuccess());
        assertThat(result.getNomenclatures()).isEqualTo(nomenclatureBuilder.getResults());
        assertThat(result.getQuestionnaireModels()).isEqualTo(questionnaireBuilder.getResults());
    }

    @Test
    @DisplayName("when integrating campaign in errors, then questionnaires are still processed and campaign reports the error")
    void integrate03() throws IOException {
        campaignBuilder.setResultIsInErrorState(true);
        InputStream zipInputStream = getClass().getClassLoader().getResourceAsStream("data/integration/json/integration-component.zip");
        MultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.APPLICATION_JSON_VALUE, zipInputStream);
        IntegrationResultsDto result = integrationComponent.integrateContext(uploadedFile);
        assertThat(result.getCampaign()).isEqualTo(campaignBuilder.getResultError());
        assertThat(result.getNomenclatures()).isEqualTo(nomenclatureBuilder.getResults());
        assertThat(result.getQuestionnaireModels()).isEqualTo(questionnaireBuilder.getResults());
    }

    @Test
    @DisplayName("on integration, the campaign builder receives the ids of successfully integrated questionnaires")
    void integrate04() throws IOException {
        InputStream zipInputStream = getClass().getClassLoader().getResourceAsStream("data/integration/json/integration-component.zip");
        MultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.APPLICATION_JSON_VALUE, zipInputStream);
        integrationComponent.integrateContext(uploadedFile);
        assertThat(campaignBuilder.getReceivedQuestionnaireIds())
                .containsExactlyInAnyOrder("id-questionnaire1", "id-questionnaire2");
    }

    @Test
    @DisplayName("when at least one questionnaire is in error, the campaign is not integrated")
    void integrate05() throws IOException {
        questionnaireBuilder.setOneResultInErrorState(true);
        InputStream zipInputStream = getClass().getClassLoader().getResourceAsStream("data/integration/json/integration-component.zip");
        MultipartFile uploadedFile = new MockMultipartFile("file", "hello.txt", MediaType.APPLICATION_JSON_VALUE, zipInputStream);
        IntegrationResultsDto result = integrationComponent.integrateContext(uploadedFile);

        IntegrationResultUnitDto expectedCampaign = IntegrationResultUnitDto.integrationResultUnitError(
                null, IntegrationResultLabel.CAMPAIGN_SKIPPED_QUESTIONNAIRE_ERRORS);
        assertThat(result.getCampaign()).isEqualTo(expectedCampaign);
        assertThat(result.getCampaign().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(result.getQuestionnaireModels()).isEqualTo(questionnaireBuilder.getResults());
        assertThat(campaignBuilder.getReceivedQuestionnaireIds()).isEmpty();
    }
}
