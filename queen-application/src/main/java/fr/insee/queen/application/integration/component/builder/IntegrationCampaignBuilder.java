package fr.insee.queen.application.integration.component.builder;

import fr.insee.queen.domain.group.model.Group;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tools.jackson.databind.ObjectMapper;
import fr.insee.queen.application.integration.component.builder.schema.SchemaComponent;
import fr.insee.queen.application.integration.dto.input.CampaignIntegrationData;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.integration.service.IntegrationService;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


/**
 * Handle the integration of a campaign
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "application.group.kind", havingValue = "CAMPAIGN", matchIfMissing = true)
public class IntegrationCampaignBuilder extends AbstractGroupBuilder<CampaignIntegrationData> {
    public static final String CAMPAIGN_JSON = "campaign.json";

    public IntegrationCampaignBuilder(SchemaComponent schemaComponent, Validator validator,
                                      IntegrationService integrationService, ObjectMapper mapper) {
        super(schemaComponent, validator, integrationService, mapper);
    }

    @Override
    protected String jsonFileName() {
        return CAMPAIGN_JSON;
    }

    @Override
    protected SchemaType schemaType() {
        return SchemaType.CAMPAIGN_INTEGRATION;
    }

    @Override
    protected Class<CampaignIntegrationData> dataType() {
        return CampaignIntegrationData.class;
    }

    @Override
    protected List<Group> toGroups(CampaignIntegrationData data, Set<String> questionnaireIds) {
        return List.of(CampaignIntegrationData.toModel(data, questionnaireIds));
    }

    @Override
    protected String errorId(CampaignIntegrationData data) {
        return data.id();
    }
}
