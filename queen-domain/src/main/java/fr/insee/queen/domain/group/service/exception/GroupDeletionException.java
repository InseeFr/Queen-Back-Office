package fr.insee.queen.domain.campaign.service.exception;

public class CampaignDeletionException extends RuntimeException {
    public CampaignDeletionException(String message) {
        super(message);
    }
}
