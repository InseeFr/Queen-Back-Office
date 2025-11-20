package fr.insee.queen.infrastructure.broker.debezium;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Value(
        String id,
        String payload,
        @JsonProperty("created_date") Long createdDateMicros
) {}