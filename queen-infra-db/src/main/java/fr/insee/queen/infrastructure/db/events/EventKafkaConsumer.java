package fr.insee.queen.infrastructure.db.events;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "feature.events.scheduler.enabled", havingValue = "true")
public class EventKafkaConsumer {

    @KafkaListener(id="events-listener", topics = "events")
    public void listen(ObjectNode in){
        System.out.println(in);
    }
}
