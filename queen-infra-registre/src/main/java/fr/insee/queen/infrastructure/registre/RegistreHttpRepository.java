package fr.insee.queen.infrastructure.registre;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.queen.domain.registre.gateway.RegistreRepository;
import fr.insee.queen.domain.registre.model.CollectionInstrument;
import fr.insee.queen.domain.registre.model.CodeList;
import fr.insee.queen.domain.registre.service.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RegistreHttpRepository implements RegistreRepository {

    private final RestClient restClient;

    @Override
    public CollectionInstrument findCollectionInstrumentByUrl(String url) {
        log.info("Finding collection instrument with URL: {}", url);
        return restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) ->
                                handleErrorStatus(response.getStatusCode(), url, RegistreResourceType.COLLECTION_INSTRUMENT))
                .body(CollectionInstrument.class);
    }

    @Override
    public List<CodeList> findCodesListByUrl(String url) {
        log.info("Finding code lists with URL: {}", url);
        return restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                    (request, response) ->
                            handleErrorStatus(response.getStatusCode(), url, RegistreResourceType.CODE_LIST))
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public ArrayNode findModalitiesByCodeUrl(String url) {
        log.info("Finding modalities for code with URL: {}", url);
        return restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                    (request, response) ->
                            handleErrorStatus(response.getStatusCode(), url, RegistreResourceType.CODE_MODALITIES))
                .body(new ParameterizedTypeReference<>() {});
    }

    private void handleErrorStatus(HttpStatusCode status, String url, RegistreResourceType registreResourceType) {
        if (status == HttpStatus.NOT_FOUND) {
            switch (registreResourceType) {
                case COLLECTION_INSTRUMENT -> throw new CollectionInstrumentNotFoundException("Collection instrument not found: " + url);
                case CODE_LIST  -> throw new CodeListNotFoundException("Code list not found: " + url);
                case CODE_MODALITIES -> throw new CodeModalitiesNotFoundException("Code modalities not found: " + url);
            }
        } else if (status == HttpStatus.UNAUTHORIZED) {
            throw new RegistreAuthException("Unauthorized access to registre API: " + url);
        } else if (status == HttpStatus.FORBIDDEN) {
            throw new RegistreAuthException("Forbidden access to registre API: " + url);
        }
        throw new RegistreException("Error accessing registre API: " + url + " - Status: " + status);
    }
}
