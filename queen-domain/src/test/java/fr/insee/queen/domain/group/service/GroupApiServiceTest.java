package fr.insee.queen.domain.group.service;

import fr.insee.queen.domain.group.gateway.GroupRepository;
import fr.insee.queen.domain.group.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.group.model.GroupSummary;
import fr.insee.queen.domain.group.service.exception.GroupDeletionException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupApiServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private InterrogationRepository interrogationRepository;
    @Mock
    private QuestionnaireModelRepository questionnaireModelRepository;
    @Mock
    private GroupExistenceService groupExistenceService;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache dummyCache; // stub cache to avoid NPE, cache behavior is tested in integration tests

    private GroupApiService service;

    @BeforeEach
    void setUp() {
        service = new GroupApiService(
                groupRepository,
                interrogationRepository,
                questionnaireModelRepository,
                groupExistenceService,
                cacheManager
        );
    }

    @Test
    void delete_should_delete_interrogations() {
        // Given
        String groupId = "C1";
        GroupSummary summary = mock(GroupSummary.class);
        when(summary.getQuestionnaireIds()).thenReturn(Set.of());
        when(groupRepository.findWithQuestionnaireIds(groupId)).thenReturn(Optional.of(summary));

        // When
        service.delete(groupId, true);

        // Then
        verify(interrogationRepository).deleteInterrogations(groupId);
        verify(interrogationRepository, never()).existsByGroupId(anyString());

        verify(questionnaireModelRepository, never()).deleteAllFromGroup(anyString());
        verify(groupRepository).delete(groupId);
    }

    @Test
    void delete_should_throw_group_exception_when_interrogations_exist() {
        // Given
        String groupId = "C2";
        when(interrogationRepository.existsByGroupId(groupId)).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> service.delete(groupId, false))
                .isInstanceOf(GroupDeletionException.class)
                .hasMessageContaining(groupId);

        // Ensure nothing else happens after the guard clause.
        verify(interrogationRepository).existsByGroupId(groupId);
        verify(interrogationRepository, never()).deleteInterrogations(anyString());

        verify(groupRepository, never()).findWithQuestionnaireIds(anyString());
        verify(questionnaireModelRepository, never()).deleteAllFromGroup(anyString());
        verify(groupRepository, never()).delete(anyString());
    }

    @Test
    void delete_shouldProceedAndDeleteGroup_whenFlagFalse_andNoInterrogations() {
        // Given
        String groupId = "C3";
        when(interrogationRepository.existsByGroupId(groupId)).thenReturn(false);

        GroupSummary summary = mock(GroupSummary.class);
        when(summary.getQuestionnaireIds()).thenReturn(Set.of());
        when(groupRepository.findWithQuestionnaireIds(groupId)).thenReturn(Optional.of(summary));

        // When
        service.delete(groupId, false);

        // Then
        verify(interrogationRepository).existsByGroupId(groupId);
        verify(interrogationRepository, never()).deleteInterrogations(anyString());

        verify(questionnaireModelRepository, never()).deleteAllFromGroup(anyString());
        verify(groupRepository).delete(groupId);
    }

    @Test
    void delete_shouldThrowEntityNotFoundException_whenGroupNotFound() {
        // Given
        String groupId = "C4";
        when(interrogationRepository.existsByGroupId(groupId)).thenReturn(false);
        when(groupRepository.findWithQuestionnaireIds(groupId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.delete(groupId, false))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(groupId);

        // Ensure no deletion occurs when group does not exist.
        verify(groupRepository, never()).delete(anyString());
        verify(questionnaireModelRepository, never()).deleteAllFromGroup(anyString());
    }

    @Test
    void delete_shouldDeleteQuestionnaireModels_thenDeleteGroup_whenQuestionnairesExist() {
        // Given
        String groupId = "C5";
        Set<String> questionnaireIds = Set.of("Q1", "Q2");

        GroupSummary summary = mock(GroupSummary.class);
        when(summary.getQuestionnaireIds()).thenReturn(questionnaireIds);
        when(groupRepository.findWithQuestionnaireIds(groupId)).thenReturn(Optional.of(summary));
        // Cache behavior is out of scope for these unit tests; we only prevent NPEs.
        when(cacheManager.getCache(anyString())).thenReturn(dummyCache);

        // When
        service.delete(groupId, true);

        // Then
        verify(interrogationRepository).deleteInterrogations(groupId);
        verify(questionnaireModelRepository).deleteAllFromGroup(groupId);
        verify(groupRepository).delete(groupId);

        // Ensure questionnaire cleanup happens before group deletion.
        InOrder inOrder = inOrder(questionnaireModelRepository, groupRepository);
        inOrder.verify(questionnaireModelRepository).deleteAllFromGroup(groupId);
        inOrder.verify(groupRepository).delete(groupId);
    }

    @Test
    void delete_shouldNotDeleteQuestionnaireModels_whenQuestionnaireIdsIsNull() {
        // Given
        String groupId = "C6";

        GroupSummary summary = mock(GroupSummary.class);
        when(summary.getQuestionnaireIds()).thenReturn(null);
        when(groupRepository.findWithQuestionnaireIds(groupId)).thenReturn(Optional.of(summary));

        // When
        service.delete(groupId, true);

        // Then
        verify(questionnaireModelRepository, never()).deleteAllFromGroup(anyString());
        verify(groupRepository).delete(groupId);
    }
}

