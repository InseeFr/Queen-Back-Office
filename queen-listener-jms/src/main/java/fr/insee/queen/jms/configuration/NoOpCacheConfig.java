package fr.insee.queen.jms.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class NoOpCacheConfig {
    @Bean
    @Primary
    protected CacheManager cacheManager() {
        return new NoOpCacheManager();
    }
}
