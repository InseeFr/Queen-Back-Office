package fr.insee.queen.domain.group.model;

import lombok.Getter;

@Getter
public enum GroupKind {
    CAMPAIGN("campaign", "campaigns", "campaigns", "Campaign"),
    PARTITION("partition", "partitions", "partitionings", "Partitions");

    private final String pathSingular;
    private final String pathPlural;
    private final String pathPilotage;
    private final String forLabel;

    GroupKind(String pathSingular, String pathPlural, String pathPilotage, String forLabel) {
        this.pathSingular = pathSingular;
        this.pathPlural = pathPlural;
        this.pathPilotage = pathPilotage;
        this.forLabel = forLabel;
    }
}
