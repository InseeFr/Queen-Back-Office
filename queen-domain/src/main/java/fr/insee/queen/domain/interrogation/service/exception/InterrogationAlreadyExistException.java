package fr.insee.queen.domain.interrogation.service.exception;

import java.io.Serial;

public class InterrogationAlreadyExistException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -478002885484590035L;

    public InterrogationAlreadyExistException(String message) {
            super(message);
        }
}

