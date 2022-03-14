package fr.insee.queen.api.configuration;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableCaching
@Profile("!test")
public class CacheConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfig.class);

    @Autowired
    CacheManager cacheManager;

    @Bean
    public Consumer<String> evictCampaignFromCache() {

        return campaignId -> {
            LOGGER.info("{} removed from campaign cache", campaignId);
            cacheManager.getCache("campaign").evict(campaignId);
        };
    };

}
