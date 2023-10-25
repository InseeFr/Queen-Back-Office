package fr.insee.queen.api.service.exception;

public class CampaignDeletionException extends RuntimeException {
    public CampaignDeletionException(String message) {
        super(message);
    }
}
