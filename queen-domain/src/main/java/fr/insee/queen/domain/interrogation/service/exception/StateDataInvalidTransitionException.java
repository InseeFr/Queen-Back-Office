package fr.insee.queen.domain.interrogation.service.exception;

public class StateDataInvalidTransitionException extends RuntimeException {
    public StateDataInvalidTransitionException(String message) {
        super(message);
    }
}
