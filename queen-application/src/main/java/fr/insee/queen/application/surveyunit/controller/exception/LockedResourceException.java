package fr.insee.queen.application.surveyunit.controller.exception;

public class LockedResourceException extends Exception {
    public static final String MESSAGE = "Cannot process operation, survey has ended for survey unit %s. Resource is locked";

    public LockedResourceException(String surveyUnitId) {
        super(String.format(MESSAGE, surveyUnitId));
    }
}
