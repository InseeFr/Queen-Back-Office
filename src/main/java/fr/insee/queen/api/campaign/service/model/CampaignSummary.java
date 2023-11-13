package fr.insee.queen.api.campaign.service.model;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CampaignSummary {
    private final String id;
    private final String label;
    private final Set<String> questionnaireIds;

    public CampaignSummary(String id, String label, Set<String> questionnaireIds) {
        this.id = id;
        this.label = label;
        this.questionnaireIds = questionnaireIds;
    }

    public CampaignSummary(String id, String label) {
        this.id = id;
        this.label = label;
        this.questionnaireIds = new HashSet<>();
    }
}