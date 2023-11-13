package fr.insee.queen.api.depositproof.service.exception;

public class DepositProofException extends RuntimeException {
    public DepositProofException() {
        super("An error has occurred when generating deposit proof");
    }
}
