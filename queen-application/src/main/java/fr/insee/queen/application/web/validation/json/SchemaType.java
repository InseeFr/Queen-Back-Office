package fr.insee.queen.application.web.validation.json;

import lombok.Getter;

@Getter
public enum SchemaType {
    CAMPAIGN_INTEGRATION(Names.CAMPAIGN_INTEGRATION),
    NOMENCLATURE_INTEGRATION(Names.NOMENCLATURE_INTEGRATION),
    QUESTIONNAIRE_INTEGRATION(Names.QUESTIONNAIRE_INTEGRATION),
    DATA(Names.DATA),
    COLLECTED_DATA(Names.COLLECTED_DATA),
    METADATA(Names.METADATA),
    PERSONALIZATION(Names.PERSONALIZATION),
    NOMENCLATURE(Names.NOMENCLATURE),
    VARIABLE_TYPE(Names.VARIABLE_TYPE),
    SURVEY_UNIT_TEMP_ZONE(Names.SURVEY_UNIT_TEMP_ZONE);

    public static class Names {
        public static final String CAMPAIGN_INTEGRATION = "schema.campaign-integration.json";
        public static final String QUESTIONNAIRE_INTEGRATION = "schema.questionnaire-integration.json";
        public static final String NOMENCLATURE_INTEGRATION = "schema.nomenclature-integration.json";
        public static final String DATA = "schema.data.json";
        public static final String COLLECTED_DATA = "schema.collected-data.json";
        public static final String METADATA = "schema.metadata.json";
        public static final String PERSONALIZATION = "schema.personalization.json";
        public static final String NOMENCLATURE = "schema.nomenclature.json";
        public static final String VARIABLE_TYPE = "schema.variable-type.json";
        public static final String SURVEY_UNIT_TEMP_ZONE = "schema.survey-unit-temp-zone.json";
        private Names() {

        }
    }
    private final String schemaFileName;

    SchemaType(String schemaFileName) {
        this.schemaFileName = schemaFileName;
    }
}
