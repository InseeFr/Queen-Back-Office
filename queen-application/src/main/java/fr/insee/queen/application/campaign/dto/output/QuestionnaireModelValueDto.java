package fr.insee.queen.application.campaign.dto.output;

import com.fasterxml.jackson.annotation.JsonRawValue;

public record QuestionnaireModelValueDto(@JsonRawValue String value) {
}
