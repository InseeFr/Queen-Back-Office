package fr.insee.queen.domain.surveyunit.service.exception;

public class SurveyUnitCommandException extends RuntimeException {
    public static final String ALREADY_EXECUTED_MESSAGE = "Command %s with same execution already executed. Command %s aborted";
    public static final String CAMPAIGN_NOT_FOUND_MESSAGE = "Campaign for questionnaire %s not found";


    public SurveyUnitCommandException(String executedCommandId, String abortedCommandId) {
        super(String.format(ALREADY_EXECUTED_MESSAGE, executedCommandId, abortedCommandId));
    }

    public SurveyUnitCommandException(String questionnaireId) {
        super(String.format(CAMPAIGN_NOT_FOUND_MESSAGE, questionnaireId));
    }
}
