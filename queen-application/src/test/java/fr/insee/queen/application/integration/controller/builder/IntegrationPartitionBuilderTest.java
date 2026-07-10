package fr.insee.queen.application.integration.controller.builder;

import fr.insee.queen.application.integration.component.builder.IntegrationCampaignBuilder;
import fr.insee.queen.application.integration.component.builder.IntegrationPartitionsBuilder;
import fr.insee.queen.application.integration.component.builder.schema.SchemaIntegrationComponent;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.integration.service.dummy.IntegrationFakeService;
import fr.insee.queen.application.web.validation.json.JsonValidatorComponent;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import fr.insee.queen.domain.integration.model.IntegrationStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class IntegrationPartitionBuilderTest {
    private final ZipUtils zipUtils = new ZipUtils();
    private IntegrationPartitionsBuilder partitionsBuilder;
    private IntegrationFakeService integrationFakeService;

    @BeforeEach
    void init() {
        Locale.setDefault(Locale.of("en", "US"));
        ObjectMapper objectMapper = new JsonMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        SchemaIntegrationComponent schemaComponent = new SchemaIntegrationComponent(objectMapper, new JsonValidatorComponent());
        integrationFakeService = new IntegrationFakeService();
        partitionsBuilder = new IntegrationPartitionsBuilder(schemaComponent, validator, integrationFakeService, objectMapper);
    }

    @Test
    @DisplayName("on building partitions, return integration success")
    void testPartitionsBuilder01() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/json/partitions-builder/valid-partitions.zip");

        List<IntegrationResultUnitDto> partitionsResult = partitionsBuilder.build(zipFile, java.util.Set.of());
        List<Group> partitionsCreated = integrationFakeService.getGroupsCreated();
        assertThat(partitionsCreated).hasSize(2);
        assertThat(partitionsResult.getFirst().getStatus()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(partitionsResult.getFirst().getId()).containsAnyOf("SIMPSONS2020X0001", "SIMPSONS2020X0002");
        assertThat(partitionsResult.get(1).getStatus()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(partitionsResult.get(1).getId()).containsAnyOf("SIMPSONS2020X0001", "SIMPSONS2020X0002");
    }

    @Test
    @DisplayName("on building partitions, when partitions input invalid return integration error")
    void testPartitionsBuilder02() throws IOException {
        String partitionIds = "%hello !, %hello2 !";
        ZipFile zipFile = zipUtils.createZip("data/integration/json/partitions-builder/invalid-input-partitions.zip");

        List<IntegrationResultUnitDto> partitionsResult = partitionsBuilder.build(zipFile, java.util.Set.of());
        assertThat(partitionsResult.getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(partitionsResult.getFirst().getId()).isEqualTo(partitionIds);
        assertThat(partitionsResult.getFirst().getCause()).contains("ids[0].<list element>: The identifier is invalid.");
        assertThat(partitionsResult.getFirst().getCause()).contains("ids[1].<list element>: The identifier is invalid.");
        assertThat(partitionsResult.getFirst().getCause()).contains("label: must not be blank.");
    }

    @Test
    @DisplayName("on building partitions, when partitions input forgotten return integration error")
    void testPartitionsBuilder03() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/json/partitions-builder/forgotten-input-partitions.zip");

        List<IntegrationResultUnitDto> partitionsResult = partitionsBuilder.build(zipFile, java.util.Set.of());
        assertThat(partitionsResult.getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(partitionsResult.getFirst().getId()).isNull();
        assertThat(partitionsResult.getFirst().getCause())
                .contains(String.format(IntegrationResultLabel.FILE_INVALID, IntegrationPartitionsBuilder.PARTITIONS_JSON, ""));
    }

    @Test
    @DisplayName("on building partitions, when partitions json missing return integration error")
    void testPartitionsBuilder04() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/json/partitions-builder/partitions-missing.zip");

        List<IntegrationResultUnitDto> partitionsResult = partitionsBuilder.build(zipFile, java.util.Set.of());
        assertThat(partitionsResult.getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(partitionsResult.getFirst().getId()).isNull();
        assertThat(partitionsResult.getFirst().getCause())
                .contains(String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationPartitionsBuilder.PARTITIONS_JSON));
    }
}
