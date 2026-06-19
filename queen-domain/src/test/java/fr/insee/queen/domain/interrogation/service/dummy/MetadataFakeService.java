package fr.insee.queen.domain.interrogation.service.dummy;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.service.MetadataService;
import lombok.Setter;

public class MetadataFakeService implements MetadataService {

    @Setter
    private ObjectNode metadata;

    @Override
    public ObjectNode getMetadata(String groupId) {
        return metadata;
    }

    @Override
    public ObjectNode getMetadataByQuestionnaireId(String questionnaireId) {
        return metadata;
    }
}
