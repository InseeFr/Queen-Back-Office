package fr.insee.queen.application.interrogation.dto.output;

import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "InterrogationSummary")
public record InterrogationBySurveyUnitDto(
        String interrogationId,
        String campaignId) {
    public static InterrogationBySurveyUnitDto fromModel(InterrogationSummary summary) {
        return new InterrogationBySurveyUnitDto(summary.id(), summary.campaign().getId());
    }
}