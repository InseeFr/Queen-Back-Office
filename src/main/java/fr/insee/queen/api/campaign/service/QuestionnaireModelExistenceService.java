package fr.insee.queen.api.campaign.service;

public interface QuestionnaireModelExistenceService {
    boolean existsById(String questionnaireId);

    void throwExceptionIfQuestionnaireNotExist(String questionnaireId);

    void throwExceptionIfQuestionnaireAlreadyExist(String questionnaireId);
}
