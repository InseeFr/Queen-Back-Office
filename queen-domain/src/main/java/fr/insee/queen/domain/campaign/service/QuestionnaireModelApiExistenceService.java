package fr.insee.queen.domain.campaign.service;

import fr.insee.queen.domain.campaign.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QuestionnaireModelApiExistenceService implements QuestionnaireModelExistenceService {
    private final QuestionnaireModelRepository questionnaireModelRepository;
    public static final String NOT_FOUND_MESSAGE = "Questionnaire model %s was not found";
    public static final String ALREADY_EXIST_MESSAGE = "Questionnaire model %s already exist";

    public boolean existsById(String questionnaireId) {
        return questionnaireModelRepository.exists(questionnaireId);
    }

    @Override
    public void throwExceptionIfQuestionnaireNotExist(String questionnaireId) {
        if (!existsById(questionnaireId)) {
            throw new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, questionnaireId));
        }
    }

    @Override
    public void throwExceptionIfQuestionnaireAlreadyExist(String questionnaireId) {
        if (existsById(questionnaireId)) {
            throw new EntityAlreadyExistException(String.format(ALREADY_EXIST_MESSAGE, questionnaireId));
        }
    }
}
