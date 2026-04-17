package fr.insee.queen.domain.registre.service.exception;

public class RegistreAuthException extends RuntimeException {
    public RegistreAuthException(String message) {
        super(message);
    }

    public RegistreAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}