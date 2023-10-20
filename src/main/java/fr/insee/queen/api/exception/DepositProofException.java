package fr.insee.queen.api.exception;

public class DepositProofException extends RuntimeException {
    public DepositProofException() {
        super("An error has occurred when generating deposit proof");
    }
}
