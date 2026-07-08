package fr.insee.queen.domain.campaign.model;


import tools.jackson.databind.node.ArrayNode;

public record Nomenclature(
        String id,
        String label,
        ArrayNode value) {
}
