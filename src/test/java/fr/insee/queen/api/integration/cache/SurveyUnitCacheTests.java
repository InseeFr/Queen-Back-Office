package fr.insee.queen.api.integration.cache;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.input.SurveyUnitCreateInputDto;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto;
import fr.insee.queen.api.service.exception.EntityNotFoundException;
import fr.insee.queen.api.service.surveyunit.SurveyUnitApiService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cache-testing")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class SurveyUnitCacheTests {

    @Autowired
    private SurveyUnitApiService surveyUnitService;
    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    public void clearCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Test
    @DisplayName("When handling surveyUnits, handle correctly cache for surveyUnit existence")
    void check_surveyUnit_existence_cache() {
        String surveyUnitId = "surveyU-unit-cache-id";
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId)).isNull();
        surveyUnitService.existsById(surveyUnitId);
        boolean surveyUnitExist = (boolean) Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST).get(surveyUnitId).get());
        assertThat(surveyUnitExist).isFalse();

        SurveyUnitCreateInputDto surveyUnitInput = new SurveyUnitCreateInputDto(surveyUnitId, "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                null);
        surveyUnitService.createSurveyUnit("LOG2021X11Tel", surveyUnitInput);

        // not retrieving yet so no survey unit in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId)).isNull();

        surveyUnitService.existsById(surveyUnitId);

        // now survey unit existence is in cache
        surveyUnitExist = (boolean) Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST).get(surveyUnitId).get());
        assertThat(surveyUnitExist).isTrue();

        surveyUnitService.delete(surveyUnitId);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId)).isNull();
    }

    @Test
    @DisplayName("When handling surveyUnits, handle correctly cache for survey units with campaign")
    void check_surveyUnit_campaign_cache() {
        String surveyUnitId = "survey-unit-campaign-cache-id";

        // check cache is null at beginning
        assertThatThrownBy(() -> surveyUnitService.getSurveyUnitWithCampaignById(surveyUnitId)).isInstanceOf(EntityNotFoundException.class);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_CAMPAIGN)).get(surveyUnitId)).isNull();

        SurveyUnitCreateInputDto surveyUnitInput = new SurveyUnitCreateInputDto(surveyUnitId, "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                null);
        surveyUnitService.createSurveyUnit("LOG2021X11Tel", surveyUnitInput);

        // not retrieving yet so no survey unit in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_CAMPAIGN)).get(surveyUnitId)).isNull();

        SurveyUnitHabilitationDto expectedSurveyUnit = surveyUnitService.getSurveyUnitWithCampaignById(surveyUnitId);

        // now survey unit is in cache
        SurveyUnitHabilitationDto surveyUnit = (SurveyUnitHabilitationDto) Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_CAMPAIGN).get(surveyUnitId).get());
        assertThat(surveyUnit).isEqualTo(expectedSurveyUnit);

        surveyUnitService.delete(surveyUnitId);

        // after deletion, no survey unit anymore in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_CAMPAIGN)).get(surveyUnitId)).isNull();
    }
}
