package fr.insee.queen.domain.interrogation.service.exception;

public class MetadataValueNotFoundException extends RuntimeException {
    public static final String ERROR_MESSAGE = "Metadata key %s is incorrect or does not exist";

    public MetadataValueNotFoundException(String metadataKey) {
        super(String.format(ERROR_MESSAGE, metadataKey));
    }
}
