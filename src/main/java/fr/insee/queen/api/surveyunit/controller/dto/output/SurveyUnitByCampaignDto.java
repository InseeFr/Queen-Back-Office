package fr.insee.queen.api.surveyunit.controller.dto.output;

import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;

public record SurveyUnitByCampaignDto(
        String id,
        String questionnaireId) {
    public static SurveyUnitByCampaignDto fromModel(SurveyUnitSummary summary) {
        return new SurveyUnitByCampaignDto(summary.id(), summary.questionnaireId());
    }
}
