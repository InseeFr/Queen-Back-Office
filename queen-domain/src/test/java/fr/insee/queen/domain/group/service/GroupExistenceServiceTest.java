package fr.insee.queen.domain.group.service;

import fr.insee.queen.domain.group.service.exception.GroupNotLinkedToQuestionnaireException;
import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.group.infrastructure.dummy.GroupFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;

import static org.assertj.core.api.Assertions.*;

class GroupExistenceServiceTest {

    private GroupExistenceService groupExistenceService;
    private GroupFakeRepository groupFakeRepository = new GroupFakeRepository();

    @BeforeEach
    void init() {
        groupFakeRepository = new GroupFakeRepository();
        CacheManager cacheManager = new NoOpCacheManager();
        groupExistenceService = new GroupExistenceApiService(groupFakeRepository, cacheManager);
    }

    @Test
    @DisplayName("When checking group existence, if group not exists, then throws exception")
    void test_group_existence_01() {
        groupFakeRepository.setGroupExists(false);
        assertThatThrownBy(() -> groupExistenceService.throwExceptionIfGroupNotExist(GroupFakeRepository.GROUP_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When checking group existence, if group exists, resume")
    void test_group_existence_02() {
        assertThatCode(() -> groupExistenceService.throwExceptionIfGroupNotExist(GroupFakeRepository.GROUP_ID))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When checking group existence, if group already exists, then throws exception")
    void test_group_existence_03() {
        assertThatThrownBy(() -> groupExistenceService.throwExceptionIfGroupAlreadyExist(GroupFakeRepository.GROUP_ID))
                .isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    @DisplayName("When checking group existence, if group does not exist, resume")
    void test_group_existence_04() {
        groupFakeRepository.setGroupExists(false);
        assertThatCode(() -> groupExistenceService.throwExceptionIfGroupAlreadyExist(GroupFakeRepository.GROUP_ID))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When checking link between a group and a questionnaire, if group does not exist, then throws exception")
    void test_group_existence_05() {
        groupFakeRepository.setGroupExists(false);
        assertThatThrownBy(() -> groupExistenceService.throwExceptionIfGroupNotLinkedToQuestionnaire(GroupFakeRepository.GROUP_ID, "id-questionnaire"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When checking link between a group and a questionnaire, if group and questionnaire are not linked, then throws exception")
    void test_group_existence_06() {
        assertThatThrownBy(() -> groupExistenceService.throwExceptionIfGroupNotLinkedToQuestionnaire(GroupFakeRepository.GROUP_ID, "random-id"))
                .isInstanceOf(GroupNotLinkedToQuestionnaireException.class);
    }

    @Test
    @DisplayName("When checking link between a group and a questionnaire, if group and questionnaire are linked, resume")
    void test_group_existence_07() {
        assertThatCode(() -> groupExistenceService.throwExceptionIfGroupNotLinkedToQuestionnaire(GroupFakeRepository.GROUP_ID, GroupFakeRepository.QUESTIONNAIRE_LINKED_ID))
                .doesNotThrowAnyException();
    }
}
