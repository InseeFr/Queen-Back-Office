package fr.insee.queen.domain.interrogation.service.exception;

public class InterrogationBatchException extends RuntimeException {
    public static final String CAMPAIGN_NOT_FOUND_MESSAGE = "Campaign for questionnaire %s not found";

    public InterrogationBatchException(String questionnaireId) {
        super(String.format(CAMPAIGN_NOT_FOUND_MESSAGE, questionnaireId));
    }
}
