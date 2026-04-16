package fr.insee.queen.application.integration.service.dummy;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.registre.model.CodeList;
import fr.insee.queen.domain.registre.model.CollectionInstrument;
import fr.insee.queen.domain.registre.service.RegistreService;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class RegistreFakeService implements RegistreService {

    @Setter
    private CollectionInstrument collectionInstrument;

    @Setter
    private Map<CodeList, ArrayNode> modalities = new HashMap<>();

    @Override
    public CollectionInstrument findCollectionInstrumentByUrl(String url) {
        return collectionInstrument;
    }

    @Override
    public Map<CodeList, ArrayNode> findCodeModalitiesByUrl(String codesListsUrl) {
        return modalities;
    }
}
