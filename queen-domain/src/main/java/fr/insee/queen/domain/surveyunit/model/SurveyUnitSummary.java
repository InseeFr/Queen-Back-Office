package fr.insee.queen.domain.surveyunit.model;

import fr.insee.queen.domain.campaign.model.CampaignSummary;

public record SurveyUnitSummary(
        String id,
        String questionnaireId,
        CampaignSummary campaign) {
}
