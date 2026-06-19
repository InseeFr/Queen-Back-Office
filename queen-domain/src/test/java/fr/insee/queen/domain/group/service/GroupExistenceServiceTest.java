package fr.insee.queen.domain.campaign.service;

import fr.insee.queen.domain.campaign.service.exception.CampaignNotLinkedToQuestionnaireException;
import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.campaign.infrastructure.dummy.CampaignFakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;

import static org.assertj.core.api.Assertions.*;

class CampaignExistenceServiceTest {

    private CampaignExistenceService campaignExistenceService;
    private CampaignFakeRepository campaignFakeRepository = new CampaignFakeRepository();

    @BeforeEach
    void init() {
        campaignFakeRepository = new CampaignFakeRepository();
        CacheManager cacheManager = new NoOpCacheManager();
        campaignExistenceService = new CampaignExistenceApiService(campaignFakeRepository, cacheManager);
    }

    @Test
    @DisplayName("When checking campaign existence, if campaign not exists, then throws exception")
    void test_campaign_existence_01() {
        campaignFakeRepository.setCampaignExists(false);
        assertThatThrownBy(() -> campaignExistenceService.throwExceptionIfCampaignNotExist(CampaignFakeRepository.CAMPAIGN_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When checking campaign existence, if campaign exists, resume")
    void test_campaign_existence_02() {
        assertThatCode(() -> campaignExistenceService.throwExceptionIfCampaignNotExist(CampaignFakeRepository.CAMPAIGN_ID))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When checking campaign existence, if campaign already exists, then throws exception")
    void test_campaign_existence_03() {
        assertThatThrownBy(() -> campaignExistenceService.throwExceptionIfCampaignAlreadyExist(CampaignFakeRepository.CAMPAIGN_ID))
                .isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    @DisplayName("When checking campaign existence, if campaign does not exist, resume")
    void test_campaign_existence_04() {
        campaignFakeRepository.setCampaignExists(false);
        assertThatCode(() -> campaignExistenceService.throwExceptionIfCampaignAlreadyExist(CampaignFakeRepository.CAMPAIGN_ID))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When checking link between a campaign and a questionnaire, if campaign does not exist, then throws exception")
    void test_campaign_existence_05() {
        campaignFakeRepository.setCampaignExists(false);
        assertThatThrownBy(() -> campaignExistenceService.throwExceptionIfCampaignNotLinkedToQuestionnaire(CampaignFakeRepository.CAMPAIGN_ID, "id-questionnaire"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When checking link between a campaign and a questionnaire, if campaign and questionnaire are not linked, then throws exception")
    void test_campaign_existence_06() {
        assertThatThrownBy(() -> campaignExistenceService.throwExceptionIfCampaignNotLinkedToQuestionnaire(CampaignFakeRepository.CAMPAIGN_ID, "random-id"))
                .isInstanceOf(CampaignNotLinkedToQuestionnaireException.class);
    }

    @Test
    @DisplayName("When checking link between a campaign and a questionnaire, if campaign and questionnaire are linked, resume")
    void test_campaign_existence_07() {
        assertThatCode(() -> campaignExistenceService.throwExceptionIfCampaignNotLinkedToQuestionnaire(CampaignFakeRepository.CAMPAIGN_ID, CampaignFakeRepository.QUESTIONNAIRE_LINKED_ID))
                .doesNotThrowAnyException();
    }
}
