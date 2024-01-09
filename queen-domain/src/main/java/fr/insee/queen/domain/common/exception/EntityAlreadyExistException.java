package fr.insee.queen.domain.common.exception;

import java.io.Serial;

public class EntityAlreadyExistException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -478002885484590045L;

    public EntityAlreadyExistException(String message) {
        super(message);
    }
}
