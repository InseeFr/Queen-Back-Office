package fr.insee.queen.api.web.exception;

public class AuthenticationTokenException extends RuntimeException {
    public AuthenticationTokenException(String message) {
        super(message);
    }
}
