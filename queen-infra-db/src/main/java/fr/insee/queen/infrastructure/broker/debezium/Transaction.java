package fr.insee.queen.infrastructure.broker.debezium;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Transaction(
        String id,
        @JsonProperty("total_order") Long totalOrder,
        @JsonProperty("data_collection_order") Long dataCollectionOrder
) {}