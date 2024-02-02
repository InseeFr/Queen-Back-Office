package fr.insee.queen.application.integration.controller.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.application.integration.component.builder.IntegrationNomenclatureBuilder;
import fr.insee.queen.application.integration.component.builder.schema.SchemaIntegrationComponent;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.integration.service.dummy.IntegrationFakeService;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
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
class NomenclatureBuilderTest {
    private IntegrationNomenclatureBuilder nomenclatureBuilder;
    private final ZipUtils zipUtils = new ZipUtils();

    @BeforeEach
    void init() {
        Locale.setDefault(Locale.of("en", "US"));
        ObjectMapper objectMapper = new ObjectMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        SchemaIntegrationComponent schemaComponent = new SchemaIntegrationComponent(objectMapper);
        IntegrationFakeService integrationService = new IntegrationFakeService();
        nomenclatureBuilder = new IntegrationNomenclatureBuilder(schemaComponent, validator, objectMapper, integrationService);
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building nomenclatures, return integration result created")
    void testNomenclatureBuilder01(String path, boolean isXmlIntegration) throws IOException {
        String nomenclatureId1 = "regions2023";
        String nomenclatureId2 = "cities2023";
        ZipFile zipFile = zipUtils.createZip("data/integration" + path + "/nomenclature-builder/valid-nomenclatures.zip");
        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile, isXmlIntegration);
        IntegrationResultUnitDto result1 = new IntegrationResultUnitDto(nomenclatureId1, IntegrationStatus.CREATED, null);
        IntegrationResultUnitDto result2 = new IntegrationResultUnitDto(nomenclatureId2, IntegrationStatus.CREATED, null);
        assertThat(results)
                .hasSize(2)
                .contains(result1)
                .contains(result2);
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building nomenclature, when nomenclature input invalid return integration error")
    void testNomenclatureBuilder02(String path, boolean isXmlIntegration) throws IOException {
        String nomenclatureId = "cities%2023";
        ZipFile zipFile = zipUtils.createZip("data/integration" + path + "/nomenclature-builder/invalid-input-nomenclatures.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile, isXmlIntegration);
        assertThat(results).hasSize(2);
        List<IntegrationResultUnitDto> resultErrors = results.stream()
                .filter(result -> result.getStatus().equals(IntegrationStatus.ERROR))
                .toList();
        assertThat(resultErrors).hasSize(1);
        IntegrationResultUnitDto errorResult = resultErrors.get(0);
        assertThat(errorResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(errorResult.getId()).isEqualTo(nomenclatureId);
        assertThat(errorResult.getCause()).contains("id: The identifier is invalid.");
        assertThat(errorResult.getCause()).contains("label: must not be blank.");
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building nomenclature, when json nomenclature forgotten return integration error")
    void testNomenclatureBuilder03(String path, boolean isXmlIntegration) throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration" + path + "/nomenclature-builder/forgotten-nomenclatures.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile, isXmlIntegration);
        assertThat(results).hasSize(2);
        List<IntegrationResultUnitDto> resultErrors = results.stream()
                .filter(result -> result.getStatus().equals(IntegrationStatus.ERROR))
                .toList();
        assertThat(resultErrors).hasSize(1);
        IntegrationResultUnitDto errorResult = resultErrors.get(0);
        assertThat(errorResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(errorResult.getId()).isEqualTo("cities2023");
        assertThat(errorResult.getCause()).contains(String.format(IntegrationResultLabel.NOMENCLATURE_FILE_NOT_FOUND, "cities2023.json"));
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building nomenclature, when nomenclature xml missing return integration error")
    void testNomenclatureBuilder04(String path, boolean isXmlIntegration) throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration" + path + "/nomenclature-builder/xml-nomenclature-missing.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile, isXmlIntegration);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto nomenclatureResult = results.get(0);
        assertThat(nomenclatureResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(nomenclatureResult.getId()).isNull();
        assertThat(nomenclatureResult.getCause())
                .containsAnyOf(String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationNomenclatureBuilder.NOMENCLATURES_XML),
                        String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationNomenclatureBuilder.NOMENCLATURES_JSON));
    }

    @Test
    @DisplayName("on building nomenclature, when malformed xml nomenclature return integration error")
    void testNomenclatureBuilderXml05() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/xml/nomenclature-builder/malformed-nomenclatures.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile, true);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto nomenclatureResult = results.get(0);
        assertThat(nomenclatureResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(nomenclatureResult.getId()).isNull();
        assertThat(nomenclatureResult.getCause()).contains(String.format(IntegrationResultLabel.FILE_INVALID, IntegrationNomenclatureBuilder.NOMENCLATURES_XML, ""));
    }

    @Test
    @DisplayName("on building nomenclature, when malformed xml nomenclature return integration error")
    void testNomenclatureBuilderJson05() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/json/nomenclature-builder/malformed-nomenclatures.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile, false);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto nomenclatureResult = results.get(0);
        assertThat(nomenclatureResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(nomenclatureResult.getId()).isNull();
        assertThat(nomenclatureResult.getCause()).contains(String.format(IntegrationResultLabel.FILE_INVALID, IntegrationNomenclatureBuilder.NOMENCLATURES_JSON, ""));
    }

    private static Stream<Arguments> xmlIntegrationWithPaths() {
        return Stream.of(
                Arguments.of("/json", false),
                Arguments.of("/xml", true)
        );
    }
}
