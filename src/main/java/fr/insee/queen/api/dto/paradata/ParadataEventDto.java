package fr.insee.queen.api.dto.paradata;

import com.fasterxml.jackson.annotation.JsonRawValue;

public record ParadataEventDto(String id, @JsonRawValue String value) {
}
