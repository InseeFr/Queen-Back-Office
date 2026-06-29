package fr.insee.queen.application.integration.component.builder;

import fr.insee.queen.application.integration.component.builder.schema.SchemaComponent;
import fr.insee.queen.application.integration.dto.input.PartitionsIntegrationData;
import fr.insee.queen.application.web.validation.json.SchemaType;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.integration.service.IntegrationService;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;

/**
 * Handle the integration of a campaign
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "application.group.kind", havingValue = "PARTITION", matchIfMissing = false)
public class IntegrationPartitionsBuilder extends AbstractGroupBuilder<PartitionsIntegrationData> {

    public static final String PARTITIONS_JSON = "partitions.json";

    public IntegrationPartitionsBuilder(SchemaComponent schemaComponent, Validator validator,
                                        IntegrationService integrationService, ObjectMapper mapper) {
        super(schemaComponent, validator, integrationService, mapper);
    }

    @Override
    protected String jsonFileName() {
        return PARTITIONS_JSON;
    }

    @Override
    protected SchemaType schemaType() {
        return SchemaType.PARTITIONS_INTEGRATION;
    }

    @Override
    protected Class<PartitionsIntegrationData> dataType() {
        return PartitionsIntegrationData.class;
    }

    @Override
    protected List<Group> toGroups(PartitionsIntegrationData data, Set<String> questionnaireIds) {
        return PartitionsIntegrationData.toModel(data, questionnaireIds);
    }

    @Override
    protected String errorId(PartitionsIntegrationData data) {
        return data.ids() == null ? null : String.join(", ", data.ids());
    }
}
