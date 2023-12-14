package fr.insee.queen.api.integration.controller.component.builder;

import java.util.List;

public record QuestionnaireModelItem(String id, String label, String fileName, List<String> requiredNomenclatures) {
}
