package fr.insee.queen.infrastructure.db.campaign.entity;

import fr.insee.queen.domain.campaign.model.CampaignSensitivity;

public record CampaignSummaryRow(String campaignId, String label, CampaignSensitivity sensitivity,
                                 String questionnaireId) {
}

