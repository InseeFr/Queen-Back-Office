package fr.insee.queen.jms.service;

import fr.insee.modelefiliere.EventDto;

/**
 * Interface for consuming events from the multimode topic.
 * Implementations of this interface will be called when an event is received.
 */
public interface EventConsumer {

    /**
     * Checks if the event can be consumed for a given interrogation.
     * By default, all events can be consumed.
     *
     * @param interrogationId the interrogation id to check
     * @return true if the event can be consumed, false otherwise
     */
    default boolean canConsume(String interrogationId) {
        return true;
    }

    /**
     * Consumes an event received from the topic.
     *
     * @param eventDto the event to consume
     */
    void consume(EventDto eventDto);
}