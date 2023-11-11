package fr.insee.queen.api.controller.integration.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.api.integration.controller.component.builder.IntegrationCampaignBuilder;
import fr.insee.queen.api.integration.controller.component.builder.schema.SchemaIntegrationComponent;
import fr.insee.queen.api.integration.controller.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.api.integration.service.model.IntegrationResultLabel;
import fr.insee.queen.api.integration.service.model.IntegrationStatus;
import fr.insee.queen.api.service.dummy.IntegrationFakeService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Locale;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CampaignBuilderTest {
    private final ZipUtils zipUtils = new ZipUtils();
    private IntegrationCampaignBuilder campaignBuilder;

    @BeforeEach
    void init() {
        Locale.setDefault(new Locale("en", "US"));
        ObjectMapper objectMapper = new ObjectMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        SchemaIntegrationComponent schemaComponent = new SchemaIntegrationComponent();
        IntegrationFakeService integrationService = new IntegrationFakeService();
        campaignBuilder = new IntegrationCampaignBuilder(schemaComponent, validator, integrationService, objectMapper);
    }

    @Test
    @DisplayName("on building campaign, return integration success")
    void testCampaignBuilder01() throws IOException {
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("integration/campaign-builder/valid-campaign.zip");

        IntegrationResultUnitDto campaignResult = campaignBuilder.build(zipFile);
        assertThat(campaignResult.status()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(campaignResult.id()).isEqualTo(campaignId);
    }

    @Test
    @DisplayName("on building campaign, when campaign input invalid return integration error")
    void testCampaignBuilder02() throws IOException {
        String campaignId = "%hello !".toUpperCase();
        ZipFile zipFile = zipUtils.createZip("integration/campaign-builder/invalid-input-campaign.zip");

        IntegrationResultUnitDto campaignResult = campaignBuilder.build(zipFile);
        assertThat(campaignResult.status()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(campaignResult.id()).isEqualTo(campaignId);
        assertThat(campaignResult.cause()).contains("id: The identifier is invalid.");
        assertThat(campaignResult.cause()).contains("label: must not be blank.");
    }

    @Test
    @DisplayName("on building campaign, when campaign input forgotten return integration error")
    void testCampaignBuilder03() throws IOException {
        ZipFile zipFile = zipUtils.createZip("integration/campaign-builder/forgotten-input-campaign.zip");

        IntegrationResultUnitDto campaignResult = campaignBuilder.build(zipFile);
        assertThat(campaignResult.status()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(campaignResult.id()).isNull();
        assertThat(campaignResult.cause()).contains(String.format(IntegrationResultLabel.FILE_INVALID, IntegrationCampaignBuilder.CAMPAIGN_XML, ""));
    }

    @Test
    @DisplayName("on building campaign, when campaign xml missing return integration error")
    void testCampaignBuilder04() throws IOException {
        ZipFile zipFile = zipUtils.createZip("integration/campaign-builder/xml-campaign-missing.zip");

        IntegrationResultUnitDto campaignResult = campaignBuilder.build(zipFile);
        assertThat(campaignResult.status()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(campaignResult.id()).isNull();
        assertThat(campaignResult.cause()).contains(String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationCampaignBuilder.CAMPAIGN_XML));
    }
}
