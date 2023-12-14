package fr.insee.queen.api.integration.controller.component.builder;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuestionnaireModelItem(String id,
                                     String label,
                                     String filename,
                                     @JsonProperty("required-nomenclatures")
                                     List<String> requiredNomenclatures) {
}
