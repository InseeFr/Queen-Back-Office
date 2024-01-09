package fr.insee.queen.domain.campaign.service.exception;

public class QuestionnaireInvalidException extends RuntimeException {
    public QuestionnaireInvalidException(String message) {
        super(message);
    }
}
