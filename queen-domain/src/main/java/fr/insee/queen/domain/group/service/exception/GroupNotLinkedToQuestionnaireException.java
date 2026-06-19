package fr.insee.queen.domain.campaign.service.exception;

public class CampaignNotLinkedToQuestionnaireException extends RuntimeException {
    public static final String MESSAGE = "Questionnaire %s is not linked to campaign %s";

    public CampaignNotLinkedToQuestionnaireException(String campaignId, String questionnaireId) {
        super(String.format(MESSAGE, questionnaireId, campaignId));
    }
}
