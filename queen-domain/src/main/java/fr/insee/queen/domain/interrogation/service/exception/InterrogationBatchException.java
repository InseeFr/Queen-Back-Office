package fr.insee.queen.domain.interrogation.service.exception;

public class InterrogationBatchException extends RuntimeException {
    public static final String ALREADY_EXECUTED_MESSAGE = "Command %s with same execution already executed. Command %s aborted";
    public static final String CAMPAIGN_NOT_FOUND_MESSAGE = "Campaign for questionnaire %s not found";


    public InterrogationBatchException(String executedCommandId, String abortedCommandId) {
        super(String.format(ALREADY_EXECUTED_MESSAGE, executedCommandId, abortedCommandId));
    }

    public InterrogationBatchException(String questionnaireId) {
        super(String.format(CAMPAIGN_NOT_FOUND_MESSAGE, questionnaireId));
    }
}
