package fr.insee.queen.application.group.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "QuestionnaireModelId")
public record QuestionnaireModelIdDto(String questionnaireId) {
}
