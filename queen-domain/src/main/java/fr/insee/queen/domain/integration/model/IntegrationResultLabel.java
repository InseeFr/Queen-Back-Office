package fr.insee.queen.domain.integration.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IntegrationResultLabel {
    public static final String NOMENCLATURE_FILE_NOT_FOUND = "Nomenclature file '%s' could not be found in input zip";
    public static final String QUESTIONNAIRE_FILE_NOT_FOUND = "Questionnaire model file '%s' could not be found in input zip";
    public static final String JSON_PARSING_ERROR = "Could not parse json in file '%s'";
    public static final String ZIP_PARSING_ERROR = "Could not parse zip file '%s'";
    public static final String FILE_INVALID = "File %s does not fit the required template: %s";
    public static final String FILE_NOT_FOUND = "No file %s found";
    public static final String NOMENCLATURE_ALREADY_EXISTS = "A nomenclature with id %s already exists";
    public static final String NOMENCLATURE_DO_NOT_EXIST = "The nomenclature '%s' does not exist";
    public static final String GROUPS_SKIPPED_QUESTIONNAIRE_ERRORS = "%s not integrated because one or more questionnaire models failed to integrate";
}
