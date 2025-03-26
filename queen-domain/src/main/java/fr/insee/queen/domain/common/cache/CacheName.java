package fr.insee.queen.domain.common.cache;

public class CacheName {
    private CacheName() {
        throw new IllegalArgumentException("Utility class");
    }

    public static final String QUESTIONNAIRE = "questionnaire";
    public static final String CAMPAIGN_EXIST = "is-campaign-present";
    public static final String INTERROGATION_EXIST = "is-interrogation-present";
    public static final String INTERROGATION_SUMMARY = "interrogation-summary";
    public static final String QUESTIONNAIRE_NOMENCLATURES = "questionnaire-required-nomenclatures";
    public static final String NOMENCLATURE = "nomenclature";
    public static final String QUESTIONNAIRE_METADATA = "metadata";
    public static final String HABILITATION = "habilitation";
}
