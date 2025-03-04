package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.campaign.service.CampaignExistenceService;
import fr.insee.queen.domain.campaign.service.QuestionnaireModelService;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.domain.pilotage.gateway.PilotageRepository;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.model.PilotageSurveyUnit;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
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
    public boolean isClosed(String campaignId) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        return pilotageRepository.isClosed(campaignId);
    }

    @Override
    public List<SurveyUnitSummary> getSurveyUnitsByCampaign(String campaignId) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        Map<String, SurveyUnitSummary> surveyUnitMap = new HashMap<>();

        List<String> surveyUnitIds = getSurveyUnitIds(campaignId);

        surveyUnitService.findSummariesByIds(surveyUnitIds)
                .forEach(surveyUnitSummary ->
                        surveyUnitMap.putIfAbsent(surveyUnitSummary.id(), surveyUnitSummary)
                );
        return surveyUnitMap.values().stream().toList();
    }

    @Override
    public List<SurveyUnit> getInterviewerSurveyUnits() {
        Map<String, SurveyUnit> surveyUnitMap = new HashMap<>();
        List<String> surveyUnitIds = getSurveyUnitIds();

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
     * @return List of survey unit ids
     */
    private List<String> getSurveyUnitIds(String campaignId) {
        List<PilotageSurveyUnit> surveyUnits = pilotageRepository.getSurveyUnits();

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
     * @return List of survey unit ids
     */
    private List<String> getSurveyUnitIds() {
        List<PilotageSurveyUnit> surveyUnits = pilotageRepository.getSurveyUnits();

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
    public List<PilotageCampaign> getInterviewerCampaigns() {
        List<PilotageCampaign> campaigns = pilotageRepository.getInterviewerCampaigns();
        if (campaigns == null) {
            log.error("Pilotage API does not have a body (was expecting a campaign list)");
            throw new PilotageApiException();
        }

        return campaigns.stream()
                .map(PilotageCampaign::id)
                .map(campaignId -> {
                    try {
                        return new PilotageCampaign(campaignId, questionnaireModelService.getQuestionnaireIds(campaignId));
                    } catch (EntityNotFoundException ex) {
                        log.error("Campaign id {} from pilotage API was not found in the DB", campaignId);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Cacheable(value = CacheName.HABILITATION, key = "{#surveyUnit.id, #surveyUnit.campaignId, #role, #idep}")
    public boolean hasHabilitation(SurveyUnitSummary surveyUnit, PilotageRole role, String idep) {
        return pilotageRepository.hasHabilitation(surveyUnit, role, idep);
    }
}
