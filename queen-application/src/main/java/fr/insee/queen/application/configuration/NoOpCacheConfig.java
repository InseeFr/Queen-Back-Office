package fr.insee.queen.application.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnProperty(
        value = "feature.cache.enabled",
        havingValue = "false")
public class NoOpCacheConfig {
    @Bean
    @Primary
    protected CacheManager cacheManager() {
        return new NoOpCacheManager();
    }
}
