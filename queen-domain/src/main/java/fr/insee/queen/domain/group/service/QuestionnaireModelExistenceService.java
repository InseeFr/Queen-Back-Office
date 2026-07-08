package fr.insee.queen.domain.group.service;

public interface QuestionnaireModelExistenceService {
    boolean existsById(String questionnaireId);

    void throwExceptionIfQuestionnaireNotExist(String questionnaireId);

    void throwExceptionIfQuestionnaireAlreadyExist(String questionnaireId);
}
