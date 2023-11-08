package fr.insee.queen.api.service.pilotage;

import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.campaign.CampaignSummaryDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto;
import fr.insee.queen.api.service.campaign.CampaignExistenceService;
import fr.insee.queen.api.service.exception.PilotageApiException;
import fr.insee.queen.api.service.surveyunit.SurveyUnitService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PilotageApiService implements PilotageService {
    private final SurveyUnitService surveyUnitService;
    private final CampaignExistenceService campaignExistenceService;
    private final PilotageRepository pilotageRepository;
    public static final String CAMPAIGN = "campaign";

    public boolean isClosed(String campaignId, String authToken) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        return pilotageRepository.isClosed(campaignId, authToken);
    }

    public List<SurveyUnitSummaryDto> getSurveyUnitsByCampaign(String campaignId, String authToken) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        Map<String, SurveyUnitSummaryDto> surveyUnitMap = new HashMap<>();

        List<LinkedHashMap<String,String>> objects = pilotageRepository.getCurrentSurveyUnit(authToken, campaignId);

        if(objects == null || objects.isEmpty()) {
            log.info("GET survey-units for campaign with id {} resulting in 404", campaignId);
            return Collections.emptyList();
        }

        log.info("Number of SU read in Pearl Jam API : {}", objects.size());
        log.info("Detail : {}", displayDetail(objects));
        for(LinkedHashMap<String, String> map : objects) {
            if(map.get("campaign").equals(campaignId)) {
                log.info("ID : {}", map.get("id"));
                surveyUnitService.findSummaryById(map.get("id")).ifPresent(surveyUnitDto ->
                    surveyUnitMap.putIfAbsent(surveyUnitDto.id(), surveyUnitDto)
                );
            }
        }
        log.info("Number of SU to return : {}", surveyUnitMap.size());
        log.info("GET survey-units for campaign with id {} resulting in 200", campaignId);
        return surveyUnitMap.values().stream().toList();
    }

    private String displayDetail(List<LinkedHashMap<String, String>> objects) {
        Map<String,Integer> nbSUbyCampaign = new HashMap<>();
        for(LinkedHashMap<String, String> map : objects) {
            nbSUbyCampaign.putIfAbsent(map.get(CAMPAIGN), 0);
            nbSUbyCampaign.put(map.get(CAMPAIGN),  nbSUbyCampaign.get(map.get(CAMPAIGN))+1);
        }
        return "["+nbSUbyCampaign.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " Survey unit")
                .collect(Collectors.joining("; "))+"]";

    }

    public List<CampaignSummaryDto> getInterviewerCampaigns(String authToken) {
        List<CampaignSummaryDto> campaigns = pilotageRepository.getInterviewerCampaigns(authToken);
        if(campaigns == null) {
            log.error("Pilotage API does not have a body (was expecting a campaign list)");
            throw new PilotageApiException();
        }
        log.info("{} campaigns returned", campaigns.size());
        return campaigns;
    }

    @Cacheable(value = CacheName.HABILITATION, key = "{#surveyUnit.id, #surveyUnit.campaignId, #role, #idep}")
    public boolean hasHabilitation(SurveyUnitHabilitationDto surveyUnit, PilotageRole role, String idep, String authToken) {
        return pilotageRepository.hasHabilitation(surveyUnit, role, idep, authToken);
    }
}
