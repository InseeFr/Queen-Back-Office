package fr.insee.queen.domain.group.service;

import fr.insee.queen.domain.group.gateway.GroupRepository;
import fr.insee.queen.domain.group.model.GroupSummary;
import fr.insee.queen.domain.group.service.exception.GroupNotLinkedToQuestionnaireException;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class GroupExistenceApiService implements GroupExistenceService {
    private final GroupRepository groupRepository;
    private final CacheManager cacheManager;
    public static final String NOT_FOUND_MESSAGE = "Group %s was not found";
    public static final String ALREADY_EXIST_MESSAGE = "Group %s already exist";

    @Override
    public void throwExceptionIfGroupNotExist(String groupId) {
        if (!existsById(groupId)) {
            throw new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, groupId));
        }
    }

    @Override
    public void throwExceptionIfGroupAlreadyExist(String groupId) {
        if (existsById(groupId)) {
            throw new EntityAlreadyExistException(String.format(ALREADY_EXIST_MESSAGE, groupId));
        }
    }

    @Override
    public void throwExceptionIfGroupNotLinkedToQuestionnaire(String groupId, String questionnaireId) {
        GroupSummary group = groupRepository.findWithQuestionnaireIds(groupId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, groupId)));
        if(!group.getQuestionnaireIds().contains(questionnaireId)) {
            throw new GroupNotLinkedToQuestionnaireException(groupId, questionnaireId);
        }
    }

    @Override
    public boolean existsById(String groupId) {
        // not using @Cacheable annotation here, to avoid problems with proxy class generation
        Boolean isGroupPresent = Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_EXIST)).get(groupId, Boolean.class);
        if (isGroupPresent != null) {
            return isGroupPresent;
        }
        isGroupPresent = groupRepository.exists(groupId);
        Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_EXIST)).putIfAbsent(groupId, isGroupPresent);

        return isGroupPresent;
    }
}
