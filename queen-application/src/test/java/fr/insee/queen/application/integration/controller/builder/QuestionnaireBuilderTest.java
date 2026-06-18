package fr.insee.queen.application.integration.controller.builder;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import fr.insee.queen.application.integration.component.builder.IntegrationQuestionnaireBuilder;
import fr.insee.queen.application.integration.component.builder.schema.SchemaIntegrationComponent;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.integration.service.dummy.IntegrationFakeService;
import fr.insee.queen.application.web.validation.json.JsonValidatorComponent;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class QuestionnaireBuilderTest {
    private IntegrationQuestionnaireBuilder questionnaireBuilder;
    private final ZipUtils zipUtils = new ZipUtils();

    @BeforeEach
    void init() {
        Locale.setDefault(Locale.of("en", "US"));
        ObjectMapper objectMapper = new JsonMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        SchemaIntegrationComponent schemaComponent = new SchemaIntegrationComponent(objectMapper, new JsonValidatorComponent());
        IntegrationFakeService integrationService = new IntegrationFakeService();
        questionnaireBuilder = new IntegrationQuestionnaireBuilder(schemaComponent, validator, integrationService, objectMapper);
    }

    @Test
    @DisplayName("on building questionnaires, return integration result created")
    void testQuestionnaireBuilder01() throws IOException {
        String questionnaireId1 = "simpsons-v1";
        String questionnaireId2 = "simpson-v2";
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("data/integration/json/questionnaire-builder/valid-questionnaires.zip");
        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile);
        IntegrationResultUnitDto result1 = IntegrationResultUnitDto.integrationResultUnitCreated(questionnaireId1);
        IntegrationResultUnitDto result2 = IntegrationResultUnitDto.integrationResultUnitCreated(questionnaireId2);
        assertThat(results)
                .hasSize(2)
                .contains(result1)
                .contains(result2);
    }

    @Test
    @DisplayName("on building questionnaires, when questionnaire input invalid return integration error")
    void testQuestionnaireBuilder02() throws IOException {
        String questionnaireId = "simpsons%v1";
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("data/integration/json/questionnaire-builder/invalid-input-questionnaires.zip");

        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile);
        assertThat(results).hasSize(2);
        List<IntegrationResultUnitDto> resultErrors = results.stream()
                .filter(result -> result.getStatus().equals(IntegrationStatus.ERROR))
                .toList();
        assertThat(resultErrors).hasSize(1);
        IntegrationResultUnitDto errorResult = resultErrors.getFirst();
        assertThat(errorResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(errorResult.getId()).isEqualTo(questionnaireId);
        assertThat(errorResult.getCause()).contains("idQuestionnaireModel: The identifier is invalid.");
        assertThat(errorResult.getCause()).contains("label: must not be empty.");
    }

    @Test
    @DisplayName("on building questionnaires, when questionnaire forgotten return integration error")
    void testQuestionnaireBuilder03() throws IOException {
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("data/integration/json/questionnaire-builder/forgotten-questionnaires.zip");

        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile);
        assertThat(results).hasSize(2);
        List<IntegrationResultUnitDto> resultErrors = results.stream()
                .filter(result -> result.getStatus().equals(IntegrationStatus.ERROR))
                .toList();
        assertThat(resultErrors).hasSize(1);
        IntegrationResultUnitDto errorResult = resultErrors.getFirst();
        assertThat(errorResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(errorResult.getId()).isEqualTo("simpson-v2");
        assertThat(errorResult.getCause()).contains(String.format(IntegrationResultLabel.QUESTIONNAIRE_FILE_NOT_FOUND, "simpsons-v2.json"));
    }

    @Test
    @DisplayName("on building questionnaires, when questionnaire missing return integration error")
    void testQuestionnaireBuilder04() throws IOException {
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("data/integration/json/questionnaire-builder/xml-questionnaire-missing.zip");

        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto questionnaireResult = results.getFirst();
        assertThat(questionnaireResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(questionnaireResult.getId()).isNull();
        assertThat(questionnaireResult.getCause())
                .contains(String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationQuestionnaireBuilder.QUESTIONNAIRE_MODELS_JSON));
    }

    @Test
    @DisplayName("on building questionnaires, when malformed json questionnaire return integration error")
    void testQuestionnaireBuilder05() throws IOException {
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("data/integration/json/questionnaire-builder/malformed-questionnaires.zip");

        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto questionnaireResult = results.getFirst();
        assertThat(questionnaireResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(questionnaireResult.getId()).isNull();
        assertThat(questionnaireResult.getCause())
                .contains(String.format(IntegrationResultLabel.FILE_INVALID, IntegrationQuestionnaireBuilder.QUESTIONNAIRE_MODELS_JSON, ""));
    }
}
