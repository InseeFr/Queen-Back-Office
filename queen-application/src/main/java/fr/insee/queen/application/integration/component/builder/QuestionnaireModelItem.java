package fr.insee.queen.application.integration.component.builder;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record QuestionnaireModelItem(String id,
                                     String label,
                                     String filename,
                                     @JsonProperty("required-nomenclatures")
                                     Set<String> requiredNomenclatures) {
}
