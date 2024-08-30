package fr.insee.queen.jms.service.stub;

import fr.insee.queen.domain.surveyunit.model.SurveyUnitCommand;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitCommandService;
import fr.insee.queen.domain.surveyunit.service.exception.SurveyUnitCommandException;
import lombok.Getter;
import lombok.Setter;

public class SurveyUnitCommandFakeService implements SurveyUnitCommandService {

    public static final String RUNTIME_EXCEPTION_MESSAGE = "runtime exception";

    @Getter
    private SurveyUnitCommand surveyUnitCommandUsed = null;

    @Setter
    private boolean shouldThrowSurveyUnitCommandException = false;

    @Setter
    private boolean shouldThrowRuntimeException = false;

    @Override
    public void createSurveyUnit(SurveyUnitCommand surveyUnitCommand) throws SurveyUnitCommandException {
        if(shouldThrowSurveyUnitCommandException) {
            throw new SurveyUnitCommandException(surveyUnitCommand.questionnaireId());
        }

        if(shouldThrowRuntimeException) {
            throw new RuntimeException(RUNTIME_EXCEPTION_MESSAGE);
        }
        surveyUnitCommandUsed = surveyUnitCommand;
    }
}
