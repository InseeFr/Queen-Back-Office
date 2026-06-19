package fr.insee.queen.domain.group.service;

import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.model.GroupSummary;

import java.util.List;

public interface GroupService {
    List<GroupSummary> getAllGroups();

    void delete(String groupId, boolean deleteInterrogations);

    void createGroup(Group groupData);

    void updateGroup(Group groupData);

    Group getGroup(String groupId);

    List<String> getAllGroupIds();
}
