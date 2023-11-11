package fr.insee.queen.api.pilotage.repository;

import fr.insee.queen.api.pilotage.exception.PilotageApiException;
import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.gateway.PilotageRepository;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PilotageHttpRepository implements PilotageRepository {
    public static final String API_HABILITATION = "/api/check-habilitation";
    public static final String API_PEARLJAM_SURVEYUNITS = "/api/survey-units";
    public static final String API_PEARLJAM_INTERVIEWER_CAMPAIGNS = "/api/interviewer/campaigns";

    @Value("${application.pilotage.service.url.scheme}")
    private final String pilotageScheme;
    @Value("${application.pilotage.service.url.host}")
    private final String pilotageHost;
    @Value("${application.pilotage.service.url.port}")
    private final String pilotagePort;
    @Value("${application.pilotage.alternative-habilitation-service.url}")
    private final String alternativeHabilitationServiceURL;
    @Value("${application.pilotage.alternative-habilitation-service.campaignids-regex}")
    private final String campaignIdRegexWithAlternativeHabilitationService;
    private final RestTemplate restTemplate;

    @Override
    public boolean isClosed(String campaignId, String authToken) {
        final String uriPilotageFilter = pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + "/campaigns/" + campaignId + "/ongoing";

        try {
            ResponseEntity<LinkedHashMap<String, Boolean>> response =
                    exchange(uriPilotageFilter, authToken,
                            new ParameterizedTypeReference<LinkedHashMap<String, Boolean>>() {
                            });
            LinkedHashMap<String, Boolean> responseBody = response.getBody();
            if (responseBody == null) {
                log.error("Pilotage API does not have a body (was expecting a boolean value as response body");
                throw new PilotageApiException();
            }
            return !responseBody.get("ongoing");
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw new PilotageApiException();
        }
    }

    /**
     * This method retrieve the data from the Pilotage API for the current user
     *
     * @param authToken authorization token header
     * @return String of UserId
     */
    @Override
    public List<LinkedHashMap<String, String>> getCurrentSurveyUnit(String authToken, String campaignId) {
        final String uriPilotageFilter = pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + API_PEARLJAM_SURVEYUNITS;
        ResponseEntity<List<LinkedHashMap<String, String>>> response =
                exchange(uriPilotageFilter, authToken, new ParameterizedTypeReference<List<LinkedHashMap<String, String>>>() {
                });
        log.info("GET survey-units from PearlJam API resulting in {}", response.getStatusCode());
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("""
                    GET survey-units for campaign with id {} resulting in 500
                    caused by one of following:
                    - No survey unit found in pearl jam DB
                    - User not authorized
                    """, campaignId);
            throw new PilotageApiException(String.format("No survey unit found for the campaign id %s or user not authorized", campaignId));
        }
        return response.getBody();
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns(String authToken) {
        // call pilotage API
        final String uriPilotageInterviewerCampaigns = pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + API_PEARLJAM_INTERVIEWER_CAMPAIGNS;

        ResponseEntity<List<PilotageCampaign>> response =
                exchange(uriPilotageInterviewerCampaigns, authToken, new ParameterizedTypeReference<List<PilotageCampaign>>() {
                });
        log.info("Pilotage API call returned {}", response.getStatusCode().value());
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.info("Got a {} status code, 0 campaigns returned", response.getStatusCode().value());
            return Collections.emptyList();
        }
        return response.getBody();
    }

    @Override
    public boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken) {
        String uriPilotageFilter = "";
        String campaignId = surveyUnit.campaignId();

        if (Pattern.matches(campaignIdRegexWithAlternativeHabilitationService, campaignId)) {
            log.info("Current campaignId {} requires an alternative habilitation service {} ", campaignId, alternativeHabilitationServiceURL);
            uriPilotageFilter += alternativeHabilitationServiceURL;
        } else {
            uriPilotageFilter += pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + API_HABILITATION;
        }

        uriPilotageFilter += "?id=" + surveyUnit.id()
                + "&role=" + role.getExpectedRole() + "&campaign=" + campaignId + "&idep=" + idep;

        try {
            ResponseEntity<LinkedHashMap<String, Boolean>> response =
                    exchange(uriPilotageFilter, authToken, new ParameterizedTypeReference<LinkedHashMap<String, Boolean>>() {
                    });

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.info(
                        "Habilitation of user {} with role {} to access survey-unit {} denied : habilitation service returned {} ",
                        idep, role.name(), surveyUnit.id(), response.getStatusCode());
                return false;
            }

            LinkedHashMap<String, Boolean> responseBody = response.getBody();

            if (responseBody == null) {
                log.error("Pilotage API does not have a body (was expecting a boolean value)");
                throw new PilotageApiException();
            }

            boolean habilitationResult = responseBody.get("habilitated");
            log.info("Habilitation of user {} with role {} to access survey-unit {} : {}", idep, role.name(),
                    surveyUnit.id(), habilitationResult ? "granted" : "denied");
            return habilitationResult;

        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                log.info("Habilitation of user {} with role {} to access survey-unit {} denied.",
                        idep, role.name(), surveyUnit.id());
                return false;
            }
            log.error(e.getMessage(), e);
            throw new PilotageApiException();
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw new PilotageApiException();
        }
    }

    private HttpEntity getHttpHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    private <T> ResponseEntity<T> exchange(String url, String authToken, ParameterizedTypeReference<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.GET, getHttpHeaders(authToken), responseType);
    }
}
