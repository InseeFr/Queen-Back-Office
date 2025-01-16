package fr.insee.queen.application.surveyunit.controller.exception;

public class ConflictException extends Exception {
    public static final String MESSAGE = "Cannot process operation, survey unit %s has finished answering survey";

    public ConflictException(String surveyUnitId) {
        super(String.format(MESSAGE, surveyUnitId));
    }
}
