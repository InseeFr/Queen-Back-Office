package fr.insee.queen.application.surveyunit.integration.cache;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitApiService;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cache-testing")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase
@AutoConfigureMockMvc
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
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_surveyUnit_existence_cache() throws StateDataInvalidDateException {
        String surveyUnitId = "survey-unit-cache-id";
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST)).get(surveyUnitId)).isNull();
        surveyUnitService.existsById(surveyUnitId);
        Boolean surveyUnitExist =  Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_EXIST).get(surveyUnitId, Boolean.class));
        assertThat(surveyUnitExist).isFalse();

        SurveyUnit surveyUnit = new SurveyUnit(surveyUnitId,
                "LOG2021X11Tel",
                "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode().toString(),
                JsonNodeFactory.instance.objectNode().toString(),
                JsonNodeFactory.instance.objectNode().toString(),
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
    @DisplayName("When handling surveyUnits, handle correctly cache for survey units with campaign")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_surveyUnit_campaign_cache() throws StateDataInvalidDateException {
        String surveyUnitId = "survey-unit-campaign-cache-id";

        // check cache is null at beginning
        assertThatThrownBy(() -> surveyUnitService.getSurveyUnitWithCampaignById(surveyUnitId)).isInstanceOf(EntityNotFoundException.class);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).get(surveyUnitId)).isNull();

        SurveyUnit surveyUnit = new SurveyUnit(surveyUnitId,
                "LOG2021X11Tel",
                "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode().toString(),
                JsonNodeFactory.instance.objectNode().toString(),
                JsonNodeFactory.instance.objectNode().toString(),
                null);
        surveyUnitService.createSurveyUnit(surveyUnit);

        // not retrieving yet so no survey unit in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).get(surveyUnitId)).isNull();

        SurveyUnitSummary expectedSurveyUnitSummary = surveyUnitService.getSurveyUnitWithCampaignById(surveyUnitId);

        // now survey unit is in cache
        SurveyUnitSummary surveyUnitSummary = Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY).get(surveyUnitId, SurveyUnitSummary.class));
        assertThat(surveyUnitSummary).isEqualTo(expectedSurveyUnitSummary);

        surveyUnitService.delete(surveyUnitId);

        // after deletion, no survey unit anymore in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.SURVEY_UNIT_SUMMARY)).get(surveyUnitId)).isNull();
    }
}
