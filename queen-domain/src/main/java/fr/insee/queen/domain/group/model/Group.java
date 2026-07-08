package fr.insee.queen.domain.group.model;

import tools.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NonNull;

import java.util.Set;

@Getter
public class Group {
    private final String id;
    private final String label;
    private final String shortLabel;
    private final Set<String> questionnaireIds;
    private final ObjectNode metadata;

    public Group(String id, String label, @NonNull Set<String> questionnaireIds, ObjectNode metadata) {
        this(id, label, id, questionnaireIds, metadata);
    }

    public Group(String id, String label, @NonNull String shortLabel, @NonNull Set<String> questionnaireIds, ObjectNode metadata) {
        this.id = id;
        this.label = label;
        this.shortLabel = shortLabel;
        this.questionnaireIds = questionnaireIds;
        this.metadata = metadata;
    }
}
