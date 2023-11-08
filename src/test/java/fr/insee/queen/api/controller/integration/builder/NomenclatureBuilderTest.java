package fr.insee.queen.api.controller.integration.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.api.controller.integration.component.IntegrationResultLabel;
import fr.insee.queen.api.controller.integration.component.SchemaIntegrationComponent;
import fr.insee.queen.api.controller.integration.component.builder.IntegrationNomenclatureBuilder;
import fr.insee.queen.api.dto.integration.IntegrationResultErrorUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultSuccessUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationResultUnitDto;
import fr.insee.queen.api.dto.integration.IntegrationStatus;
import fr.insee.queen.api.service.dummy.IntegrationFakeService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class NomenclatureBuilderTest {
    private SchemaIntegrationComponent schemaComponent;
    private Validator validator;
    private IntegrationFakeService integrationService;
    private ObjectMapper objectMapper;
    private IntegrationNomenclatureBuilder nomenclatureBuilder;
    private ZipUtils zipUtils = new ZipUtils();

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        schemaComponent = new SchemaIntegrationComponent();
        integrationService = new IntegrationFakeService();
        nomenclatureBuilder = new IntegrationNomenclatureBuilder(schemaComponent, validator, objectMapper, integrationService);
    }

    @Test
    @DisplayName("on building nomenclatures, return integration result created")
    void testNomenclatureBuilder01() throws IOException {
        String nomenclatureId1 = "regions2023";
        String nomenclatureId2 = "cities2023";
        ZipFile zipFile = zipUtils.createZip("integration/nomenclature-builder/valid-nomenclatures.zip");
        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile);
        IntegrationResultUnitDto result1 = IntegrationResultSuccessUnitDto.integrationResultUnitCreated(nomenclatureId1);
        IntegrationResultUnitDto result2 = IntegrationResultSuccessUnitDto.integrationResultUnitCreated(nomenclatureId2);
        assertThat(results).hasSize(2);
        assertThat(results).contains(result1);
        assertThat(results).contains(result2);
    }

    @Test
    @DisplayName("on building nomenclature, when nomenclature input invalid return integration error")
    void testNomenclatureBuilder02() throws IOException {
        String nomenclatureId = "cities_2023";
        ZipFile zipFile = zipUtils.createZip("integration/nomenclature-builder/invalid-input-nomenclatures.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile);
        assertThat(results).hasSize(2);
        List<IntegrationResultErrorUnitDto> resultErrors = results.stream()
                .filter(IntegrationResultErrorUnitDto.class::isInstance)
                .map(IntegrationResultErrorUnitDto.class::cast)
                .toList();
        assertThat(resultErrors).hasSize(1);
        IntegrationResultErrorUnitDto errorResult = resultErrors.get(0);
        assertThat(errorResult.status()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(errorResult.id()).isEqualTo(nomenclatureId);
        assertThat(errorResult.cause()).contains("id: The identifier is invalid.");
        assertThat(errorResult.cause()).contains("label: ne doit pas être vide.");
    }

    @Test
    @DisplayName("on building nomenclature, when json nomenclature forgotten return integration error")
    void testNomenclatureBuilder03() throws IOException {
        ZipFile zipFile = zipUtils.createZip("integration/nomenclature-builder/forgotten-nomenclatures.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile);
        assertThat(results).hasSize(2);
        List<IntegrationResultErrorUnitDto> resultErrors = results.stream()
                .filter(IntegrationResultErrorUnitDto.class::isInstance)
                .map(IntegrationResultErrorUnitDto.class::cast)
                .toList();
        assertThat(resultErrors).hasSize(1);
        log.error(resultErrors.toString());
        IntegrationResultErrorUnitDto errorResult = resultErrors.get(0);
        assertThat(errorResult.status()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(errorResult.id()).isEqualTo("cities2023");
        assertThat(errorResult.cause()).contains(String.format(IntegrationResultLabel.NOMENCLATURE_FILE_NOT_FOUND, "cities2023.json"));
    }

    @Test
    @DisplayName("on building nomenclature, when nomenclature xml missing return integration error")
    void testNomenclatureBuilder04() throws IOException {
        ZipFile zipFile = zipUtils.createZip("integration/nomenclature-builder/xml-nomenclature-missing.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto nomenclatureResult = results.get(0);
        assertThat(nomenclatureResult.status()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(nomenclatureResult.id()).isNull();
        assertThat(nomenclatureResult.cause()).contains(String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationNomenclatureBuilder.NOMENCLATURES_XML));
    }

    @Test
    @DisplayName("on building nomenclature, when malformed xml nomenclature return integration error")
    void testNomenclatureBuilder05() throws IOException {
        ZipFile zipFile = zipUtils.createZip("integration/nomenclature-builder/malformed-nomenclatures.zip");

        List<IntegrationResultUnitDto> results = nomenclatureBuilder.build(zipFile);
        assertThat(results).hasSize(1);
        IntegrationResultUnitDto nomenclatureResult = results.get(0);
        assertThat(nomenclatureResult.status()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(nomenclatureResult.id()).isNull();
        assertThat(nomenclatureResult.cause()).contains(String.format(IntegrationResultLabel.FILE_INVALID, IntegrationNomenclatureBuilder.NOMENCLATURES_XML, ""));
    }
}
