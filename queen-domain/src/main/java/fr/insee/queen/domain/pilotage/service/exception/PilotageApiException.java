package fr.insee.queen.domain.pilotage.service.exception;

public class PilotageApiException extends RuntimeException {
    public PilotageApiException() {
        super("Error when requesting Pilotage API");
    }

    public PilotageApiException(String message) {
        super(message);
    }
}
