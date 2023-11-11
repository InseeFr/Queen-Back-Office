package fr.insee.queen.api.campaign.service.exception;

public class CampaignDeletionException extends RuntimeException {
    public CampaignDeletionException(String message) {
        super(message);
    }
}
