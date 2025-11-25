package fr.insee.queen.infrastructure.broker;

import fr.insee.queen.infrastructure.broker.dto.EventDto;
import fr.insee.queen.infrastructure.broker.dto.EventPayloadDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface MessageConsumer {
    boolean shouldConsume(EventDto.EventTypeEnum type);
    void consume(EventDto.EventTypeEnum type, EventPayloadDto payload);
}
