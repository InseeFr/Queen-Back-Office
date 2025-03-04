package fr.insee.queen.application.surveyunit.integration.cache;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.domain.campaign.service.CampaignApiService;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitApiService;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ActiveProfiles("test-cache")
class SurveyUnitCacheTests extends ContainerConfiguration {

    @Autowired
    private SurveyUnitApiService surveyUnitService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CampaignApiService campaignService;

    @AfterEach
    void clearCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Test
    @DisplayName("When creating/updating surveyUnits, handle correctly cache for surveyUnit existence")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_surveyUnit_existence_cache_01() throws StateDataInvalidDateException {
        String surveyUnitId = "survey-unit-cache-id";
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId)).isNull();
        surveyUnitService.existsById(surveyUnitId);
        Boolean surveyUnitExist =  Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST).get(surveyUnitId, Boolean.class));
        assertThat(surveyUnitExist).isFalse();

        SurveyUnit surveyUnit = new SurveyUnit(surveyUnitId,
                "LOG2021X11Tel",
                "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                null);
        surveyUnitService.createSurveyUnit(surveyUnit);

        // not retrieving yet so no survey unit in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId)).isNull();

        surveyUnitService.existsById(surveyUnitId);

        // now survey unit existence is in cache
        surveyUnitExist = Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST).get(surveyUnitId, Boolean.class));
        assertThat(surveyUnitExist).isTrue();

        surveyUnitService.delete(surveyUnitId);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId)).isNull();
    }

    @Test
    @DisplayName("When creating survey unit and delete the related campaign, then the cache for surveyUnit existence is deleted")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_surveyUnit_existence_cache_02() throws StateDataInvalidDateException {
        String surveyUnitId = "survey-unit-cache-id";
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId)).isNull();
        surveyUnitService.existsById(surveyUnitId);
        Boolean surveyUnitExist =  Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST).get(surveyUnitId, Boolean.class));
        assertThat(surveyUnitExist).isFalse();

        String campaignId = "LOG2021X11Tel";
        SurveyUnit surveyUnit = new SurveyUnit(surveyUnitId,
                campaignId,
                "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                null);
        surveyUnitService.createSurveyUnit(surveyUnit);

        // not retrieving yet so no survey unit in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId)).isNull();

        surveyUnitService.existsById(surveyUnitId);

        // now survey unit existence is in cache
        surveyUnitExist = Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST).get(surveyUnitId, Boolean.class));
        assertThat(surveyUnitExist).isTrue();

        campaignService.delete(campaignId);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId)).isNull();
    }

    @Test
    @DisplayName("When handling surveyUnits, handle correctly cache for survey units with campaign")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_surveyUnit_campaign_cache_01() throws StateDataInvalidDateException {
        String surveyUnitId = "survey-unit-campaign-cache-id";

        // check cache is null at beginning
        assertThatThrownBy(() -> surveyUnitService.getSummaryById(surveyUnitId)).isInstanceOf(EntityNotFoundException.class);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).get(surveyUnitId)).isNull();

        SurveyUnit surveyUnit = new SurveyUnit(surveyUnitId,
                "LOG2021X11Tel",
                "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                null);
        surveyUnitService.createSurveyUnit(surveyUnit);

        // not retrieving yet so no survey unit in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).get(surveyUnitId)).isNull();

        SurveyUnitSummary expectedSurveyUnitSummary = surveyUnitService.getSummaryById(surveyUnitId);

        // now survey unit is in cache
        SurveyUnitSummary surveyUnitSummary = Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY).get(surveyUnitId, SurveyUnitSummary.class));
        assertThat(surveyUnitSummary).isEqualTo(expectedSurveyUnitSummary);

        surveyUnitService.delete(surveyUnitId);

        // after deletion, no survey unit anymore in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).get(surveyUnitId)).isNull();
    }

    @Test
    @DisplayName("When creating survey unit and delete the related campaign, then the cache for surveyUnit summary is deleted")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_surveyUnit_campaign_cache_02() throws StateDataInvalidDateException {
        String surveyUnitId = "survey-unit-campaign-cache-id";
        String campaignId = "LOG2021X11Tel";
        // check cache is null at beginning
        assertThatThrownBy(() -> surveyUnitService.getSummaryById(surveyUnitId)).isInstanceOf(EntityNotFoundException.class);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).get(surveyUnitId)).isNull();

        SurveyUnit surveyUnit = new SurveyUnit(surveyUnitId,
                campaignId,
                "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                JsonNodeFactory.instance.objectNode(),
                null);
        surveyUnitService.createSurveyUnit(surveyUnit);

        // not retrieving yet so no survey unit in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).get(surveyUnitId)).isNull();

        SurveyUnitSummary expectedSurveyUnitSummary = surveyUnitService.getSummaryById(surveyUnitId);

        // now survey unit is in cache
        SurveyUnitSummary surveyUnitSummary = Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY).get(surveyUnitId, SurveyUnitSummary.class));
        assertThat(surveyUnitSummary).isEqualTo(expectedSurveyUnitSummary);

        campaignService.delete(campaignId);

        // after deletion, no survey unit anymore in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).get(surveyUnitId)).isNull();
    }
}
