package fr.insee.queen.api.dto.surveyunit;

import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.UUID;

public record SurveyUnitTempZoneDto(
		UUID id,
		String surveyUnitId,
		String userId,
		Long date,
		@JsonRawValue
		String surveyUnit) {
}
