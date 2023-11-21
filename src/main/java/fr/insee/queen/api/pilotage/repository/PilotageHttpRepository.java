package fr.insee.queen.api.pilotage.repository;

import fr.insee.queen.api.pilotage.service.PilotageRole;
import fr.insee.queen.api.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.api.pilotage.service.gateway.PilotageRepository;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.pilotage.service.model.PilotageSurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PilotageHttpRepository implements PilotageRepository {
    public static final String API_HABILITATION = "/api/check-habilitation";
    public static final String API_PEARLJAM_SURVEYUNITS = "/api/survey-units";
    public static final String API_PEARLJAM_CAMPAIGNS = "/campaigns/%s/ongoing";
    public static final String API_PEARLJAM_INTERVIEWER_CAMPAIGNS = "/api/interviewer/campaigns";

    @Value("${application.pilotage.url}")
    private final String pilotageUrl;
    @Value("${application.pilotage.alternative-habilitation.url}")
    private final String alternativeHabilitationServiceURL;
    @Value("${application.pilotage.alternative-habilitation.campaignids-regex}")
    private final String campaignIdRegexWithAlternativeHabilitationService;
    private final RestTemplate restTemplate;

    @Override
    public boolean isClosed(String campaignId, String authToken) {
        final String uriPilotageFilter = pilotageUrl + API_PEARLJAM_CAMPAIGNS.formatted(campaignId);

        try {
            ResponseEntity<LinkedHashMap<String, Boolean>> response =
                    restTemplate.exchange(uriPilotageFilter, HttpMethod.GET, getHttpHeaders(authToken),
                            new ParameterizedTypeReference<LinkedHashMap<String, Boolean>>() {});
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

    @Override
    public List<PilotageSurveyUnit> getSurveyUnits(String authToken) {
        try {
            final String uriPilotageFilter = pilotageUrl + API_PEARLJAM_SURVEYUNITS;
            ResponseEntity<List<PilotageSurveyUnit>> response =
                    restTemplate.exchange(uriPilotageFilter, HttpMethod.GET, getHttpHeaders(authToken),
                            new ParameterizedTypeReference<List<PilotageSurveyUnit>>() {});
            log.info("GET survey-units from PearlJam API resulting in {}", response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            if(HttpStatus.NOT_FOUND.equals(ex.getStatusCode())) {
                log.info("Got a 404 status code, 0 survey units returned");
                return new ArrayList<>();
            }
            log.error(ex.getMessage(), ex);
            throw new PilotageApiException();
        }
    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns(String authToken) {
        try {
            final String uriPilotageInterviewerCampaigns = pilotageUrl + API_PEARLJAM_INTERVIEWER_CAMPAIGNS;

            ResponseEntity<List<PilotageCampaign>> response =
                    restTemplate.exchange(uriPilotageInterviewerCampaigns, HttpMethod.GET, getHttpHeaders(authToken),
                            new ParameterizedTypeReference<List<PilotageCampaign>>() {});
            log.info("Pilotage API call returned {}", response.getStatusCode().value());
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            if(HttpStatus.NOT_FOUND.equals(ex.getStatusCode())) {
                log.info("Got a 404 status code, 0 campaigns returned");
                return new ArrayList<>();
            }
            log.error(ex.getMessage(), ex);
            throw new PilotageApiException();
        }
    }

    @Override
    public boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken) {
        StringBuilder uriPilotageFilter = new StringBuilder();
        String campaignId = surveyUnit.campaignId();

        if (Pattern.matches(campaignIdRegexWithAlternativeHabilitationService, campaignId)) {
            log.info("Current campaignId {} requires an alternative habilitation service {} ", campaignId, alternativeHabilitationServiceURL);
            uriPilotageFilter.append(alternativeHabilitationServiceURL);
        } else {
            uriPilotageFilter
                    .append(pilotageUrl)
                    .append(API_HABILITATION);
        }

        uriPilotageFilter.append(String.format("?id=%s&role=%s&campaign=%s&idep=%s", surveyUnit.id(), role.getExpectedRole(), campaignId, idep));

        try {
            ResponseEntity<LinkedHashMap<String, Boolean>> response =
                    restTemplate.exchange(uriPilotageFilter.toString(), HttpMethod.GET, getHttpHeaders(authToken),
                            new ParameterizedTypeReference<LinkedHashMap<String, Boolean>>(){});

            LinkedHashMap<String, Boolean> responseBody = response.getBody();

            if (responseBody == null) {
                log.error("Pilotage API does not have a body (was expecting a boolean value)");
                throw new PilotageApiException();
            }

            boolean habilitationResult = responseBody.get("habilitated");
            log.info("Habilitation of user {} with role {} to access survey-unit {} : {}", idep, role.name(),
                    surveyUnit.id(), habilitationResult ? "granted" : "denied");
            return habilitationResult;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            HttpStatusCode status = ex.getStatusCode();
            if (status.equals(HttpStatus.UNAUTHORIZED)) {
                log.info("Habilitation of user {} with role {} to access survey-unit {} denied.",
                        idep, role.name(), surveyUnit.id());
                return false;
            }
            log.error(ex.getMessage(), ex);
            throw new PilotageApiException();
        }
    }

    private HttpEntity<String> getHttpHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
