package fr.insee.queen.domain.registre.service.exception;

public class CodeListNotFoundException extends RuntimeException {

    public CodeListNotFoundException(String message) {
        super(message);
    }
}