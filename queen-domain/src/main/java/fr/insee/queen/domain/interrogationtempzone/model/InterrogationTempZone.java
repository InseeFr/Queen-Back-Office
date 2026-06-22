package fr.insee.queen.domain.interrogationtempzone.model;

import tools.jackson.databind.node.ObjectNode;

import java.util.UUID;

public record InterrogationTempZone(
        UUID id,
        String interrogationId,
        String userId,
        Long date,
        ObjectNode interrogation) {
}
