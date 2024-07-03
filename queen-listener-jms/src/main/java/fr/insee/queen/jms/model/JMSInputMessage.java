package fr.insee.queen.jms.model;

import fr.insee.modelefiliere.InterrogationDto;

public record JMSInputMessage(
        String correlationID,
        String replyTo,
        InterrogationDto payload
) {
}
