package fr.insee.queen.application.interrogation.controller.exception;

public class LockedResourceException extends Exception {
    public static final String MESSAGE = "Cannot process operation, survey has ended for interrogation %s. Resource is locked";

    public LockedResourceException(String interrogationId) {
        super(String.format(MESSAGE, interrogationId));
    }
}
