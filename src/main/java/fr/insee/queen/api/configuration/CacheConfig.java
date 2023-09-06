package fr.insee.queen.api.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {
    @Bean
    public CaffeineCache habilitationCache() {
        return new CaffeineCache("habilitations",
                Caffeine.newBuilder()
                        .initialCapacity(200)
                        .maximumSize(5000)
                        .expireAfterAccess(5, TimeUnit.MINUTES)
                        .build());
    }
}
