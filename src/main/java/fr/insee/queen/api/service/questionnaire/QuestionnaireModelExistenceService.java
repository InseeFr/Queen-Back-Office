package fr.insee.queen.api.service.questionnaire;

import fr.insee.queen.api.repository.QuestionnaireModelRepository;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.service.exception.QuestionnaireModelServiceException;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QuestionnaireModelExistenceService {
    private final QuestionnaireModelRepository questionnaireModelRepository;
    private final CacheManager cacheManager;

    public boolean existsById(String questionnaireId) {
        return questionnaireModelRepository.existsById(questionnaireId);
    }

    public void throwExceptionIfQuestionnaireNotExist(String questionnaireId) {
        if(!existsById(questionnaireId)) {
            throw new EntityNotFoundException(String.format("Questionnaire model %s was not found", questionnaireId));
        }
    }

    public void throwExceptionIfQuestionnaireAlreadyExist(String questionnaireId) {
        if(existsById(questionnaireId)) {
            throw new QuestionnaireModelServiceException(String.format("Questionnaire model %s already exist", questionnaireId));
        }
    }
}
