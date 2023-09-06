package fr.insee.queen.api.dto.metadata;

import com.fasterxml.jackson.annotation.JsonRawValue;

public record MetadataDto (@JsonRawValue String value) {
}
