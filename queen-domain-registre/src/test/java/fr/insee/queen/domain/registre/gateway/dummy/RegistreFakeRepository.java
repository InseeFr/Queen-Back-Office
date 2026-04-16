package fr.insee.queen.domain.registre.gateway.dummy;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.registre.gateway.RegistreRepository;
import fr.insee.queen.domain.registre.model.CodeList;
import fr.insee.queen.domain.registre.model.CollectionInstrument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fake implementation of RegistreRepository for testing purposes.
 * Allows setting up test data and retrieving it.
 */
public class RegistreFakeRepository implements RegistreRepository {

    private final Map<String, CollectionInstrument> collectionInstrumentsByUrl = new HashMap<>();
    private final Map<String, List<CodeList>> codeListsByUrl = new HashMap<>();
    private final Map<String, ArrayNode> modalitiesByCodeListUrl = new HashMap<>();

    @Override
    public CollectionInstrument findCollectionInstrumentByUrl(String instrumentUrl) {
        return collectionInstrumentsByUrl.get(instrumentUrl);
    }

    @Override
    public List<CodeList> findCodesListByUrl(String codesListsUrl) {
        return codeListsByUrl.getOrDefault(codesListsUrl, new ArrayList<>());
    }

    @Override
    public ArrayNode findModalitiesByCodeUrl(String codeListUrl) {
        return modalitiesByCodeListUrl.get(codeListUrl);
    }

    /**
     * Sets a collection instrument for a specific URL.
     *
     * @param url the URL to associate with the collection instrument
     * @param collectionInstrument the collection instrument to store
     */
    public void setCollectionInstrumentForUrl(String url, CollectionInstrument collectionInstrument) {
        this.collectionInstrumentsByUrl.put(url, collectionInstrument);
    }

    /**
     * Sets code lists for a specific codes lists URL.
     *
     * @param codesListsUrl the URL to associate with the code lists
     * @param codeLists the list of code lists to store
     */
    public void setCodeListsForUrl(String codesListsUrl, List<CodeList> codeLists) {
        this.codeListsByUrl.put(codesListsUrl, codeLists);
    }

    /**
     * Sets modalities for a specific code list URL.
     *
     * @param codeListUrl the URL to associate with the modalities
     * @param modalities the modalities to store
     */
    public void setModalitiesForCodeListUrl(String codeListUrl, ArrayNode modalities) {
        this.modalitiesByCodeListUrl.put(codeListUrl, modalities);
    }
}