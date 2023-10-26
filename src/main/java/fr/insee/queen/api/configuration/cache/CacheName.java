package fr.insee.queen.api.configuration.cache;

public class CacheName {
    private CacheName() {
        throw new IllegalArgumentException("Utility class");
    }

    public static final String QUESTIONNAIRE = "questionnaire";
    public static final String CAMPAIGN_EXIST = "is-campaign-present";
    public static final String QUESTIONNAIRE_NOMENCLATURES = "questionnaire-required-nomenclatures";
    public static final String NOMENCLATURE = "nomenclature";
    public static final String METADATA_BY_QUESTIONNAIRE = "metadata";
    public static final String HABILITATION = "habilitation";
}
