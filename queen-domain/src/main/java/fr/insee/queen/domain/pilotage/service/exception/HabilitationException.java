package fr.insee.queen.domain.pilotage.service.exception;

import java.io.Serial;

public class HabilitationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -784002885484508123L;

    public HabilitationException(String message) {
        super(message);
    }
}
