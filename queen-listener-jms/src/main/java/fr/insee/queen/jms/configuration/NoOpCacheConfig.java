package fr.insee.queen.jms.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class NoOpCacheConfig {
    @Bean
    @Primary
    protected CacheManager cacheManager() {
        log.info("NoOpCacheManager()");
        return new NoOpCacheManager();
    }
}
