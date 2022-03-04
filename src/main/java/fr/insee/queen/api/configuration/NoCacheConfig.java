package fr.insee.queen.api.configuration;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@Profile("test")
public class NoCacheConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoCacheConfig.class);

    @Bean
    public Consumer<String> evictCampaignFromCache() {
        return campaignId -> LOGGER.info("no-cache operation");
    };

}
