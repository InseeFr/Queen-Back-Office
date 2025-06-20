package fr.insee.queen.domain.events.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.UUID;

@Getter()
public class Event {
    private final UUID id;
    private final ObjectNode value;

    public Event(UUID id, ObjectNode value) {
        this.id = id;
        this.value = value;
    }
}
