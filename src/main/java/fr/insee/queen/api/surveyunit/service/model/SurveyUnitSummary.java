package fr.insee.queen.api.surveyunit.service.model;

public record SurveyUnitSummary(
        String id,
        String questionnaireId,
        String campaignId) {
}
