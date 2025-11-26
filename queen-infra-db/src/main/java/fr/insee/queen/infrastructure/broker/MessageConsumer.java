package fr.insee.queen.infrastructure.broker;

import fr.insee.queen.infrastructure.broker.dto.EventDto;
import fr.insee.queen.infrastructure.broker.dto.EventPayloadDto;

public interface MessageConsumer {
    boolean shouldConsume(EventDto.EventTypeEnum type);
    void consume(EventDto.EventTypeEnum type, EventPayloadDto payload);
}
