package fr.insee.queen.domain.messaging.port.serverside;

import fr.insee.modelefiliere.EventDto;

import java.util.UUID;

public interface Publisher {
    void publish(EventDto eventDto, UUID correlationId);
}
