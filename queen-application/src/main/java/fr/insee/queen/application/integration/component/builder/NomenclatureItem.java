package fr.insee.queen.application.integration.component.builder;

import tools.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.group.model.Nomenclature;
public record NomenclatureItem(String id, String label, String filename) {
    public static Nomenclature toModel(NomenclatureItem nomenclatureItem, ArrayNode nomenclatureValue) {
        return new Nomenclature(nomenclatureItem.id, nomenclatureItem.label(), nomenclatureValue);
    }
}
