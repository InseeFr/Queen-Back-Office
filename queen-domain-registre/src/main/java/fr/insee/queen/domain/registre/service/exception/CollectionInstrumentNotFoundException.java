package fr.insee.queen.domain.registre.service.exception;

public class CollectionInstrumentNotFoundException extends RuntimeException {

    public CollectionInstrumentNotFoundException(String message) {
        super(message);
    }
}