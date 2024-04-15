package fr.insee.queen.domain.campaign.model;


import com.fasterxml.jackson.databind.node.ArrayNode;

public record Nomenclature(
        String id,
        String label,
        ArrayNode value) {
}
