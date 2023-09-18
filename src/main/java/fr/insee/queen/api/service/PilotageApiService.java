package fr.insee.queen.api.service;

import fr.insee.queen.api.constants.Constants;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto;
import fr.insee.queen.api.exception.EntityNotFoundException;
import fr.insee.queen.api.exception.PilotageApiException;
import fr.insee.queen.api.repository.CampaignRepository;
import fr.insee.queen.api.repository.SurveyUnitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PilotageApiService {

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
    private SurveyUnitRepository surveyUnitRepository;

    private CampaignRepository campaignRepository;

    private final RestTemplate restTemplate;

    public boolean isClosed(String campaignId, String authToken) {
        if(!campaignRepository.existsById(campaignId)) {
            throw new EntityNotFoundException(String.format("Campaign %s was not found", campaignId));
        }
        final String uriPilotageFilter = pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + "/campaigns/" + campaignId + "/ongoing";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);

        try {
            ResponseEntity<LinkedHashMap<String, Boolean>> response = restTemplate.exchange(uriPilotageFilter, HttpMethod.GET,
                    new HttpEntity<>(headers), new ParameterizedTypeReference<LinkedHashMap<String, Boolean>>(){});
            LinkedHashMap<String, Boolean> responseBody = response.getBody();
            if(responseBody == null) {
                log.error("Pilotage API does not have a body (was expecting a boolean value as response body");
                throw new PilotageApiException();
            }
            return !responseBody.get("ongoing");

        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw new PilotageApiException();
        }
    }

    public List<SurveyUnitSummaryDto> getSurveyUnitsByCampaign(String campaignId, String authToken) {
        Map<String, SurveyUnitSummaryDto> surveyUnitMap = new HashMap<>();

        ResponseEntity<Object> result = getSuFromPilotage(authToken);
        log.info("GET survey-units from PearJam API resulting in {}", result.getStatusCode());
        if(result.getStatusCode()!=HttpStatus.OK) {
            log.error("""
					GET survey-units for campaign with id {} resulting in 500
					caused by one of following:
					- No survey unit found in pearl jam DB
					- User not authorized
					""", campaignId);
            throw new PilotageApiException(String.format("No survey unit found for the campaign id %s or user not authorized", campaignId));
        }

        List<LinkedHashMap<String,String>> objects = (List<LinkedHashMap<String, String>>) result.getBody();
        if(objects == null || objects.isEmpty()) {
            log.info("GET survey-units for campaign with id {} resulting in 404", campaignId);
            return Collections.emptyList();
        }
        log.info("Number of SU read in Pearl Jam API : {}", objects.size());
        log.info("Detail : {}", displayDetail(objects));
        for(LinkedHashMap<String, String> map : objects) {
            if(map.get("campaign").equals(campaignId)) {
                log.info("ID : {}", map.get("id"));
                Optional<SurveyUnitSummaryDto> su = surveyUnitRepository.findSummaryById(map.get("id"));
                if(su.isPresent() && surveyUnitMap.get(su.get().id())==null) {
                    SurveyUnitSummaryDto surveyUnitDto = su.get();
                    log.info("ID is present");
                    surveyUnitMap.put(surveyUnitDto.id(), surveyUnitDto);
                }
            }
        }
        log.info("Number of SU to return : {}", surveyUnitMap.size());
        log.info("GET survey-units for campaign with id {} resulting in 200", campaignId);
        return surveyUnitMap.values().stream().toList();
    }

    private String displayDetail(List<LinkedHashMap<String, String>> objects) {
        Map<String,Integer> nbSUbyCampaign = new HashMap<>();
        for(LinkedHashMap<String, String> map : objects) {
            nbSUbyCampaign.putIfAbsent(map.get(Constants.CAMPAIGN), 0);
            nbSUbyCampaign.put(map.get(Constants.CAMPAIGN),  nbSUbyCampaign.get(map.get(Constants.CAMPAIGN))+1);
        }
        return "["+nbSUbyCampaign.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " Survey unit")
                .collect(Collectors.joining("; "))+"]";

    }

    /**
     * This method retrieve the data from the Pilotage API for the current user
     * @param authToken authorization token header
     * @return String of UserId
     */
    public ResponseEntity<Object> getSuFromPilotage(String authToken){
        final String uriPilotageFilter = pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + Constants.API_PEARLJAM_SURVEYUNITS;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return restTemplate.exchange(uriPilotageFilter, HttpMethod.GET, new HttpEntity<>(headers), Object.class);
    }

    public List<CampaignSummaryDto> getInterviewerCampaigns(String authToken) {
        // call pilotage API
        final String uriPilotageInterviewerCampaigns = pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + Constants.API_PEARLJAM_INTERVIEWER_CAMPAIGNS;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);

        ResponseEntity<List<CampaignSummaryDto>> response = restTemplate.exchange(uriPilotageInterviewerCampaigns,
                HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<CampaignSummaryDto>>() {
                });
        log.info("Pilotage API call returned {}", response.getStatusCode().value());
        if (response.getStatusCode().is2xxSuccessful()) {
            List<CampaignSummaryDto> campaigns = response.getBody();
            if(campaigns == null) {
                log.error("Pilotage API does not have a body (was expecting a campaign list)");
                throw new PilotageApiException();
            }
            log.info("{} campaigns returned", campaigns.size());
            return campaigns;
        } else {
            return Collections.emptyList();
        }
    }

    @Cacheable("habilitations")
    public boolean hasHabilitation(SurveyUnitHabilitationDto surveyUnit, String role, String idep, String authToken) {
        String expectedRole;
        switch (role) {
            case Constants.INTERVIEWER -> expectedRole = "";
            case Constants.REVIEWER -> expectedRole = Constants.REVIEWER;
            default -> {
                return false;
            }
        }

        String uriPilotageFilter="";
        String campaignId = surveyUnit.campaignId();

        if(Pattern.matches(campaignIdRegexWithAlternativeHabilitationService, campaignId)) {
            log.info("Current campaignId {} requires an alternative habilitation service {} ",campaignId, alternativeHabilitationServiceURL);
            uriPilotageFilter+=alternativeHabilitationServiceURL;
        }else {
            uriPilotageFilter+=pilotageScheme + "://" + pilotageHost + ":" + pilotagePort + Constants.API_HABILITATION;
        }

        uriPilotageFilter += "?id=" + surveyUnit.id()
                + "&role=" + expectedRole + "&campaign=" + campaignId + "&idep=" + idep;


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        log.error(authToken);
        try {
            log.error(uriPilotageFilter);
            ResponseEntity<LinkedHashMap<String, Boolean>> response = restTemplate.exchange(uriPilotageFilter, HttpMethod.GET,
                    new HttpEntity<>(headers), new ParameterizedTypeReference<LinkedHashMap<String, Boolean>>() {});

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.info(
                        "Habilitation of user {} with role {} to access survey-unit {} denied : habilitation service returned {} ",
                        idep, role, surveyUnit.id(), response.getStatusCode());
                return false;
            }

            LinkedHashMap<String, Boolean> responseBody = response.getBody();

            if(responseBody == null) {
                log.error("Pilotage API does not have a body (was expecting a boolean value)");
                throw new PilotageApiException();
            }

            boolean habilitationResult = responseBody.get("habilitated");
            log.info("Habilitation of user {} with role {} to access survey-unit {} : {}", idep, role,
                    surveyUnit.id(), habilitationResult ? "granted" : "denied");
            return habilitationResult;

        } catch(HttpStatusCodeException e){
            if(e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                log.info("Habilitation of user {} with role {} to access survey-unit {} denied.",
                        idep, role, surveyUnit.id());
                return false;
            }
            log.error(e.getMessage(), e);
            throw new PilotageApiException();
        } catch(RestClientException e) {
            log.error(e.getMessage(), e);
            throw new PilotageApiException();
        }
    }
}
