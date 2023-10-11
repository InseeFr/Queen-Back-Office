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
                        .initialCapacity(2000)
                        .maximumSize(10000)
                        .expireAfterAccess(5, TimeUnit.MINUTES)
                        .build());
    }

    @Bean
    public CaffeineCache nomenclaturesCache() {
        return new CaffeineCache("nomenclatures",
                Caffeine.newBuilder()
                        .initialCapacity(10)
                        .maximumSize(100)
                        .expireAfterWrite(8, TimeUnit.HOURS)
                        .build());
    }

    @Bean
    public CaffeineCache campaignsCache() {
        return new CaffeineCache("campaigns",
                Caffeine.newBuilder()
                        .initialCapacity(10)
                        .maximumSize(100)
                        .expireAfterWrite(8, TimeUnit.HOURS)
                        .build());
    }

    @Bean
    public CaffeineCache requiredNomenclaturesByCampaignCache() {
        return new CaffeineCache("required-nomenclatures-campaign",
                Caffeine.newBuilder()
                        .initialCapacity(5)
                        .maximumSize(10)
                        .expireAfterWrite(8, TimeUnit.HOURS)
                        .build());
    }
}
