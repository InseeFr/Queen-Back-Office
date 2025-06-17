package fr.insee.queen.infrastructure.pilotage;

import fr.insee.queen.domain.pilotage.gateway.PilotageRepository;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.model.PilotageCampaignEnabled;
import fr.insee.queen.domain.pilotage.model.PilotageHabilitation;
import fr.insee.queen.domain.pilotage.model.PilotageInterrogation;
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
        StringBuilder uriPilotageFilter = new StringBuilder();
        String campaignId = interrogation.campaign().getId();
        String params;
        if (Pattern.matches(campaignIdRegexWithAlternativeHabilitationService, campaignId)) {
            log.debug("Current campaignId {} requires an alternative habilitation service {} ", campaignId, alternativeHabilitationServiceURL);
            uriPilotageFilter.append(alternativeHabilitationServiceURL);
            params = String.format("?id=%s&role=%s&campaign=%s&idep=%s", interrogation.surveyUnitId(), role.getExpectedRole(), campaignId, idep);
        } else {
            uriPilotageFilter
                    .append(pilotageUrl)
                    .append(API_HABILITATION);
            params = String.format("?id=%s&role=%s&idep=%s", interrogation.id(), role.getExpectedRole(), idep);
        }

        uriPilotageFilter.append(params);

        try {
            ResponseEntity<PilotageHabilitation> response =
                    restTemplate.exchange(uriPilotageFilter.toString(), HttpMethod.GET,
                            HttpEntity.EMPTY,
                            PilotageHabilitation.class);

            PilotageHabilitation habilitation = response.getBody();

            if (habilitation == null) {
                log.error("Pilotage API does not have a body (was expecting a boolean value)");
                throw new PilotageApiException();
            }


            log.debug("Habilitation of user {} with role {} to access interrogation {} : {}", idep, role.name(),
                    interrogation.id(), habilitation.habilitated() ? "granted" : "denied");
            return habilitation.habilitated();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            HttpStatusCode status = ex.getStatusCode();
            if (status.equals(HttpStatus.UNAUTHORIZED)) {
                log.debug("Habilitation of user {} with role {} to access interrogation {} denied.",
                        idep, role.name(), interrogation.id());
                return false;
            }
            throw generateException(ex);
        } catch(RestClientException ex) {
            throw generateException(ex);
        }
    }

    private PilotageApiException generateException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new PilotageApiException();
    }
}
