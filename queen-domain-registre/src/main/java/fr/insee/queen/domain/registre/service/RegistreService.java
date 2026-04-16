package fr.insee.queen.domain.registre.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.registre.model.CodeList;
import fr.insee.queen.domain.registre.model.CollectionInstrument;

import java.util.Map;

/**
 * Service interface for accessing registre data and business operations.
 * Provides higher-level business methods that use the RegistreRepository.
 */
public interface RegistreService {

    /**
     * Retrieves a collection instrument by its URL.
     *
     * @param url the URL of the instrument
     * @return the CollectionInstrument containing instrument data
     */
    CollectionInstrument findCollectionInstrumentByUrl(String url);

    /**
     * Retrieves all code lists associated with a codes lists URL and their modalities.
     * Returns a map of CodeList to their corresponding modalities.
     * If no modalities are found for a code list, the value is null in the map.
     *
     * @param url the URL to get code lists and their modalities
     * @return map of CodeList to their modalities (value can be null if not found)
     */
    Map<CodeList, ArrayNode> findCodeModalitiesByUrl(String url);
}