package fr.insee.queen.domain.surveyunit.service.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.service.MetadataService;
import lombok.Setter;

public class MetadataFakeService implements MetadataService {

    @Setter
    private ObjectNode metadata;

    @Override
    public ObjectNode getMetadata(String campaignId) {
        return metadata;
    }

    @Override
    public ObjectNode getMetadataByQuestionnaireId(String questionnaireId) {
        return metadata;
    }
}
