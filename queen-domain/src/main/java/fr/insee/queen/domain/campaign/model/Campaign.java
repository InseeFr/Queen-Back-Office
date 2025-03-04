package fr.insee.queen.domain.campaign.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Campaign {
    private final String id;
    private final String label;
    private final CampaignSensitivity sensitivity;
    private final Set<String> questionnaireIds;
    private final ObjectNode metadata;

    public Campaign(String id, String label, @NonNull CampaignSensitivity sensitivity, @NonNull Set<String> questionnaireIds, ObjectNode metadata) {
        this.id = id;
        this.label = label;
        this.sensitivity = sensitivity;
        this.questionnaireIds = questionnaireIds;
        this.metadata = metadata;
    }

    public Campaign(String id, String label, @NonNull CampaignSensitivity sensitivity, ObjectNode metadata) {
        this.id = id;
        this.label = label;
        this.sensitivity = sensitivity;
        this.questionnaireIds = new HashSet<>();
        this.metadata = metadata;
    }
}
