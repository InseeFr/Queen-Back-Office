package fr.insee.queen.domain.interrogation.service.exception;

public class InterrogationBatchException extends RuntimeException {
    public static final String GROUP_NOT_FOUND_MESSAGE = "Group for questionnaire %s not found";

    public InterrogationBatchException(String questionnaireId) {
        super(String.format(GROUP_NOT_FOUND_MESSAGE, questionnaireId));
    }
}
