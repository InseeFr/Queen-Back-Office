package fr.insee.queen.application.group.dto.output;

import tools.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "QuestionnaireModelValue")
public record QuestionnaireModelValueDto(ObjectNode value) {
}
