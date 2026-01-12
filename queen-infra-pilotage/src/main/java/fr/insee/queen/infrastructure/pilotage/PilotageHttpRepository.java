package fr.insee.queen.infrastructure.pilotage;

import fr.insee.queen.domain.pilotage.gateway.PilotageRepository;
import fr.insee.queen.domain.pilotage.model.*;
import fr.insee.queen.domain.pilotage.service.PilotageRole;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PilotageHttpRepository implements PilotageRepository {
    public static final String API_HABILITATION = "/api/check-habilitation";
    public static final String API_PEARLJAM_SURVEYUNITS = "/api/interrogations";
    public static final String API_PEARLJAM_CAMPAIGNS = "/campaigns/%s/ongoing";
    public static final String API_PEARLJAM_INTERVIEWER_CAMPAIGNS = "/api/interviewer/campaigns";

    @Value("${feature.pilotage.url}")
    private final String pilotageUrl;
    @Value("${feature.pilotage.alternative-habilitation.url}")
    private final String alternativeHabilitationServiceURL;
    @Value("${feature.pilotage.alternative-habilitation.campaignids-regex}")
    private final String campaignIdRegexWithAlternativeHabilitationService;
    private final RestTemplate restTemplate;
    @Value("${feature.collection.mode:WEB}")
    private InputModeEnum inputMode;

    @Override
    public boolean isClosed(String campaignId) {
        final String uriPilotageFilter = pilotageUrl + API_PEARLJAM_CAMPAIGNS.formatted(campaignId);

        try {
            ResponseEntity<PilotageCampaignEnabled> response =
                    restTemplate.exchange(uriPilotageFilter, HttpMethod.GET,
                            HttpEntity.EMPTY,
                            PilotageCampaignEnabled.class);
            PilotageCampaignEnabled campaignEnabled = response.getBody();
            if (campaignEnabled == null) {
                log.error("Pilotage API does not have a body (was expecting a boolean value as response body");
                throw new PilotageApiException();
            }
            return !campaignEnabled.ongoing();
        } catch (RestClientException e) {
            throw generateException(e);
        }
    }

    @Override
    public List<PilotageInterrogation> getInterrogations() {
        try {
            final String uriPilotageFilter = pilotageUrl + API_PEARLJAM_SURVEYUNITS;
            ResponseEntity<List<PilotageInterrogation>> response =
                    restTemplate.exchange(uriPilotageFilter, HttpMethod.GET,
                            HttpEntity.EMPTY,
                            new ParameterizedTypeReference<List<PilotageInterrogation>>() {});
            log.debug("GET interrogations from PearlJam API resulting in {}", response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            if(HttpStatus.NOT_FOUND.equals(ex.getStatusCode())) {
                log.debug("Got a 404 status code, 0 interrogations returned");
                return new ArrayList<>();
            }
            throw generateException(ex);
        } catch(RestClientException ex) {
            throw generateException(ex);
        }
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns() {
        try {
            final String uriPilotageInterviewerCampaigns = pilotageUrl + API_PEARLJAM_INTERVIEWER_CAMPAIGNS;

            ResponseEntity<List<PilotageCampaign>> response =
                    restTemplate.exchange(uriPilotageInterviewerCampaigns, HttpMethod.GET,
                            HttpEntity.EMPTY,
                            new ParameterizedTypeReference<List<PilotageCampaign>>() {});
            log.debug("Pilotage API call returned {}", response.getStatusCode().value());
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            if(HttpStatus.NOT_FOUND.equals(ex.getStatusCode())) {
                log.debug("Got a 404 status code, 0 campaigns returned");
                return new ArrayList<>();
            }
            throw generateException(ex);
        } catch(RestClientException ex) {
            throw generateException(ex);
        }
    }

    @Override
    public boolean hasHabilitation(InterrogationSummary interrogation, PilotageRole role, String idep) {
        if (InputModeEnum.PAPER.equals(inputMode)) {
            return hasPaperHabilitation(interrogation);
        }
        return hasWebHabilitation(interrogation, role, idep);
    }

    private boolean hasPaperHabilitation(InterrogationSummary interrogation) {
        try {
            String url = buildPaperHabilitationUrl(interrogation.id());

            log.debug("Checking PAPER permission for interrogation {} via {}", interrogation.id(), url);

            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url, HttpMethod.GET, HttpEntity.EMPTY, Boolean.class
            );

            boolean authorized = Boolean.TRUE.equals(response.getBody());
            log.debug("Habilitation PAPER for interrogation {} : {}", interrogation.id(),
                    authorized ? "granted" : "denied");

            return authorized;

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return handleUnauthorized(ex, interrogation.id());
        } catch (RestClientException ex) {
            throw generateException(ex);
        }
    }

    private boolean hasWebHabilitation(InterrogationSummary interrogation, PilotageRole role, String idep) {
        try {
            String url = buildWebHabilitationUrl(interrogation, role, idep);

            ResponseEntity<PilotageHabilitation> response =
                    restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, PilotageHabilitation.class);

            PilotageHabilitation habilitation = response.getBody();
            if (habilitation == null) {
                log.error("Pilotage API returned null body");
                throw new PilotageApiException();
            }

            log.debug("Habilitation of user {} with role {} to access interrogation {} : {}",
                    idep, role.name(), interrogation.id(),
                    habilitation.habilitated() ? "granted" : "denied");

            return habilitation.habilitated();

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return handleUnauthorized(ex, interrogation.id(), role, idep);
        } catch (RestClientException ex) {
            throw generateException(ex);
        }
    }


    private String buildPaperHabilitationUrl(String interrogationId) {
        return UriComponentsBuilder.fromUriString(pilotageUrl)
                .path("/api/permissions/check")
                .queryParam("id", interrogationId)
                .queryParam("permission", "INTERROGATION_PAPER_DATA_EDIT")
                .toUriString();
    }

    private String buildWebHabilitationUrl(InterrogationSummary interrogation,
                                           PilotageRole role,
                                           String idep) {
        String campaignId = interrogation.campaign().getId();

        if (Pattern.matches(campaignIdRegexWithAlternativeHabilitationService, campaignId)) {
            log.debug("Campaign {} uses alternative habilitation service {}", campaignId,
                    alternativeHabilitationServiceURL);

            return String.format(
                    "%s?id=%s&role=%s&campaign=%s&idep=%s",
                    alternativeHabilitationServiceURL,
                    interrogation.id(),
                    role.getExpectedRole(),
                    campaignId,
                    idep
            );
        }

        return String.format(
                "%s%s?id=%s&role=%s&idep=%s",
                pilotageUrl,
                API_HABILITATION,
                interrogation.id(),
                role.getExpectedRole(),
                idep
        );
    }

    private boolean handleUnauthorized(HttpStatusCodeException ex, String interrogationId) {
        if (ex.getStatusCode().equals(HttpStatus.UNAUTHORIZED)
                || ex.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            log.debug("Habilitation denied (HTTP {}) for interrogation {}",
                    ex.getStatusCode(), interrogationId);
            return false;
        }
        throw generateException(ex);
    }

    private boolean handleUnauthorized(HttpStatusCodeException ex,
                                       String interrogationId,
                                       PilotageRole role,
                                       String idep) {
        if (ex.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            log.debug("Habilitation of user {} with role {} to access interrogation {} denied.",
                    idep, role.name(), interrogationId);
            return false;
        }
        throw generateException(ex);
    }

    private PilotageApiException generateException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new PilotageApiException();
    }
}
