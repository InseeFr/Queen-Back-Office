package fr.insee.queen.domain.interrogation.model;

import tools.jackson.databind.node.ObjectNode;

public record InterrogationMetadata(
        InterrogationPersonalization interrogationPersonalization,
        ObjectNode metadata) {

    public static InterrogationMetadata create(InterrogationPersonalization interrogationPersonalization, ObjectNode metadata) {
        return new InterrogationMetadata(interrogationPersonalization, metadata);
    }
}
