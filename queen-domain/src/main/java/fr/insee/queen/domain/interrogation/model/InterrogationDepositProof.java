package fr.insee.queen.domain.interrogation.model;

import fr.insee.queen.domain.group.model.GroupSummary;

public record InterrogationDepositProof(String id, String surveyUnitId, GroupSummary group, StateData stateData) {
}
