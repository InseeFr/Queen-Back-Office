package fr.insee.queen.domain.group.model;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class GroupSummary {
    private final String id;
    private final String label;
    private final Set<String> questionnaireIds;

    public GroupSummary(String id, String label, Set<String> questionnaireIds) {
        this.id = id;
        this.label = label;
        this.questionnaireIds = questionnaireIds;
    }

    public GroupSummary(String id, String label) {
        this.id = id;
        this.label = label;
        this.questionnaireIds = new HashSet<>();
    }
}