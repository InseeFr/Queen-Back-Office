package fr.insee.queen.domain.surveyunit.service;

import fr.insee.queen.domain.surveyunit.model.SurveyUnitCommand;
import fr.insee.queen.domain.surveyunit.service.exception.SurveyUnitCommandException;

public interface SurveyUnitCommandService {
    /**
     * create a survey unit from a command
     * @param surveyUnitCommand Command for survey unit creation
     * @throws SurveyUnitCommandException exception thrown when command failed
     */
    void createSurveyUnit(SurveyUnitCommand surveyUnitCommand) throws SurveyUnitCommandException;
}
