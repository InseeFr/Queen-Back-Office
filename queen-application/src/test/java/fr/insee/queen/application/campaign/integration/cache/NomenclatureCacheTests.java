package fr.insee.queen.application.campaign.integration.cache;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.domain.campaign.model.Nomenclature;
import fr.insee.queen.domain.campaign.service.NomenclatureService;
import fr.insee.queen.domain.common.cache.CacheName;
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
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cache-testing")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase
@AutoConfigureMockMvc
class NomenclatureCacheTests {

    @Autowired
    private NomenclatureService nomenclatureService;

    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    public void clearCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Test
    @DisplayName("When saving nomenclature, evict the associated nomenclature in nomenclature cache")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_nomenclature_cache() {
        String nomenclatureId = "nomenclature-cache-id";

        // create nomenclature
        nomenclatureService.saveNomenclature(new Nomenclature(nomenclatureId, "label", JsonNodeFactory.instance.arrayNode().toString()));
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.NOMENCLATURE)).get(nomenclatureId)).isNull();

        // when retrieving nomenclature, cache is created
        Nomenclature nomenclature = nomenclatureService.getNomenclature(nomenclatureId);
        Nomenclature nomenclatureCache = Objects.requireNonNull(cacheManager.getCache(CacheName.NOMENCLATURE)).get(nomenclatureId, Nomenclature.class);
        assertThat(nomenclature).isEqualTo(nomenclatureCache);

        // when updating nomenclature, cache is evicted
        nomenclatureService.saveNomenclature(new Nomenclature(nomenclatureId, "label2", JsonNodeFactory.instance.arrayNode().toString()));
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.NOMENCLATURE)).get(nomenclatureId)).isNull();

        // when retrieving nomenclature, cache is created
        nomenclature = nomenclatureService.getNomenclature(nomenclatureId);
        nomenclatureCache = Objects.requireNonNull(cacheManager.getCache(CacheName.NOMENCLATURE)).get(nomenclatureId, Nomenclature.class);
        assertThat(nomenclature).isEqualTo(nomenclatureCache);
    }
}
