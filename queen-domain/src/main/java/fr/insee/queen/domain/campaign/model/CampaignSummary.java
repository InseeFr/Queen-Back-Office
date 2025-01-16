package fr.insee.queen.domain.campaign.model;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CampaignSummary {
    private final String id;
    private final String label;
    private final CampaignSensitivity sensitivity;
    private final Set<String> questionnaireIds;

    public CampaignSummary(String id, String label, CampaignSensitivity sensitivity, Set<String> questionnaireIds) {
        this.id = id;
        this.label = label;
        this.sensitivity = sensitivity;
        this.questionnaireIds = questionnaireIds;
    }

    public CampaignSummary(String id, String label, CampaignSensitivity sensitivity) {
        this.id = id;
        this.label = label;
        this.sensitivity = sensitivity;
        this.questionnaireIds = new HashSet<>();
    }
}