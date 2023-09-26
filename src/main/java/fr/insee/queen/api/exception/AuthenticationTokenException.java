package fr.insee.queen.api.exception;

public class AuthenticationTokenException extends RuntimeException {
    public AuthenticationTokenException(String message) {
        super(message);
    }
}
