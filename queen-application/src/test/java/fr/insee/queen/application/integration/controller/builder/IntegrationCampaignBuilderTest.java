package fr.insee.queen.application.integration.controller.builder;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import fr.insee.queen.application.integration.component.builder.IntegrationCampaignBuilder;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class IntegrationCampaignBuilderTest {
    private final ZipUtils zipUtils = new ZipUtils();
    private IntegrationCampaignBuilder campaignBuilder;
    private IntegrationFakeService integrationFakeService;

    @BeforeEach
    void init() {
        Locale.setDefault(Locale.of("en", "US"));
        ObjectMapper objectMapper = new JsonMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        SchemaIntegrationComponent schemaComponent = new SchemaIntegrationComponent(objectMapper, new JsonValidatorComponent());
        integrationFakeService = new IntegrationFakeService();
        campaignBuilder = new IntegrationCampaignBuilder(schemaComponent, validator, integrationFakeService, objectMapper);
    }

    @Test
    @DisplayName("on building campaign, return integration success")
    void testCampaignBuilder01() throws IOException {
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("data/integration/json/campaign-builder/valid-campaign.zip");

        List<IntegrationResultUnitDto> campaignResult = campaignBuilder.build(zipFile, java.util.Set.of());
        Group campaignCreated = integrationFakeService.getGroupsCreated().getFirst();
        assertThat(campaignResult.getFirst().getStatus()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(campaignResult.getFirst().getId()).isEqualTo(campaignId);
        assertThat(campaignCreated.getId()).isEqualTo("SIMPSONS2020X00");
        assertThat(campaignCreated.getLabel()).isEqualTo("Enquête sur les simpsons 2020");
    }

    @Test
    @DisplayName("on building campaign, when campaign input invalid return integration error")
    void testCampaignBuilder02() throws IOException {
        String campaignId = "%hello !";
        ZipFile zipFile = zipUtils.createZip("data/integration/json/campaign-builder/invalid-input-campaign.zip");

        List<IntegrationResultUnitDto> campaignResult = campaignBuilder.build(zipFile, java.util.Set.of());
        assertThat(campaignResult.getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(campaignResult.getFirst().getId()).isEqualTo(campaignId);
        assertThat(campaignResult.getFirst().getCause()).contains("id: The identifier is invalid.");
        assertThat(campaignResult.getFirst().getCause()).contains("label: must not be blank.");
    }

    @Test
    @DisplayName("on building campaign, when campaign input forgotten return integration error")
    void testCampaignBuilder03() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/json/campaign-builder/forgotten-input-campaign.zip");

        List<IntegrationResultUnitDto> campaignResult = campaignBuilder.build(zipFile, java.util.Set.of());
        assertThat(campaignResult.getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(campaignResult.getFirst().getId()).isNull();
        assertThat(campaignResult.getFirst().getCause())
                .contains(String.format(IntegrationResultLabel.FILE_INVALID, IntegrationCampaignBuilder.CAMPAIGN_JSON, ""));
    }

    @Test
    @DisplayName("on building campaign, when campaign json missing return integration error")
    void testCampaignBuilder04() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/json/campaign-builder/campaign-missing.zip");

        List<IntegrationResultUnitDto> campaignResult = campaignBuilder.build(zipFile, java.util.Set.of());
        assertThat(campaignResult.getFirst().getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(campaignResult.getFirst().getId()).isNull();
        assertThat(campaignResult.getFirst().getCause())
                .contains(String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationCampaignBuilder.CAMPAIGN_JSON));
    }
}
