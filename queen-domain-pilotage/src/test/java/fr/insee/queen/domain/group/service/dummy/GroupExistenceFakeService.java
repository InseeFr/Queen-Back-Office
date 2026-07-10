package fr.insee.queen.domain.group.service.dummy;

import fr.insee.queen.domain.group.service.GroupExistenceService;
import lombok.Getter;
import lombok.Setter;

public class GroupExistenceFakeService implements GroupExistenceService {

    @Setter
    private boolean groupExist = true;
    @Getter
    private boolean checkGroupNotExist = false;
    @Getter
    private boolean checkGroupExist = false;

    @Override
    public void throwExceptionIfGroupNotExist(String groupId) {
        checkGroupExist = true;
    }

    @Override
    public void throwExceptionIfGroupAlreadyExist(String groupId) {
        checkGroupNotExist = true;
    }

    @Override
    public void throwExceptionIfGroupNotLinkedToQuestionnaire(String groupId, String questionnaireId) {
        // not implemented yet
    }

    @Override
    public boolean existsById(String groupId) {
        return groupExist;
    }
}
