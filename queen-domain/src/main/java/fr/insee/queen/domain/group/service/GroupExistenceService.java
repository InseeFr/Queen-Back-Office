package fr.insee.queen.domain.group.service;

public interface GroupExistenceService {
    void throwExceptionIfGroupNotExist(String groupId);

    void throwExceptionIfGroupAlreadyExist(String groupId);

    void throwExceptionIfGroupNotLinkedToQuestionnaire(String groupId, String questionnaireId);

    boolean existsById(String groupId);
}
