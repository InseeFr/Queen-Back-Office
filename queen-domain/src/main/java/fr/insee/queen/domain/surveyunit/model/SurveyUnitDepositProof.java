package fr.insee.queen.domain.surveyunit.model;

import fr.insee.queen.domain.campaign.model.CampaignSummary;

public record SurveyUnitDepositProof(String id, CampaignSummary campaign, StateData stateData) {
}
