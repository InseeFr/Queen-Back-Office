package fr.insee.queen.infrastructure.pilotage;

import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.synchronisation.gateway.SynchronisationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SynchronisationHttpRepository implements SynchronisationRepository {

    private static final String API_INTERROGATIONS = "/api/interrogations/%s";

    @Value("${feature.synchronisation.queen-url}")
    private final String queenUrl;

    private final RestTemplate restTemplate;

    @Override
    public Interrogation synchronise(String interrogationId) {
        String url = queenUrl + API_INTERROGATIONS.formatted(interrogationId);
        log.info("Synchronizing interrogation {} from {}", interrogationId, url);

        ResponseEntity<Interrogation> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Interrogation.class
        );

        log.debug("Synchronization response status: {}", response.getStatusCode());
        return response.getBody();
    }
}
