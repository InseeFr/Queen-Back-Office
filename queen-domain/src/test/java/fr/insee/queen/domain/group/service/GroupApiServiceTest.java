package fr.insee.queen.domain.campaign.service;

import fr.insee.queen.domain.campaign.gateway.CampaignRepository;
import fr.insee.queen.domain.campaign.gateway.QuestionnaireModelRepository;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.campaign.service.exception.CampaignDeletionException;
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
class CampaignApiServiceTest {

    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private InterrogationRepository interrogationRepository;
    @Mock
    private QuestionnaireModelRepository questionnaireModelRepository;
    @Mock
    private CampaignExistenceService campaignExistenceService;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache dummyCache; // stub cache to avoid NPE, cache behavior is tested in integration tests

    private CampaignApiService service;

    @BeforeEach
    void setUp() {
        service = new CampaignApiService(
                campaignRepository,
                interrogationRepository,
                questionnaireModelRepository,
                campaignExistenceService,
                cacheManager
        );
    }

    @Test
    void delete_should_delete_interrogations() {
        // Given
        String campaignId = "C1";
        CampaignSummary summary = mock(CampaignSummary.class);
        when(summary.getQuestionnaireIds()).thenReturn(Set.of());
        when(campaignRepository.findWithQuestionnaireIds(campaignId)).thenReturn(Optional.of(summary));

        // When
        service.delete(campaignId, true);

        // Then
        verify(interrogationRepository).deleteInterrogations(campaignId);
        verify(interrogationRepository, never()).existsByCampaignId(anyString());

        verify(questionnaireModelRepository, never()).deleteAllFromCampaign(anyString());
        verify(campaignRepository).delete(campaignId);
    }

    @Test
    void delete_should_throw_campaign_exception_when_interrogations_exist() {
        // Given
        String campaignId = "C2";
        when(interrogationRepository.existsByCampaignId(campaignId)).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> service.delete(campaignId, false))
                .isInstanceOf(CampaignDeletionException.class)
                .hasMessageContaining(campaignId);

        // Ensure nothing else happens after the guard clause.
        verify(interrogationRepository).existsByCampaignId(campaignId);
        verify(interrogationRepository, never()).deleteInterrogations(anyString());

        verify(campaignRepository, never()).findWithQuestionnaireIds(anyString());
        verify(questionnaireModelRepository, never()).deleteAllFromCampaign(anyString());
        verify(campaignRepository, never()).delete(anyString());
    }

    @Test
    void delete_shouldProceedAndDeleteCampaign_whenFlagFalse_andNoInterrogations() {
        // Given
        String campaignId = "C3";
        when(interrogationRepository.existsByCampaignId(campaignId)).thenReturn(false);

        CampaignSummary summary = mock(CampaignSummary.class);
        when(summary.getQuestionnaireIds()).thenReturn(Set.of());
        when(campaignRepository.findWithQuestionnaireIds(campaignId)).thenReturn(Optional.of(summary));

        // When
        service.delete(campaignId, false);

        // Then
        verify(interrogationRepository).existsByCampaignId(campaignId);
        verify(interrogationRepository, never()).deleteInterrogations(anyString());

        verify(questionnaireModelRepository, never()).deleteAllFromCampaign(anyString());
        verify(campaignRepository).delete(campaignId);
    }

    @Test
    void delete_shouldThrowEntityNotFoundException_whenCampaignNotFound() {
        // Given
        String campaignId = "C4";
        when(interrogationRepository.existsByCampaignId(campaignId)).thenReturn(false);
        when(campaignRepository.findWithQuestionnaireIds(campaignId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.delete(campaignId, false))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(campaignId);

        // Ensure no deletion occurs when campaign does not exist.
        verify(campaignRepository, never()).delete(anyString());
        verify(questionnaireModelRepository, never()).deleteAllFromCampaign(anyString());
    }

    @Test
    void delete_shouldDeleteQuestionnaireModels_thenDeleteCampaign_whenQuestionnairesExist() {
        // Given
        String campaignId = "C5";
        Set<String> questionnaireIds = Set.of("Q1", "Q2");

        CampaignSummary summary = mock(CampaignSummary.class);
        when(summary.getQuestionnaireIds()).thenReturn(questionnaireIds);
        when(campaignRepository.findWithQuestionnaireIds(campaignId)).thenReturn(Optional.of(summary));
        // Cache behavior is out of scope for these unit tests; we only prevent NPEs.
        when(cacheManager.getCache(anyString())).thenReturn(dummyCache);

        // When
        service.delete(campaignId, true);

        // Then
        verify(interrogationRepository).deleteInterrogations(campaignId);
        verify(questionnaireModelRepository).deleteAllFromCampaign(campaignId);
        verify(campaignRepository).delete(campaignId);

        // Ensure questionnaire cleanup happens before campaign deletion.
        InOrder inOrder = inOrder(questionnaireModelRepository, campaignRepository);
        inOrder.verify(questionnaireModelRepository).deleteAllFromCampaign(campaignId);
        inOrder.verify(campaignRepository).delete(campaignId);
    }

    @Test
    void delete_shouldNotDeleteQuestionnaireModels_whenQuestionnaireIdsIsNull() {
        // Given
        String campaignId = "C6";

        CampaignSummary summary = mock(CampaignSummary.class);
        when(summary.getQuestionnaireIds()).thenReturn(null);
        when(campaignRepository.findWithQuestionnaireIds(campaignId)).thenReturn(Optional.of(summary));

        // When
        service.delete(campaignId, true);

        // Then
        verify(questionnaireModelRepository, never()).deleteAllFromCampaign(anyString());
        verify(campaignRepository).delete(campaignId);
    }
}

