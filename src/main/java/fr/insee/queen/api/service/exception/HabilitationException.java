package fr.insee.queen.api.service.exception;

import java.io.Serial;

public class HabilitationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -784002885484508123L;

    public HabilitationException() {
        super("the user does not have the habilitation to perform this action");
    }
}
