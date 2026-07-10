package fr.insee.queen.domain.interrogation.service.dummy;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.service.MetadataService;
import lombok.Getter;
import lombok.Setter;

public class MetadataFakeService implements MetadataService {

    @Setter
    private ObjectNode metadata;

    @Getter
    private String requestedGroupId;

    @Override
    public ObjectNode getMetadata(String groupId) {
        this.requestedGroupId = groupId;
        return metadata;
    }
}
