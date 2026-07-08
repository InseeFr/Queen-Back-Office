package fr.insee.queen.domain.group.model;

public enum GroupKind {
    CAMPAIGN("campaign", "campaigns", "Campaign"),
    PARTITION("partition", "partitions", "Partitions");

    private final String pathSingular;
    private final String pathPlural;
    private final String forLabel;

    GroupKind(String pathSingular, String pathPlural, String forLabel) {
        this.pathSingular = pathSingular;
        this.pathPlural = pathPlural;
        this.forLabel = forLabel;
    }

    public String getPathSingular() {
        return pathSingular;
    }

    public String getPathPlural() {
        return pathPlural;
    }

    public String getForLabel() {
        return forLabel;
    }
}
