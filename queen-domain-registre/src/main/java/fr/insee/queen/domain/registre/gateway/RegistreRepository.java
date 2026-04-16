package fr.insee.queen.domain.registre.gateway;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.registre.model.CodeList;
import fr.insee.queen.domain.registre.model.CollectionInstrument;
import java.util.List;

/**
 * Repository interface for accessing registre data.
 * Defines the contract for data access operations.
 */
public interface RegistreRepository {

    /**
     * Retrieves a collection instrument by its URL.
     *
     * @param instrumentUrl the URL of the instrument
     * @return the CollectionInstrument containing instrument data
     */
    CollectionInstrument findCollectionInstrumentByUrl(String instrumentUrl);

    /**
     * Retrieves all code lists associated with a collection instrument by its codes lists URL.
     *
     * @param codesListsUrl the URL to get code lists for the instrument
     * @return list of CodeList objects associated with the instrument
     */
    List<CodeList> findCodesListByUrl(String codesListsUrl);

    /**
     * Retrieves all modalities for a specific code list by its URL.
     *
     * @param codeListUrl the URL of the code list
     * @return modalities
     */
    ArrayNode findModalitiesByCodeUrl(String codeListUrl);
}