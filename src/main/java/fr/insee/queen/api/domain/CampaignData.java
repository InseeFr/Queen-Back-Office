package fr.insee.queen.api.domain;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CampaignData {
    private String id;
    private String label;
    private Set<String> questionnaireIds;
    private String metadata;

    public CampaignData(String id, String label, @NonNull Set<String> questionnaireIds, String metadata) {
        this.id = id;
        this.label = label;
        this.questionnaireIds = questionnaireIds;
        this.metadata = metadata;
    }

    public CampaignData(String id, String label, String metadata) {
        this.id = id;
        this.label = label;
        this.questionnaireIds = new HashSet<>();
        this.metadata = metadata;
    }
}
