package fr.insee.queen.application.configuration.properties;

public enum GroupKind {
    CAMPAIGN("campaign", "campaigns"),
    PARTITION("partition", "partitions");

    private final String pathSingular;
    private final String pathPlural;

    GroupKind(String pathSingular, String pathPlural) {
        this.pathSingular = pathSingular;
        this.pathPlural = pathPlural;
    }

    public String getPathSingular() {
        return pathSingular;
    }

    public String getPathPlural() {
        return pathPlural;
    }
}
