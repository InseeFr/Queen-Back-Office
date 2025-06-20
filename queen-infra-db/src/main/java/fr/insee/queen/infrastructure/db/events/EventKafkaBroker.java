package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.events.service.EventBroker;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventKafkaBroker implements EventBroker  {
    private final KafkaTemplate<String, ObjectNode> kafkaTemplate;


    @Override
    public void publishEvent(ObjectNode event) {
        kafkaTemplate.send("events", event);
    }
}
