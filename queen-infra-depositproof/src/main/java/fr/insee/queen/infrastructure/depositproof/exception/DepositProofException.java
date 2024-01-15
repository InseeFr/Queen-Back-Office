package fr.insee.queen.infrastructure.depositproof.exception;

public class DepositProofException extends RuntimeException {
    public DepositProofException() {
        super("An error has occurred when generating deposit proof");
    }
}
