package fr.insee.queen.application.surveyunit.dto.output;

import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;

public record SurveyUnitByCampaignDto(
        String id,
        String questionnaireId) {
    public static SurveyUnitByCampaignDto fromModel(SurveyUnitSummary summary) {
        return new SurveyUnitByCampaignDto(summary.id(), summary.questionnaireId());
    }
}
