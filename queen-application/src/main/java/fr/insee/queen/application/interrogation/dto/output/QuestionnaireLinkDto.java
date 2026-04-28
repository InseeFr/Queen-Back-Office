package fr.insee.queen.application.interrogation.dto.output;

import fr.insee.queen.domain.interrogation.model.QuestionnaireLink;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "QuestionnaireLink")
public record QuestionnaireLinkDto(
        @Schema(description = "Interrogation ID", example = "interro1")
        String interrogationId,

        @Schema(description = "Questionnaire ID", example = "quest1")
        String questionnaireId
) {
    public static QuestionnaireLinkDto fromModel(QuestionnaireLink questionnaireLink) {
        return new QuestionnaireLinkDto(
                questionnaireLink.interrogationId(),
                questionnaireLink.questionnaireId()
        );
    }
}