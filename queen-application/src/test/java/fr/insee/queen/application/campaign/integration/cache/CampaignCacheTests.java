package fr.insee.queen.application.campaign.integration.cache;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.service.CampaignExistenceService;
import fr.insee.queen.domain.campaign.service.CampaignService;
import fr.insee.queen.domain.common.cache.CacheName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ActiveProfiles("test-cache")
class CampaignCacheTests extends ContainerConfiguration {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignExistenceService campaignExistenceService;

    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    public void clearCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Test
    @DisplayName("When handling campaigns, handle correctly cache for campaign existence")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_campaign_existence_cache() {
        String campaignId = "campaign-cache-id";
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST)).get(campaignId)).isNull();

        campaignExistenceService.existsById(campaignId);
        Boolean campaignExist = Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST).get(campaignId, Boolean.class));
        assertThat(campaignExist).isFalse();

        campaignService.createCampaign(new Campaign(campaignId, "label", new HashSet<>(), JsonNodeFactory.instance.objectNode()));
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST)).get(campaignId)).isNull();

        campaignExistenceService.existsById(campaignId);
        campaignExist =Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST).get(campaignId, Boolean.class));
        assertThat(campaignExist).isTrue();

        campaignService.delete(campaignId);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.CAMPAIGN_EXIST)).get(campaignId)).isNull();
    }
}
