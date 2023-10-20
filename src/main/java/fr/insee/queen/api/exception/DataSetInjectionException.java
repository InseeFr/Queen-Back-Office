package fr.insee.queen.api.exception;

public class DataSetInjectionException extends RuntimeException {
    public DataSetInjectionException() {
        super("An error has occurred when creating dataset");
    }
}
