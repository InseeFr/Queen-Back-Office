package fr.insee.queen.domain.interrogation.model;

import fr.insee.queen.domain.campaign.model.CampaignSummary;

public record InterrogationSummary(
        String id,
        String questionnaireId,
        CampaignSummary campaign) {
}
