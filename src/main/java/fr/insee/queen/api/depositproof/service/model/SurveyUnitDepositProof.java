package fr.insee.queen.api.depositproof.service.model;

import fr.insee.queen.api.campaign.service.model.CampaignSummary;
import fr.insee.queen.api.surveyunit.service.model.StateData;

public record SurveyUnitDepositProof(String id, CampaignSummary campaign, StateData stateData) {
}
