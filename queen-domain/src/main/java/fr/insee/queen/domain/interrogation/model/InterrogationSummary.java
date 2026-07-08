package fr.insee.queen.domain.interrogation.model;

import fr.insee.queen.domain.group.model.GroupSummary;

public record InterrogationSummary(
        String id,
        String surveyUnitId,
        String questionnaireId,
        GroupSummary group) {
}
