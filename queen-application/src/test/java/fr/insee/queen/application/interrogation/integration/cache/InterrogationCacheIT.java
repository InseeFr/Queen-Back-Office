package fr.insee.queen.application.interrogation.integration.cache;

import tools.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.domain.group.service.GroupApiService;
import fr.insee.queen.domain.common.cache.CacheName;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.interrogation.model.Interrogation;
import fr.insee.queen.domain.interrogation.model.InterrogationSummary;
import fr.insee.queen.domain.interrogation.service.InterrogationApiService;
import fr.insee.queen.domain.interrogation.service.exception.StateDataInvalidDateException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(properties = {"feature.cache.enabled=true"})
@ActiveProfiles("test")
class InterrogationCacheIT {

    @Autowired
    private InterrogationApiService interrogationService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private GroupApiService groupService;

    @AfterEach
    void clearCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Test
    @DisplayName("When creating/updating interrogations, handle correctly cache for interrogation existence")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_interrogation_existence_cache_01() throws StateDataInvalidDateException {
        String interrogationId = "interrogation-cache-id";
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST)).get(interrogationId)).isNull();
        interrogationService.existsById(interrogationId);
        Boolean interrogationExist =  Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST).get(interrogationId, Boolean.class));
        assertThat(interrogationExist).isFalse();

        Interrogation interrogation = new Interrogation(interrogationId,
                "survey-unit-id",
                "LOG2021X11Tel",
                "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                null,
                null);
        interrogationService.createInterrogation(interrogation);

        // not retrieving yet so no interrogation in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST)).get(interrogationId)).isNull();

        interrogationService.existsById(interrogationId);

        // now interrogation existence is in cache
        interrogationExist = Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST).get(interrogationId, Boolean.class));
        assertThat(interrogationExist).isTrue();

        interrogationService.delete(interrogationId);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST)).get(interrogationId)).isNull();
    }

    @Test
    @DisplayName("When creating interrogation and delete the related group, then the cache for interrogation existence is deleted")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_interrogation_existence_cache_02() throws StateDataInvalidDateException {
        String interrogationId = "interrogation-cache-id";
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST)).get(interrogationId)).isNull();
        interrogationService.existsById(interrogationId);
        Boolean interrogationExist =  Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST).get(interrogationId, Boolean.class));
        assertThat(interrogationExist).isFalse();

        String groupId = "LOG2021X11Tel";
        Interrogation interrogation = new Interrogation(interrogationId,
                "survey-unit-id",
                groupId,
                "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                null,
                null);
        interrogationService.createInterrogation(interrogation);

        // not retrieving yet so no interrogation in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST)).get(interrogationId)).isNull();

        interrogationService.existsById(interrogationId);

        // now interrogation existence is in cache
        interrogationExist = Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST).get(interrogationId, Boolean.class));
        assertThat(interrogationExist).isTrue();

        groupService.delete(groupId, true);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_EXIST)).get(interrogationId)).isNull();
    }

    @Test
    @DisplayName("When handling interrogations, handle correctly cache for interrogations with group")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_interrogation_group_cache_01() throws StateDataInvalidDateException {
        String interrogationId = "interrogation-group-cache-id";

        // check cache is null at beginning
        assertThatThrownBy(() -> interrogationService.getSummaryById(interrogationId)).isInstanceOf(EntityNotFoundException.class);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_SUMMARY)).get(interrogationId)).isNull();

        Interrogation interrogation = new Interrogation(interrogationId,
                "survey-unit-id",
                "LOG2021X11Tel",
                "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                null,
                null);
        interrogationService.createInterrogation(interrogation);

        // not retrieving yet so no interrogation in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_SUMMARY)).get(interrogationId)).isNull();

        InterrogationSummary expectedInterrogationSummary = interrogationService.getSummaryById(interrogationId);

        // now interrogation is in cache
        InterrogationSummary interrogationSummary = Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_SUMMARY).get(interrogationId, InterrogationSummary.class));
        assertThat(interrogationSummary).isEqualTo(expectedInterrogationSummary);

        interrogationService.delete(interrogationId);

        // after deletion, no interrogation anymore in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_SUMMARY)).get(interrogationId)).isNull();
    }

    @Test
    @DisplayName("When creating interrogation and delete the related group, then the cache for interrogation summary is deleted")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_interrogation_group_cache_02() throws StateDataInvalidDateException {
        String interrogationId = "interrogation-group-cache-id";
        String groupId = "LOG2021X11Tel";
        // check cache is null at beginning
        assertThatThrownBy(() -> interrogationService.getSummaryById(interrogationId)).isInstanceOf(EntityNotFoundException.class);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_SUMMARY)).get(interrogationId)).isNull();

        Interrogation interrogation = new Interrogation(interrogationId,
                "survey-unit-id",
                groupId,
                "LOG2021X11Tel",
                JsonNodeFactory.instance.arrayNode(),
                JsonNodeFactory.instance.objectNode(),
                null,
                null);
        interrogationService.createInterrogation(interrogation);

        // not retrieving yet so no interrogation in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_SUMMARY)).get(interrogationId)).isNull();

        InterrogationSummary expectedInterrogationSummary = interrogationService.getSummaryById(interrogationId);

        // now interrogation is in cache
        InterrogationSummary interrogationSummary = Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_SUMMARY).get(interrogationId, InterrogationSummary.class));
        assertThat(interrogationSummary).isEqualTo(expectedInterrogationSummary);

        groupService.delete(groupId, true);

        // after deletion, no interrogation anymore in cache
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.INTERROGATION_SUMMARY)).get(interrogationId)).isNull();
    }
}
