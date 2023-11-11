package fr.insee.queen.api.campaign.service.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Campaign {
    private final String id;
    private final String label;
    private final Set<String> questionnaireIds;
    private final String metadata;

    public Campaign(String id, String label, @NonNull Set<String> questionnaireIds, String metadata) {
        this.id = id;
        this.label = label;
        this.questionnaireIds = questionnaireIds;
        this.metadata = metadata;
    }

    public Campaign(String id, String label, String metadata) {
        this.id = id;
        this.label = label;
        this.questionnaireIds = new HashSet<>();
        this.metadata = metadata;
    }
}
