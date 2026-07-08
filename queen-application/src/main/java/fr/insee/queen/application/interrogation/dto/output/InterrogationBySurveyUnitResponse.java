package fr.insee.queen.application.interrogation.dto.output;

import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "InterrogationnBySurveyUnitSummary")
public record InterrogationBySurveyUnitResponse(
        String interrogationId,
        String groupId) {
    public static InterrogationBySurveyUnitResponse fromModel(InterrogationSummary summary) {
        return new InterrogationBySurveyUnitResponse(summary.id(), summary.group().getId());
    }
}