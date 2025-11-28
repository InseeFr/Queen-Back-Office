package fr.insee.queen.infrastructure.broker.debezium;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Source(
        String version,
        String connector,
        String name,
        @JsonProperty("ts_ms") Long tsMs,
        String snapshot,
        String db,
        String sequence,
        String schema,
        String table,
        @JsonProperty("txId") Long txId,
        Long lsn,
        Long xmin
) {}