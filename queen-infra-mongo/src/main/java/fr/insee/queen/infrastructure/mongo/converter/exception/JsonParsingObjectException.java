package fr.insee.queen.infrastructure.mongo.converter.exception;

public class JsonParsingObjectException extends RuntimeException {
    public JsonParsingObjectException(String message) {
        super(message);
    }
}
