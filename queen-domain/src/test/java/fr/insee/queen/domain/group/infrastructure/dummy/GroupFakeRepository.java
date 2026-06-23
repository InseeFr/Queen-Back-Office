package fr.insee.queen.domain.group.infrastructure.dummy;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.gateway.GroupRepository;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.model.GroupSummary;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GroupFakeRepository implements GroupRepository {

    @Setter
    private boolean groupExists = true;

    @Setter
    private ObjectNode metadata;

    public static final String QUESTIONNAIRE_LINKED_ID = "id-questionnaire1";

    public static final String GROUP_ID = "id-group";

    @Override
    public void create(Group group) {
        // not used at this moment
    }

    @Override
    public boolean exists(String groupId) {
        return groupExists;
    }

    @Override
    public List<GroupSummary> getAllWithQuestionnaireIds() {
        return null;
    }

    @Override
    public void delete(String groupId) {
        // not used at this moment
    }

    @Override
    public Optional<GroupSummary> findWithQuestionnaireIds(String groupId) {
        if(groupExists) {
            return Optional.of(new GroupSummary(GROUP_ID,
                    "label",
                    Set.of(QUESTIONNAIRE_LINKED_ID)));
        }
        return Optional.empty();
    }

    @Override
    public void update(Group group) {
        // not used at this moment
    }

    @Override
    public Optional<ObjectNode> findMetadataByGroupId(String groupId) {
        if(metadata != null) {
            return Optional.of(metadata);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ObjectNode> findMetadataByQuestionnaireId(String questionnaireId) {
        if(metadata != null) {
            return Optional.of(metadata);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Group> findGroup(String groupId) {
        return Optional.empty();
    }

    @Override
    public List<String> getAllGroupIds() {
        return List.of(GROUP_ID);
    }
}
