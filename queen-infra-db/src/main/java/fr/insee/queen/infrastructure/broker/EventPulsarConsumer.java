package fr.insee.queen.infrastructure.broker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnProperty(name = "feature.cross-environnement-communication.consummer", havingValue = "true")
@Component
@Slf4j
@RequiredArgsConstructor
public class EventPulsarConsumer {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DebeziumEnvelope(
            Value before,
            Value after,
            Source source,
            String op,
            @JsonProperty("ts_ms") Long tsMs,
            Transaction transaction
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Value(
            String id,
            String payload,
            @JsonProperty("created_date") Long createdDateMicros
    ) {}

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Transaction(
            String id,
            @JsonProperty("total_order") Long totalOrder,
            @JsonProperty("data_collection_order") Long dataCollectionOrder
    ) {}

    private final List<MessageConsumer> consumers;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /*@PulsarListener(
            id="queen-listener",
            subscriptionName = "queen-sub",
            topics = "persistent://public/default/dbserver1.public.outbox",
            schemaType = SchemaType.KEY_VALUE
    )*/
    public void listen(String json, String keyJson) {
        try {
            DebeziumEnvelope record = objectMapper.readValue(json, DebeziumEnvelope.class);
            if (record == null || record.after() == null) {
                return;
            }
            BrokerMessage event = objectMapper.readValue(record.after().payload(), BrokerMessage.class);
            consumers.forEach(c -> {
                if(c.shouldConsume(event.type())){
                    c.consume(event.type(), event.payload());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error when processing Pulsar Json message", e);
        }
    }
}