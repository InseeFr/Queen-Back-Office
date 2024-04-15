package fr.insee.queen.application.campaign.dto.output;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "QuestionnaireModelValue")
public record QuestionnaireModelValueDto(@JsonRawValue String value) {
}
