package fr.insee.queen.api.campaign.service;

import fr.insee.queen.api.campaign.service.exception.QuestionnaireModelServiceException;
import fr.insee.queen.api.campaign.service.gateway.QuestionnaireModelRepository;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QuestionnaireModelApiExistenceService implements QuestionnaireModelExistenceService {
    private final QuestionnaireModelRepository questionnaireModelRepository;

    public boolean existsById(String questionnaireId) {
        return questionnaireModelRepository.exists(questionnaireId);
    }

    public void throwExceptionIfQuestionnaireNotExist(String questionnaireId) {
        if (!existsById(questionnaireId)) {
            throw new EntityNotFoundException(String.format("Questionnaire model %s was not found", questionnaireId));
        }
    }

    public void throwExceptionIfQuestionnaireAlreadyExist(String questionnaireId) {
        if (existsById(questionnaireId)) {
            throw new QuestionnaireModelServiceException(String.format("Questionnaire model %s already exist", questionnaireId));
        }
    }
}
