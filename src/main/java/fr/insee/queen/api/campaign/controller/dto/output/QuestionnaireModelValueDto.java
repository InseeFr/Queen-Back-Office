package fr.insee.queen.api.campaign.controller.dto.output;

import com.fasterxml.jackson.annotation.JsonRawValue;

public record QuestionnaireModelValueDto(@JsonRawValue String value) {
}
