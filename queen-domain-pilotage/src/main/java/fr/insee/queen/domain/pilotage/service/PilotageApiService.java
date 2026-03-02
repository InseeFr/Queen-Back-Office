package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.campaign.service.CampaignExistenceService;
import fr.insee.queen.domain.campaign.service.QuestionnaireModelService;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.domain.pilotage.gateway.PilotageRepository;
import fr.insee.queen.domain.pilotage.model.PilotageCampaign;
import fr.insee.queen.domain.pilotage.model.PilotageInterrogation;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
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
    private final InterrogationService interrogationService;
    private final CampaignExistenceService campaignExistenceService;
    private final PilotageRepository pilotageRepository;
    private final QuestionnaireModelService questionnaireModelService;

    @Override
    public boolean isClosed(String campaignId) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        return pilotageRepository.isClosed(campaignId);
    }

    @Override
    public List<InterrogationSummary> getInterrogationsByCampaign(String campaignId) {
        campaignExistenceService.throwExceptionIfCampaignNotExist(campaignId);
        Map<String, InterrogationSummary> interrogationMap = new HashMap<>();

        List<String> interrogationIds = getInterrogationIds(campaignId);

        interrogationService.findSummariesByIds(interrogationIds)
                .forEach(interrogationSummary ->
                        interrogationMap.putIfAbsent(interrogationSummary.id(), interrogationSummary)
                );
        return interrogationMap.values().stream().toList();
    }

    @Override
    public List<Interrogation> getInterviewerInterrogations() {
        Map<String, Interrogation> interrogationMap = new HashMap<>();
        List<String> interrogationIds = getInterrogationIds();

        interrogationService.findByIds(interrogationIds)
                .forEach(interrogation ->
                        interrogationMap.putIfAbsent(interrogation.id(), interrogation)
                );
        return interrogationMap.values().stream().toList();
    }

    /**
     * Retrieve interrogation ids for the current interviewer for a campaign
     *
     * @param campaignId campaign id
     * @return List of interrogation ids
     */
    private List<String> getInterrogationIds(String campaignId) {
        List<PilotageInterrogation> interrogations = pilotageRepository.getInterrogations();

        if (interrogations == null || interrogations.isEmpty()) {
            return Collections.emptyList();
        }

        log.debug("Detail : {}", displayDetail(interrogations));
        List<String> interrogationIds = interrogations.stream()
                .filter(interrogation -> campaignId.equals(interrogation.campaign()))
                .map(PilotageInterrogation::id)
                .toList();

        log.info("Interrogations found in pilotage api for campaign {}: {}", campaignId, interrogationIds.size());
        return interrogationIds;
    }

    /**
     * Retrieve interrogation ids for the current interviewer
     *
     * @return List of interrogation ids
     */
    private List<String> getInterrogationIds() {
        List<PilotageInterrogation> interrogations = pilotageRepository.getInterrogations();

        if (interrogations == null || interrogations.isEmpty()) {
            return Collections.emptyList();
        }

        log.debug("Detail : {}", displayDetail(interrogations));
        List<String> interrogationIds = interrogations.stream()
                .map(PilotageInterrogation::id)
                .toList();

        log.info("Interrogations found in pilotage api: {}", interrogationIds.size());
        return interrogationIds;
    }

    private String displayDetail(List<PilotageInterrogation> interrogations) {
        Map<String, Integer> countInterrogationsByCampaign = new HashMap<>();
        for (PilotageInterrogation interrogation : interrogations) {
            String campaign = interrogation.campaign();
            if(!countInterrogationsByCampaign.containsKey(campaign)) {
                countInterrogationsByCampaign.put(interrogation.campaign(), 1);
                continue;
            }
            int count = countInterrogationsByCampaign.get(campaign) + 1;
            countInterrogationsByCampaign.put(campaign, count);
        }

        return "[" + countInterrogationsByCampaign.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " Interrogation")
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
}
