package fr.insee.queen.domain.group.service;

import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.infrastructure.dummy.GroupFakeRepository;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MetadataServiceTest {
    private MetadataService metadataService;
    private GroupFakeRepository groupRepository;
    private ObjectNode metadata;

    @BeforeEach
    void init() {
        metadata = JsonNodeFactory.instance.objectNode();
        metadata.put("field", "value");
        groupRepository = new GroupFakeRepository();
        metadataService = new MetadataApiService(groupRepository);
    }

    @Test
    @DisplayName("When retrieving metadata for questionnaire, throws exception if metadata not found")
    void testMetadataQuestionnaire01() {
        groupRepository.setMetadata(null);
        assertThatThrownBy(() -> metadataService.getMetadataByQuestionnaireId(GroupFakeRepository.QUESTIONNAIRE_LINKED_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When retrieving metadata for questionnaire, return metadata")
    void testMetadataQuestionnaire02() {
        groupRepository.setMetadata(metadata);
        assertThat(metadataService.getMetadataByQuestionnaireId(GroupFakeRepository.QUESTIONNAIRE_LINKED_ID))
                .isEqualTo(metadata);
    }

    @Test
    @DisplayName("When retrieving metadata for group, throws exception if metadata not found")
    void testMetadataGroup01() {
        groupRepository.setMetadata(null);
        assertThatThrownBy(() -> metadataService.getMetadata(GroupFakeRepository.GROUP_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When retrieving metadata for group, return metadata")
    void testMetadataGroup02() {
        groupRepository.setMetadata(metadata);
        assertThat(metadataService.getMetadata(GroupFakeRepository.QUESTIONNAIRE_LINKED_ID))
                .isEqualTo(metadata);
    }
}
