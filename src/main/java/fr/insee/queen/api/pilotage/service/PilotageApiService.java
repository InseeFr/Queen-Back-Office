package fr.insee.queen.api.pilotage.service;

import fr.insee.queen.api.campaign.service.CampaignExistenceService;
import fr.insee.queen.api.campaign.service.QuestionnaireModelService;
import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.api.pilotage.service.gateway.PilotageRepository;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.surveyunit.service.SurveyUnitService;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
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
    private final QuestionnaireModelService questionnaireModelService;
    public static final String CAMPAIGN = "campaign";

    @Override
    public boolean isClosed(String campaignId, String authToken) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        return pilotageRepository.isClosed(campaignId, authToken);
    }

    @Override
    public List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId, String authToken) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        Map<String, SurveyUnitSummary> surveyUnitMap = new HashMap<>();

        List<LinkedHashMap<String, String>> objects = pilotageRepository.getCurrentSurveyUnit(authToken, campaignId);

        if (objects == null || objects.isEmpty()) {
            log.info("GET survey-units for campaign with id {} resulting in 404", campaignId);
            return Collections.emptyList();
        }

        log.info("Number of SU read in Pearl Jam API : {}", objects.size());
        log.info("Detail : {}", displayDetail(objects));
        for (LinkedHashMap<String, String> map : objects) {
            if (map.get(CAMPAIGN).equals(campaignId)) {
                log.info("ID : {}", map.get("id"));
                surveyUnitService.findSummaryById(map.get("id")).ifPresent(surveyUnitSummary ->
                        surveyUnitMap.putIfAbsent(surveyUnitSummary.id(), surveyUnitSummary)
                );
            }
        }
        log.info("Number of SU to return : {}", surveyUnitMap.size());
        log.info("GET survey-units for campaign with id {} resulting in 200", campaignId);
        return surveyUnitMap.values().stream().toList();
    }

    private String displayDetail(List<LinkedHashMap<String, String>> objects) {
        Map<String, Integer> nbSUbyCampaign = new HashMap<>();
        for (LinkedHashMap<String, String> map : objects) {
            nbSUbyCampaign.putIfAbsent(map.get(CAMPAIGN), 0);
            nbSUbyCampaign.put(map.get(CAMPAIGN), nbSUbyCampaign.get(map.get(CAMPAIGN)) + 1);
        }
        return "[" + nbSUbyCampaign.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " Survey unit")
                .collect(Collectors.joining("; ")) + "]";

    }

    @Override
    public List<PilotageCampaign> getInterviewerCampaigns(String authToken) {
        List<PilotageCampaign> campaigns = pilotageRepository.getInterviewerCampaigns(authToken);
        if (campaigns == null) {
            log.error("Pilotage API does not have a body (was expecting a campaign list)");
            throw new PilotageApiException();
        }
        campaigns.forEach(pilotageCampaign ->
                pilotageCampaign.questionnaireIds(questionnaireModelService.getQuestionnaireIds(pilotageCampaign.id())));
        return campaigns;
    }

    @Override
    @Cacheable(value = CacheName.HABILITATION, key = "{#surveyUnit.id, #surveyUnit.campaignId, #role, #idep}")
    public boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken) {
        return pilotageRepository.hasHabilitation(surveyUnit, role, idep, authToken);
    }
}
