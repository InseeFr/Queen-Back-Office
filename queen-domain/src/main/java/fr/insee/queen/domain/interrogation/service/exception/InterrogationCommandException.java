package fr.insee.queen.domain.interrogation.service.exception;

public class InterrogationCommandException extends RuntimeException {
    public static final String ALREADY_EXECUTED_MESSAGE = "Command %s with same execution already executed. Command %s aborted";
    public static final String CAMPAIGN_NOT_FOUND_MESSAGE = "Campaign for questionnaire %s not found";


    public InterrogationCommandException(String executedCommandId, String abortedCommandId) {
        super(String.format(ALREADY_EXECUTED_MESSAGE, executedCommandId, abortedCommandId));
    }

    public InterrogationCommandException(String questionnaireId) {
        super(String.format(CAMPAIGN_NOT_FOUND_MESSAGE, questionnaireId));
    }
}
