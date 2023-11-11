package fr.insee.queen.api.web.exception;

import java.io.Serial;

public class EntityNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -784002885484509004L;

    public EntityNotFoundException(String message) {
        super(message);
    }
}
