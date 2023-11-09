package fr.insee.queen.api.configuration.cache;

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
        return new CaffeineCache(CacheName.HABILITATION,
                Caffeine.newBuilder()
                        .initialCapacity(2000)
                        .maximumSize(20000)
                        .expireAfterAccess(10, TimeUnit.MINUTES)
                        .build());
    }

    @Bean
    public CaffeineCache nomenclaturesCache() {
        return new CaffeineCache(CacheName.NOMENCLATURE,
                Caffeine.newBuilder()
                        .initialCapacity(10)
                        .maximumSize(100)
                        .expireAfterWrite(8, TimeUnit.HOURS)
                        .build());
    }

    @Bean
    public CaffeineCache metadataCache() {
        return new CaffeineCache(CacheName.QUESTIONNAIRE_METADATA,
                Caffeine.newBuilder()
                        .initialCapacity(10)
                        .maximumSize(100)
                        .expireAfterWrite(8, TimeUnit.HOURS)
                        .build());
    }

    @Bean
    public CaffeineCache questionnairesCache() {
        return new CaffeineCache(CacheName.QUESTIONNAIRE,
                Caffeine.newBuilder()
                        .initialCapacity(10)
                        .maximumSize(100)
                        .expireAfterWrite(8, TimeUnit.HOURS)
                        .build());
    }

    @Bean
    public CaffeineCache requiredNomenclaturesByQuestionnaireCache() {
        return new CaffeineCache(CacheName.QUESTIONNAIRE_NOMENCLATURES,
                Caffeine.newBuilder()
                        .initialCapacity(10)
                        .maximumSize(50)
                        .expireAfterWrite(8, TimeUnit.HOURS)
                        .build());
    }

    @Bean
    public CaffeineCache campaignsExistenceCache() {
        return new CaffeineCache(CacheName.CAMPAIGN_EXIST,
                Caffeine.newBuilder()
                        .initialCapacity(10)
                        .maximumSize(100)
                        .expireAfterWrite(8, TimeUnit.HOURS)
                        .build());
    }

    @Bean
    public CaffeineCache surveyUnitExistenceCache() {
        return new CaffeineCache(CacheName.SURVEY_UNIT_EXIST,
                Caffeine.newBuilder()
                        .initialCapacity(2000)
                        .maximumSize(20000)
                        .expireAfterAccess(10, TimeUnit.MINUTES)
                        .build());
    }

    @Bean
    public CaffeineCache surveyUnitCampaignCache() {
        return new CaffeineCache(CacheName.SURVEY_UNIT_CAMPAIGN,
                Caffeine.newBuilder()
                        .initialCapacity(2000)
                        .maximumSize(20000)
                        .expireAfterAccess(10, TimeUnit.MINUTES)
                        .build());
    }
}
