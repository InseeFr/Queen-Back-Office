package fr.insee.queen.domain.group.model;


import tools.jackson.databind.node.ArrayNode;

public record Nomenclature(
        String id,
        String label,
        ArrayNode value) {
}
