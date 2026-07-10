package fr.insee.queen.application.integration.controller.builder;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import fr.insee.queen.application.integration.component.builder.IntegrationNomenclatureBuilder;
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
class IntegrationNomenclatureBuilderTest {
    private IntegrationNomenclatureBuilder nomenclatureBuilder;
    private final ZipUtils zipUtils = new ZipUtils();

    @BeforeEach
    void init() {
        Locale.setDefault(Locale.of("en", "US"));
        ObjectMapper objectMapper = new JsonMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        SchemaIntegrationComponent schemaComponent = new SchemaIntegrationComponent(objectMapper, new JsonValidatorComponent());
        IntegrationFakeService integrationService = new IntegrationFakeService();
        nomenclatureBuilder = new IntegrationNomenclatureBuilder(schemaComponent, validator, objectMapper, integrationService);
    }

    @Test
    @DisplayName("on building nomenclatures, return integration result created")
    void testNomenclatureBuilder01() throws IOException {
        String nomenclatureId1 = "regions2023";
        String nomenclatureId2 = "cities2023";
        ZipFile zipFile = zipUtils.createZip("data/integration/json/nomenclature-builder/valid-nomenclatures.zip");
        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile);
        IntegrationResultUnitDto result1 = new IntegrationResultUnitDto(nomenclatureId1, IntegrationStatus.CREATED, null);
        IntegrationResultUnitDto result2 = new IntegrationResultUnitDto(nomenclatureId2, IntegrationStatus.CREATED, null);
        assertThat(results)
                .hasSize(2)
                .contains(result1)
                .contains(result2);
    }

    @Test
    @DisplayName("on building nomenclature, when nomenclature input invalid return integration error")
    void testNomenclatureBuilder02() throws IOException {
        String nomenclatureId = "cities%2023";
        ZipFile zipFile = zipUtils.createZip("data/integration/json/nomenclature-builder/invalid-input-nomenclatures.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile);
        assertThat(results).hasSize(2);
        List<IntegrationResultUnitDto> resultErrors = results.stream()
                .filter(result -> result.getStatus().equals(IntegrationStatus.ERROR))
                .toList();
        assertThat(resultErrors).hasSize(1);
        IntegrationResultUnitDto errorResult = resultErrors.getFirst();
        assertThat(errorResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(errorResult.getId()).isEqualTo(nomenclatureId);
        assertThat(errorResult.getCause()).contains("id: The identifier is invalid.");
        assertThat(errorResult.getCause()).contains("label: must not be blank.");
    }

    @Test
    @DisplayName("on building nomenclature, when json nomenclature forgotten return integration error")
    void testNomenclatureBuilder03() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/json/nomenclature-builder/forgotten-nomenclatures.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile);
        assertThat(results).hasSize(2);
        List<IntegrationResultUnitDto> resultErrors = results.stream()
                .filter(result -> result.getStatus().equals(IntegrationStatus.ERROR))
                .toList();
        assertThat(resultErrors).hasSize(1);
        IntegrationResultUnitDto errorResult = resultErrors.getFirst();
        assertThat(errorResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(errorResult.getId()).isEqualTo("cities2023");
        assertThat(errorResult.getCause()).contains(String.format(IntegrationResultLabel.NOMENCLATURE_FILE_NOT_FOUND, "cities2023.json"));
    }

    @Test
    @DisplayName("on building nomenclature, when nomenclature json missing return integration error")
    void testNomenclatureBuilder04() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/json/nomenclature-builder/xml-nomenclature-missing.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto nomenclatureResult = results.getFirst();
        assertThat(nomenclatureResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(nomenclatureResult.getId()).isNull();
        assertThat(nomenclatureResult.getCause())
                .contains(String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationNomenclatureBuilder.NOMENCLATURES_JSON));
    }

    @Test
    @DisplayName("on building nomenclature, when malformed json nomenclature return integration error")
    void testNomenclatureBuilder05() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/json/nomenclature-builder/malformed-nomenclatures.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto nomenclatureResult = results.getFirst();
        assertThat(nomenclatureResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(nomenclatureResult.getId()).isNull();
        assertThat(nomenclatureResult.getCause()).contains(String.format(IntegrationResultLabel.FILE_INVALID, IntegrationNomenclatureBuilder.NOMENCLATURES_JSON, ""));
    }
}
