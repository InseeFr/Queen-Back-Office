package fr.insee.queen.api.dto.personalization;

import com.fasterxml.jackson.annotation.JsonRawValue;

public record PersonalizationDto(@JsonRawValue String value){}
