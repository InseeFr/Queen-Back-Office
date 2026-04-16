package fr.insee.queen.domain.registre.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.registre.gateway.RegistreRepository;
import fr.insee.queen.domain.registre.model.CodeList;
import fr.insee.queen.domain.registre.model.CollectionInstrument;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for accessing registre data and business operations.
 * Implements the RegistreService interface and uses RegistreRepository for data access.
 */
@Service
@AllArgsConstructor
@Slf4j
public class RegistreApiService implements RegistreService {

    private final RegistreRepository registreRepository;

    @Override
    public CollectionInstrument findCollectionInstrumentByUrl(String url) {
        log.debug("Finding collection instrument by URL: {}", url);
        return registreRepository.findCollectionInstrumentByUrl(url);
    }

    @Override
    public Map<CodeList, ArrayNode> findCodeModalitiesByUrl(String url) {
        log.debug("Finding codes modalities by URL: {}", url);

        List<CodeList> codeLists = registreRepository.findCodesListByUrl(url);
        Map<CodeList, ArrayNode> modalitiesByCodeList = new HashMap<>();

        for (CodeList codeList : codeLists) {
            ArrayNode modalities = registreRepository.findModalitiesByCodeUrl(codeList.url());
            modalitiesByCodeList.put(codeList, modalities);
        }

        return modalitiesByCodeList;
    }
}