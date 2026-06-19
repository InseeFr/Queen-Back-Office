package fr.insee.queen.application.interrogation.dto.output;

import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "InterrogationSummary")
public record InterrogationByCampaignDto(
        String id,
        String questionnaireId) {
    public static InterrogationByCampaignDto fromModel(InterrogationSummary summary) {
        return new InterrogationByCampaignDto(summary.id(), summary.questionnaireId());
    }
}
