package fr.insee.queen.domain.depositproof.model;

import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.surveyunit.model.StateData;

public record SurveyUnitDepositProof(String id, CampaignSummary campaign, StateData stateData) {
}
