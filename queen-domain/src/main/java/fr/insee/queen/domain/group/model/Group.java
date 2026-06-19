package fr.insee.queen.domain.group.model;

import tools.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Group {
    private final String id;
    private final String label;
    private final Set<String> questionnaireIds;
    private final ObjectNode metadata;

    public Group(String id, String label, @NonNull Set<String> questionnaireIds, ObjectNode metadata) {
        this.id = id;
        this.label = label;
        this.questionnaireIds = questionnaireIds;
        this.metadata = metadata;
    }

    public Group(String id, String label, ObjectNode metadata) {
        this.id = id;
        this.label = label;
        this.questionnaireIds = new HashSet<>();
        this.metadata = metadata;
    }
}
