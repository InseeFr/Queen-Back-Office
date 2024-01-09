package fr.insee.queen.domain.campaign.service.dummy;

import fr.insee.queen.domain.campaign.service.QuestionnaireModelExistenceService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class QuestionnaireModelExistenceFakeService implements QuestionnaireModelExistenceService {

    @Setter
    private boolean questionnaireExist = true;
    @Getter
    private boolean checkQuestionnaireNotExist = false;
    @Getter
    private boolean checkQuestionnaireExist = false;

    @Override
    public boolean existsById(String questionnaireId) {
        return questionnaireExist;
    }

    @Override
    public void throwExceptionIfQuestionnaireNotExist(String questionnaireId) {
        checkQuestionnaireExist = true;
    }

    @Override
    public void throwExceptionIfQuestionnaireAlreadyExist(String questionnaireId) {
        checkQuestionnaireNotExist = true;
    }
}
