package fr.insee.queen.application.integration.controller.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.application.integration.component.builder.IntegrationCampaignBuilder;
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
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CampaignBuilderTest {
    private final ZipUtils zipUtils = new ZipUtils();
    private IntegrationCampaignBuilder campaignBuilder;

    @BeforeEach
    void init() {
        Locale.setDefault(Locale.of("en", "US"));
        ObjectMapper objectMapper = new ObjectMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        SchemaIntegrationComponent schemaComponent = new SchemaIntegrationComponent();
        IntegrationFakeService integrationService = new IntegrationFakeService();
        campaignBuilder = new IntegrationCampaignBuilder(schemaComponent, validator, integrationService, objectMapper);
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building campaign, return integration success")
    void testCampaignBuilder01(String path, boolean isXmlIntegration) throws IOException {
        String campaignId = "SIMPSONS2020X00";
        ZipFile zipFile = zipUtils.createZip("data/integration" + path + "/campaign-builder/valid-campaign.zip");

        IntegrationResultUnitDto campaignResult = campaignBuilder.build(zipFile, isXmlIntegration);
        assertThat(campaignResult.getStatus()).isEqualTo(IntegrationStatus.CREATED);
        assertThat(campaignResult.getId()).isEqualTo(campaignId);
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building campaign, when campaign input invalid return integration error")
    void testCampaignBuilder02(String path, boolean isXmlIntegration) throws IOException {
        String campaignId = "%hello !";
        ZipFile zipFile = zipUtils.createZip("data/integration" + path + "/campaign-builder/invalid-input-campaign.zip");

        IntegrationResultUnitDto campaignResult = campaignBuilder.build(zipFile, isXmlIntegration);
        assertThat(campaignResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(campaignResult.getId()).isEqualTo(campaignId);
        assertThat(campaignResult.getCause()).contains("id: The identifier is invalid.");
        assertThat(campaignResult.getCause()).contains("label: must not be blank.");
    }

    @Test
    @DisplayName("on building campaign, when campaign input forgotten return integration error")
    void testCampaignBuilderXml03() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/xml/campaign-builder/forgotten-input-campaign.zip");

        IntegrationResultUnitDto campaignResult = campaignBuilder.build(zipFile, true);
        assertThat(campaignResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(campaignResult.getId()).isNull();
        assertThat(campaignResult.getCause()).contains(String.format(IntegrationResultLabel.FILE_INVALID, IntegrationCampaignBuilder.CAMPAIGN_XML, ""));
    }

    @Test
    @DisplayName("on building campaign, when campaign input forgotten return integration error")
    void testCampaignBuilderJson03() throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration/json/campaign-builder/forgotten-input-campaign.zip");

        IntegrationResultUnitDto campaignResult = campaignBuilder.build(zipFile, false);
        assertThat(campaignResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(campaignResult.getId()).isNull();
        assertThat(campaignResult.getCause())
                .contains("id: The identifier is invalid.")
                .contains("label: must not be blank.");
    }

    @ParameterizedTest
    @MethodSource("xmlIntegrationWithPaths")
    @DisplayName("on building campaign, when campaign xml missing return integration error")
    void testCampaignBuilder04(String path, boolean isXmlIntegration) throws IOException {
        ZipFile zipFile = zipUtils.createZip("data/integration" + path + "/campaign-builder/campaign-missing.zip");

        IntegrationResultUnitDto campaignResult = campaignBuilder.build(zipFile, isXmlIntegration);
        assertThat(campaignResult.getStatus()).isEqualTo(IntegrationStatus.ERROR);
        assertThat(campaignResult.getId()).isNull();
        assertThat(campaignResult.getCause()).containsAnyOf(
                        String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationCampaignBuilder.CAMPAIGN_XML),
                        String.format(IntegrationResultLabel.FILE_NOT_FOUND, IntegrationCampaignBuilder.CAMPAIGN_JSON));
    }

    private static Stream<Arguments> xmlIntegrationWithPaths() {
        return Stream.of(
                Arguments.of("/json", false),
                Arguments.of("/xml", true)
        );
    }
}
