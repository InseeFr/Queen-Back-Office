package fr.insee.queen.application.group.service.dummy;

import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.model.GroupSummary;
import fr.insee.queen.domain.group.service.GroupService;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class GroupFakeService implements GroupService {

    private boolean deleted = false;
    private boolean updated = false;
    private boolean created = false;

    public static final String GROUP1_ID = "allGroups1";
    public static final List<GroupSummary> GROUP_SUMMARY_LIST = List.of(
            new GroupSummary(GROUP1_ID, "label", Set.of("questionnaireId1", "questionnaireId2")),
            new GroupSummary("allGroups2", "label", Set.of("questionnaireId1", "questionnaireId2"))
    );

    @Override
    public List<GroupSummary> getAllGroups() {
        return GROUP_SUMMARY_LIST;
    }

    @Override
    public void delete(String groupId, boolean deleteInterrogations) {
        this.deleted = true;
    }

    @Override
    public void createGroup(Group groupData) {
        created = true;
    }

    @Override
    public void updateGroup(Group groupData) {
        updated = true;
    }

    @Override
    public Group getGroup(String groupId) {
        return null;
    }

    @Override
    public List<String> getAllGroupIds() {
        return List.of(GROUP1_ID);
    }
}
