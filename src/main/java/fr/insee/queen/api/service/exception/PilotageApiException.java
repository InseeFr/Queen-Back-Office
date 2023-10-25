package fr.insee.queen.api.service.exception;

public class PilotageApiException extends RuntimeException {
    public PilotageApiException() {
        super("Error when requesting Pilotage API");
    }

    public PilotageApiException(String message) {
        super(message);
    }
}
