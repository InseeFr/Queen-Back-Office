package fr.insee.queen.domain.registre.service.exception;

import java.io.Serial;

/**
 * Exception thrown when trying to create a resource that already exists.
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -784002885484509007L;

    /**
     * Creates a new ResourceAlreadyExistsException with the specified message.
     *
     * @param message the error message
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Creates a new ResourceAlreadyExistsException with the specified message and cause.
     *
     * @param message the error message
     * @param cause the cause of the exception
     */
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}