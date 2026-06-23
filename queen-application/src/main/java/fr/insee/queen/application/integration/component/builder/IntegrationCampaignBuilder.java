package fr.insee.queen.application.integration.component.builder;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import fr.insee.queen.application.integration.component.builder.schema.SchemaComponent;
import fr.insee.queen.application.integration.component.exception.IntegrationValidationException;
import fr.insee.queen.application.integration.dto.input.CampaignIntegrationData;
import fr.insee.queen.application.integration.dto.output.IntegrationResultUnitDto;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.integration.model.IntegrationResult;
import fr.insee.queen.domain.integration.model.IntegrationResultLabel;
import fr.insee.queen.domain.integration.service.IntegrationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handle the integration of a campaign
 */
@Component
@Slf4j
@AllArgsConstructor
public class IntegrationCampaignBuilder implements CampaignBuilder {
    private final SchemaComponent schemaComponent;
    private final Validator validator;
    private final IntegrationService integrationService;
    private final ObjectMapper mapper;
    public static final String CAMPAIGN_JSON = "campaign.json";

    @Override
    public IntegrationResultUnitDto build(ZipFile integrationZipFile) {
        return buildCampaign(integrationZipFile);
    }

    private IntegrationResultUnitDto buildCampaign(ZipFile zf) {
        try {
            schemaComponent.throwExceptionIfJsonDataFileNotValid(zf, CAMPAIGN_JSON, SchemaType.CAMPAIGN_INTEGRATION);
            ZipEntry zipCampaignFile = zf.getEntry(CAMPAIGN_JSON);
            CampaignIntegrationData campaign = mapper.readValue(zf.getInputStream(zipCampaignFile), CampaignIntegrationData.class);
            return buildCampaign(campaign);
        } catch (IntegrationValidationException ex) {
            return ex.getResultError();
        }  catch (JacksonException _) {
            return IntegrationResultUnitDto.integrationResultUnitError(
                    null,
                    IntegrationResultLabel.JSON_PARSING_ERROR.formatted(CAMPAIGN_JSON));
        } catch (IOException _) {
            return IntegrationResultUnitDto.integrationResultUnitError(
                    null,
                    IntegrationResultLabel.ZIP_PARSING_ERROR.formatted(zf.getName()));
        }
    }

    private IntegrationResultUnitDto buildCampaign(CampaignIntegrationData campaign) {
        Set<ConstraintViolation<CampaignIntegrationData>> violations = validator.validate(campaign);
        if (!violations.isEmpty()) {
            StringBuilder violationMessage = new StringBuilder();
            for (ConstraintViolation<CampaignIntegrationData> violation : violations) {
                violationMessage
                        .append(violation.getPropertyPath().toString())
                        .append(": ")
                        .append(violation.getMessage())
                        .append(". ");
            }
            return IntegrationResultUnitDto.integrationResultUnitError(campaign.id(), violationMessage.toString());
        }
        IntegrationResult result = integrationService.create(CampaignIntegrationData.toModel(campaign));
        return IntegrationResultUnitDto.fromModel(result);
    }
}
