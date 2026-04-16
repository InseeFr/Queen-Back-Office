package fr.insee.queen.domain.registre.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents a collection instrument from the registre.
 * Contains the instrument data
 *
 * @param id the unique identifier of the instrument
 * @param content the JSON content of the instrument
 */
public record CollectionInstrument(
        String id,
        ObjectNode content
) {}