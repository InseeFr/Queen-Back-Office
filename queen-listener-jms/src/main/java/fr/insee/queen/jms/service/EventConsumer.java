package fr.insee.queen.jms.service;

import fr.insee.modelefiliere.EventDto;

/**
 * Interface for consuming events from the multimode topic.
 * Implementations of this interface will be called when an event is received.
 */
public interface EventConsumer {

    /**
     * Consumes an event received from the topic.
     *
     * @param eventDto the event to consume
     */
    void consume(EventDto eventDto);
}