package fr.insee.queen.api.pilotage.service;

import fr.insee.queen.api.campaign.service.CampaignExistenceService;
import fr.insee.queen.api.campaign.service.QuestionnaireModelService;
import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.api.pilotage.service.gateway.PilotageRepository;
import fr.insee.queen.api.pilotage.service.model.PilotageCampaign;
import fr.insee.queen.api.pilotage.service.model.PilotageSurveyUnit;
import fr.insee.queen.api.surveyunit.service.SurveyUnitService;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
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

    @Override
    public boolean isClosed(String campaignId, String authToken) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        return pilotageRepository.isClosed(campaignId, authToken);
    }

    @Override
    public List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId, String authToken) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        Map<String, SurveyUnitSummary> surveyUnitMap = new HashMap<>();

        List<String> surveyUnitIds = getSurveyUnitIds(campaignId, authToken);

        surveyUnitService.findSummariesByIds(surveyUnitIds)
                .forEach(surveyUnitSummary ->
                        surveyUnitMap.putIfAbsent(surveyUnitSummary.id(), surveyUnitSummary)
                );
        return surveyUnitMap.values().stream().toList();
    }

    @Override
    public List<SurveyUnit> getInterviewerSurveyUnits(String authToken) {
        Map<String, SurveyUnit> surveyUnitMap = new HashMap<>();
        List<String> surveyUnitIds = getSurveyUnitIds(authToken);

        surveyUnitService.findByIds(surveyUnitIds)
                .forEach(surveyUnit ->
                        surveyUnitMap.putIfAbsent(surveyUnit.id(), surveyUnit)
                );
        return surveyUnitMap.values().stream().toList();
    }

    /**
     * Retrieve survey unit ids for the current interviewer for a campaign
     *
     * @param campaignId campaign id
     * @param authToken auth token of current user
     * @return List of survey unit ids
     */
    private List<String> getSurveyUnitIds(String campaignId, String authToken) {
        List<PilotageSurveyUnit> surveyUnits = pilotageRepository.getSurveyUnits(authToken);

        if (surveyUnits == null || surveyUnits.isEmpty()) {
            return Collections.emptyList();
        }

        log.debug("Detail : {}", displayDetail(surveyUnits));
        List<String> surveyUnitIds = surveyUnits.stream()
                .filter(surveyUnit -> campaignId.equals(surveyUnit.campaign()))
                .map(PilotageSurveyUnit::id)
                .toList();

        log.info("Survey units found in pilotage api for campaign {}: {}", campaignId, surveyUnitIds.size());
        return surveyUnitIds;
    }

    /**
     * Retrieve survey unit ids for the current interviewer
     *
     * @param authToken auth token of current user
     * @return List of survey unit ids
     */
    private List<String> getSurveyUnitIds(String authToken) {
        List<PilotageSurveyUnit> surveyUnits = pilotageRepository.getSurveyUnits(authToken);

        if (surveyUnits == null || surveyUnits.isEmpty()) {
            return Collections.emptyList();
        }

        log.debug("Detail : {}", displayDetail(surveyUnits));
        List<String> surveyUnitIds = surveyUnits.stream()
                .map(PilotageSurveyUnit::id)
                .toList();

        log.info("Survey units found in pilotage api: {}", surveyUnitIds.size());
        return surveyUnitIds;
    }

    private String displayDetail(List<PilotageSurveyUnit> surveyUnits) {
        Map<String, Integer> countSurveyUnitsByCampaign = new HashMap<>();
        for (PilotageSurveyUnit surveyUnit : surveyUnits) {
            String campaign = surveyUnit.campaign();
            if(!countSurveyUnitsByCampaign.containsKey(campaign)) {
                countSurveyUnitsByCampaign.put(surveyUnit.campaign(), 1);
                continue;
            }
            int count = countSurveyUnitsByCampaign.get(campaign) + 1;
            countSurveyUnitsByCampaign.put(campaign, count);
        }

        return "[" + countSurveyUnitsByCampaign.entrySet()
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

        return campaigns.stream()
                .map(PilotageCampaign::id)
                .map(campaignId -> new PilotageCampaign(campaignId, questionnaireModelService.getQuestionnaireIds(campaignId)))
                .toList();
    }

    @Override
    @Cacheable(value = CacheName.HABILITATION, key = "{#surveyUnit.id, #surveyUnit.campaignId, #role, #idep}")
    public boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep, String authToken) {
        return pilotageRepository.hasHabilitation(surveyUnit, role, idep, authToken);
    }
}
