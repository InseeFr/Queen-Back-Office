package fr.insee.queen.domain.group.service;

import tools.jackson.databind.node.ObjectNode;

public interface MetadataService {
    ObjectNode getMetadata(String groupId);
}
