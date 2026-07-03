package fr.insee.queen.domain.pilotage.service;

import fr.insee.queen.domain.group.service.GroupExistenceService;
import fr.insee.queen.domain.group.service.QuestionnaireModelService;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.pilotage.service.exception.PilotageApiException;
import fr.insee.queen.domain.pilotage.gateway.PilotageRepository;
import fr.insee.queen.domain.pilotage.model.PilotageGroup;
import fr.insee.queen.domain.pilotage.model.PilotageInterrogation;
import fr.insee.queen.domain.interrogation.service.InterrogationService;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PilotageApiService implements PilotageService {
    private final InterrogationService interrogationService;
    private final GroupExistenceService groupExistenceService;
    private final PilotageRepository pilotageRepository;
    private final QuestionnaireModelService questionnaireModelService;

    @Override
    public boolean isClosed(String groupId) {
        groupExistenceService.throwExceptionIfGroupNotExist(groupId);
        return pilotageRepository.isClosed(groupId);
    }

    @Override
    public List<InterrogationSummary> getInterrogations(String groupId) {
        groupExistenceService.throwExceptionIfGroupNotExist(groupId);
        Map<String, InterrogationSummary> interrogationMap = new HashMap<>();

        List<String> interrogationIds = getInterrogationIds(groupId);

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
     * Retrieve interrogation ids for the current interviewer for a group
     *
     * @param groupId group id
     * @return List of interrogation ids
     */
    private List<String> getInterrogationIds(String groupId) {
        List<PilotageInterrogation> interrogations = pilotageRepository.getInterrogations();

        if (interrogations == null || interrogations.isEmpty()) {
            return Collections.emptyList();
        }

        log.debug("Detail : {}", displayDetail(interrogations));
        List<String> interrogationIds = interrogations.stream()
                .filter(interrogation -> groupId.equals(interrogation.campaign()))
                .map(PilotageInterrogation::id)
                .toList();

        log.info("Interrogations found in pilotage api for group {}: {}", groupId, interrogationIds.size());
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
        Map<String, Integer> countInterrogationsByGroup = new HashMap<>();
        for (PilotageInterrogation interrogation : interrogations) {
            String group = interrogation.campaign();
            if(!countInterrogationsByGroup.containsKey(group)) {
                countInterrogationsByGroup.put(interrogation.campaign(), 1);
                continue;
            }
            int count = countInterrogationsByGroup.get(group) + 1;
            countInterrogationsByGroup.put(group, count);
        }

        return "[" + countInterrogationsByGroup.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " Interrogation")
                .collect(Collectors.joining("; ")) + "]";

    }

    @Override
    public List<PilotageGroup> getInterviewerGroups() {
        List<PilotageGroup> groups = pilotageRepository.getInterviewerGroups();
        if (groups == null) {
            log.error("Pilotage API does not have a body (was expecting a group list)");
            throw new PilotageApiException();
        }

        return groups.stream()
                .map(PilotageGroup::id)
                .map(groupId -> {
                    try {
                        return new PilotageGroup(groupId, questionnaireModelService.getQuestionnaireIds(groupId));
                    } catch (EntityNotFoundException ex) {
                        log.error("Group id {} from pilotage API was not found in the DB", groupId, ex);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
