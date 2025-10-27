package fr.insee.queen.domain.events.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.util.UUID;

public record Event(UUID id, ObjectNode value, LocalDateTime createdDate) {
}
