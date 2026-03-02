package fr.insee.queen.infrastructure.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.queen.infrastructure.broker.debezium.CDCEnvelope;
import fr.insee.queen.infrastructure.broker.dto.EventDto;
import fr.insee.queen.infrastructure.db.events.EventsDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnProperty(name = "feature.cross-environment-communication.consumer", havingValue = "true")
@Component
@Slf4j
@RequiredArgsConstructor
public class QueueConsumer {

    private final List<MessageConsumer> consumers;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PulsarListener(
            id="queen-listener",
            subscriptionName = "queen-sub",
            topics = "persistent://public/default/dbserver1.public.outbox",
            schemaType = SchemaType.KEY_VALUE
    )
    public void listen(String json, String keyJson) {
        try {
            CDCEnvelope envelope = objectMapper.readValue(json, CDCEnvelope.class);
            if (envelope == null || envelope.after() == null) {
                return;
            }
            EventDto event = objectMapper.readValue(envelope.after().payload(), EventDto.class);
            consumers.forEach(c -> {
                if(c.shouldConsume(event.getEventType())){
                    c.consume(event.getEventType(), event.getPayload());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error when processing Pulsar Json message", e);
        }
    }
}
