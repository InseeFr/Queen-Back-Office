package fr.insee.queen.api.integration.controller.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.api.integration.controller.component.builder.IntegrationQuestionnaireBuilder;
import fr.insee.queen.api.integration.controller.component.builder.schema.SchemaIntegrationComponent;
import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.api.integration.service.dummy.IntegrationFakeService;
import fr.insee.queen.api.integration.service.model.IntegrationResultLabel;
import fr.insee.queen.api.integration.service.model.IntegrationStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class QuestionnaireBuilderTest {
    private IntegrationQuestionnaireBuilder questionnaireBuilder;
    private final ZipUtils zipUtils = new ZipUtils();

    @BeforeEach
    void init() {
        Locale.setDefault(Locale.of("en", "US"));
        ObjectMapper objectMapper = new ObjectMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        SchemaIntegrationComponent schemaComponent = new SchemaIntegrationComponent();
        IntegrationFakeService integrationService = new IntegrationFakeService();
        questionnaireBuilder = new IntegrationQuestionnaireBuilder(schemaComponent, validator, integrationService, objectMapper);
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building questionnaires, return integration result created")
    void testQuestionnaireBuilder01(String path, boolean isXmlIntegration) throws IOException {
        String questionnaireId1 = "simpsons-v1";
        String questionnaireId2 = "simpson-v2";
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("integration" + path + "/questionnaire-builder/valid-questionnaires.zip");
        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile, isXmlIntegration);
        IntegrationResultUnitDto result1 = IntegrationResultUnitDto.integrationResultUnitCreated(questionnaireId1);
        IntegrationResultUnitDto result2 = IntegrationResultUnitDto.integrationResultUnitCreated(questionnaireId2);
        assertThat(results)
                .hasSize(2)
                .contains(result1)
                .contains(result2);
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building questionnaires, when questionnaire input invalid return integration error")
    void testQuestionnaireBuilder02(String path, boolean isXmlIntegration) throws IOException {
        String questionnaireId = "simpsons%v1";
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("integration" + path + "/questionnaire-builder/invalid-input-questionnaires.zip");

        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile, isXmlIntegration);
        assertThat(results).hasSize(2);
        List<IntegrationResultUnitDto> resultErrors = results.stream()
                .filter(result -> result.getStatus().equals(IntegrationStatus.ERROR))
                .toList();
        assertThat(resultErrors).hasSize(1);
        IntegrationResultUnitDto errorResult = resultErrors.get(0);
        assertThat(errorResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(errorResult.getId()).isEqualTo(questionnaireId);
        assertThat(errorResult.getCause()).contains("idQuestionnaireModel: The identifier is invalid.");
        assertThat(errorResult.getCause()).contains("label: must not be empty.");
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building questionnaires, when questionnaire forgotten return integration error")
    void testQuestionnaireBuilder03(String path, boolean isXmlIntegration) throws IOException {
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("integration" + path + "/questionnaire-builder/forgotten-questionnaires.zip");

        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile, isXmlIntegration);
        assertThat(results).hasSize(2);
        List<IntegrationResultUnitDto> resultErrors = results.stream()
                .filter(result -> result.getStatus().equals(IntegrationStatus.ERROR))
                .toList();
        assertThat(resultErrors).hasSize(1);
        IntegrationResultUnitDto errorResult = resultErrors.get(0);
        assertThat(errorResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(errorResult.getId()).isEqualTo("simpson-v2");
        assertThat(errorResult.getCause()).contains(String.format(IntegrationResultLabel.QUESTIONNAIRE_FILE_NOT_FOUND, "simpsons-v2.json"));
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building questionnaires, when questionnaire  missing return integration error")
    void testQuestionnaireBuilder04(String path, boolean isXmlIntegration) throws IOException {
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("integration" + path + "/questionnaire-builder/xml-questionnaire-missing.zip");

        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile, isXmlIntegration);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto questionnaireResult = results.get(0);
        assertThat(questionnaireResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(questionnaireResult.getId()).isNull();
        assertThat(questionnaireResult.getCause())
                .containsAnyOf(String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationQuestionnaireBuilder.QUESTIONNAIRE_MODELS_XML),
                        String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationQuestionnaireBuilder.QUESTIONNAIRE_MODELS_JSON));
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building questionnaires, when malformed xml questionnaire return integration error")
    void testQuestionnaireBuilder05(String path, boolean isXmlIntegration) throws IOException {
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("integration" + path + "/questionnaire-builder/malformed-questionnaires.zip");

        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile, isXmlIntegration);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto questionnaireResult = results.get(0);
        assertThat(questionnaireResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(questionnaireResult.getId()).isNull();
        assertThat(questionnaireResult.getCause())
                .containsAnyOf(String.format(IntegrationResultLabel.FILE_INVALID, IntegrationQuestionnaireBuilder.QUESTIONNAIRE_MODELS_XML, ""),
                        String.format(IntegrationResultLabel.JSON_PARSING_ERROR, IntegrationQuestionnaireBuilder.QUESTIONNAIRE_MODELS_JSON));
    }

    @Test
    @DisplayName("on building questionnaires, when campaign id from xml different from campaign id in questionnaire xml return integration error")
    void testQuestionnaireBuilderXml06() throws IOException {
        String campaignId = "different-id";
        String questionnaireId1 = "simpsons%v1";
        String questionnaireId2 = "simpson-v2";

        ZipFile zipFile = zipUtils.createZip("integration/xml/questionnaire-builder/invalid-input-questionnaires.zip");

        List<IntegrationResultUnitDto> results = questionnaireBuilder.build(campaignId, zipFile, true);
        IntegrationResultUnitDto expectedResult1 = IntegrationResultUnitDto.integrationResultUnitError(questionnaireId1, String.format(IntegrationResultLabel.CAMPAIGN_IDS_MISMATCH, "SIMPSONS2020X00", campaignId));
        IntegrationResultUnitDto expectedResult2 = IntegrationResultUnitDto.integrationResultUnitError(questionnaireId2, String.format(IntegrationResultLabel.CAMPAIGN_IDS_MISMATCH, "SIMPSONS2020X00", campaignId));

        assertThat(results)
                .hasSize(2)
                .contains(expectedResult1)
                .contains(expectedResult2);
    }

    private static Stream<Arguments> xmlIntegrationWithPaths() {
        return Stream.of(
                Arguments.of("/json", false),
                Arguments.of("/xml", true)
        );
    }
}
