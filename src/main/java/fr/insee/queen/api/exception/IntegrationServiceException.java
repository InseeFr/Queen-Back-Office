package fr.insee.queen.api.exception;

public class IntegrationServiceException extends RuntimeException {
    public IntegrationServiceException(String message) {
        super(message);
    }
}
