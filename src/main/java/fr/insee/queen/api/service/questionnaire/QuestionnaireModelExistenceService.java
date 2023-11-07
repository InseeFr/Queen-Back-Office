package fr.insee.queen.api.service.questionnaire;

public interface QuestionnaireModelExistenceService {
    boolean existsById(String questionnaireId);
    void throwExceptionIfQuestionnaireNotExist(String questionnaireId);
    void throwExceptionIfQuestionnaireAlreadyExist(String questionnaireId);
}
