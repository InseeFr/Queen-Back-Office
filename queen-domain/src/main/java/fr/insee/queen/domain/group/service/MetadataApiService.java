package fr.insee.queen.domain.group.service;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.gateway.GroupRepository;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MetadataApiService implements MetadataService {

    private final GroupRepository groupRepository;

    @Override
    @Cacheable(CacheName.GROUP_METADATA)
    public ObjectNode getMetadata(String groupId) {
        return groupRepository.findMetadataByGroupId(groupId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Metadata for group %s was not found", groupId)));
    }
}
