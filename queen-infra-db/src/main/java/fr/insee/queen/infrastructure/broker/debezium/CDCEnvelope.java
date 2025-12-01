package fr.insee.queen.infrastructure.broker.debezium;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CDCEnvelope(
        Value before,
        Value after,
        Source source,
        String op,
        @JsonProperty("ts_ms") Long tsMs,
        Transaction transaction
) {}