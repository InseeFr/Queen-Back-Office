package fr.insee.queen.infrastructure.db.campaign.entity;

import fr.insee.queen.domain.campaign.model.CampaignSensitivity;

public class CampaignSummaryRow {
    private final String campaignId;
    private final String label;
    private final CampaignSensitivity sensitivity;
    private final String questionnaireId; // peut être null

    public CampaignSummaryRow(String campaignId, String label, CampaignSensitivity sensitivity, String questionnaireId) {
        this.campaignId = campaignId;
        this.label = label;
        this.sensitivity = sensitivity;
        this.questionnaireId = questionnaireId;
    }

    public String getCampaignId() { return campaignId; }
    public String getLabel() { return label; }
    public CampaignSensitivity getSensitivity() { return sensitivity; }
    public String getQuestionnaireId() { return questionnaireId; }
}
