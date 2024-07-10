package fr.insee.queen.application.web.validation.exception;

/**
 * Exception thrown when errors occurred on json validation of specific treatments
 */
public class JsonSchemaValidationException extends RuntimeException {

    public JsonSchemaValidationException(String message) {
        super(message);
    }
}
