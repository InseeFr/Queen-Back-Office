package fr.insee.queen.api.campaign.service.exception;

public class QuestionnaireInvalidException extends RuntimeException {
    public QuestionnaireInvalidException(String message) {
        super(message);
    }
}
