package fr.insee.queen.application.web.validation.exception;

public class JsonValidatorComponentInitializationException extends RuntimeException {

    public JsonValidatorComponentInitializationException(String message) {
        super(message);
    }

    public JsonValidatorComponentInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
