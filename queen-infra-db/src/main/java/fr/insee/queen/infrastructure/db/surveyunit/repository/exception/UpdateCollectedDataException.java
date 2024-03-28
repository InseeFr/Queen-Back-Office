package fr.insee.queen.infrastructure.db.surveyunit.repository.exception;

public class UpdateCollectedDataException extends RuntimeException {
    public static final String MESSAGE = "Error when updating collected data";
    public UpdateCollectedDataException() {
        super(MESSAGE);
    }
}
