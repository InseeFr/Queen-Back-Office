package fr.insee.queen.api.controller.integration.component;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IntegrationResultLabel {
    public static final String NOMENCLATURE_FILE_NOT_FOUND = "Nomenclature file '%s' could not be found in input zip";
    public static final String QUESTIONNAIRE_FILE_NOT_FOUND = "Questionnaire model file '%s' could not be found in input zip";
    public static final String JSON_PARSING_ERROR = "Could not parse json in file '%s'";
    public static final String FILE_INVALID = "File %s does not fit the required template: %s";
    public static final String FILE_NOT_FOUND = "No file %s found";
    public static final String CAMPAIGN_IDS_MISMATCH = "Questionnaire model has campaign id %s while campaign in zip has id %s";
    public static final String NOMENCLATURE_ALREADY_EXISTS = "A nomenclature with id %s already exists";
    public static final String NOMENCLATURE_DO_NOT_EXIST = "The nomenclature '%s' does not exist";
    public static final String CAMPAIGN_DO_NOT_EXIST = "The campaign '%s' does not exist";
    public static final String CAMPAIGN_ID_INCORRECT = "The campaign id is not defined";
}
