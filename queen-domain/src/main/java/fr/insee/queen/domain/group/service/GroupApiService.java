package fr.insee.queen.domain.group.service;

import fr.insee.queen.domain.group.service.exception.GroupDeletionException;
import fr.insee.queen.domain.group.service.exception.QuestionnaireInvalidException;
import fr.insee.queen.domain.group.gateway.GroupKindProvider;
import fr.insee.queen.domain.group.gateway.GroupRepository;
import fr.insee.queen.domain.group.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.model.GroupSummary;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class GroupApiService implements GroupService {
    private final GroupRepository groupRepository;
    private final InterrogationRepository interrogationRepository;
    private final QuestionnaireModelRepository questionnaireModelRepository;
    private final GroupExistenceService groupExistenceService;
    private final CacheManager cacheManager;
    private final GroupKindProvider groupKindProvider;

    @Override
    public Group getGroup(String groupId) {
        return groupRepository.findGroup(groupId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Group %s not found", groupId)));
    }

    @Override
    public List<String> getAllGroupIds() {
        return groupRepository.getAllGroupIds();
    }

    public List<GroupSummary> getAllGroups() {
        return groupRepository.getAllWithQuestionnaireIds();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.GROUP_EXIST, key = "#groupId"),
            @CacheEvict(value = CacheName.GROUP_METADATA, key = "#groupId"),
            @CacheEvict(value = CacheName.INTERROGATION_EXIST, allEntries = true),
            @CacheEvict(value = CacheName.INTERROGATION_SUMMARY, allEntries = true)
    })
    @Override
    public void delete(String groupId, boolean deleteInterrogations) {
        if(deleteInterrogations) {
            log.info("Deleting interrogations for group {}", groupId);
            interrogationRepository.deleteInterrogations(groupId);
        } else {
            if (interrogationRepository.existsByGroupId(groupId)) {
                log.info("Checking existence of interrogations for group {}", groupId);
                throw new GroupDeletionException(
                        String.format("Cannot delete group %s because interrogations still exist", groupId)
                );
            }
        }

        GroupSummary groupSummary = groupRepository.
                findWithQuestionnaireIds(groupId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Group %s not found", groupId)));

        Set<String> questionnaireIds = groupSummary.getQuestionnaireIds();

        if (questionnaireIds != null && !questionnaireIds.isEmpty()) {
            questionnaireIds.forEach(id -> {
                Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES))
                        .evict(id);
                Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE))
                        .evict(id);
            });
        }
        groupRepository.delete(groupId);
        if (questionnaireIds != null && !questionnaireIds.isEmpty()) {
            questionnaireModelRepository.deleteOrphanedQuestionnaires(questionnaireIds);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.GROUP_EXIST, key = "#group.id")
    })
    @Override
    public void createGroup(Group group) {
        String groupId = group.getId();
        groupExistenceService.throwExceptionIfGroupAlreadyExist(groupId);
        throwExceptionIfQuestionnairesNotFound(group.getQuestionnaireIds());
        groupRepository.create(group, groupKindProvider.getKind());
    }

    @Caching(evict = {
            @CacheEvict(value = CacheName.GROUP_METADATA, key = "#group.id"),
    })
    @Override
    public void updateGroup(Group group) {
        String groupId = group.getId();
        groupExistenceService.throwExceptionIfGroupNotExist(groupId);
        throwExceptionIfQuestionnairesNotFound(group.getQuestionnaireIds());
        groupRepository.update(group);
    }

    private void throwExceptionIfQuestionnairesNotFound(Set<String> questionnaireIds) {
        Long count = questionnaireModelRepository.countExistingQuestionnaires(questionnaireIds);
        if (questionnaireIds.size() != count) {
            throw new QuestionnaireInvalidException("One or more questionnaires do not exist. Creation aborted.");
        }
    }
}
