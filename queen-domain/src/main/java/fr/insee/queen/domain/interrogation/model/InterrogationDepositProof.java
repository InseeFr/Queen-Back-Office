package fr.insee.queen.domain.interrogation.model;

import fr.insee.queen.domain.campaign.model.CampaignSummary;

public record InterrogationDepositProof(String id, CampaignSummary campaign, StateData stateData) {
}
