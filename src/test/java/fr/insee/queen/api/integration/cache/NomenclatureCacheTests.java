package fr.insee.queen.api.integration.cache;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.input.NomenclatureInputDto;
import fr.insee.queen.api.dto.nomenclature.NomenclatureDto;
import fr.insee.queen.api.service.questionnaire.NomenclatureService;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cache-testing")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class NomenclatureCacheTests {

    @Autowired
    private NomenclatureService nomenclatureService;

    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    public void clearCaches() {
        for(String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Test
    @DisplayName("When saving nomenclature, evict the associated nomenclature in nomenclature cache")
    void check_nomenclature_cache() throws Exception {
        String nomenclatureId = "nomenclature-cache-id";

        // create nomenclature
        nomenclatureService.saveNomenclature(new NomenclatureInputDto(nomenclatureId, "label", JsonNodeFactory.instance.arrayNode()));
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.NOMENCLATURE)).get(nomenclatureId)).isNull();

        // when retrieving nomenclature, cache is created
        NomenclatureDto nomenclature = nomenclatureService.getNomenclature(nomenclatureId);
        NomenclatureDto nomenclatureCache = (NomenclatureDto) Objects.requireNonNull(cacheManager.getCache(CacheName.NOMENCLATURE)).get(nomenclatureId).get();
        assertThat(nomenclature).isEqualTo(nomenclatureCache);

        // when updating nomenclature, cache is evicted
        nomenclatureService.saveNomenclature(new NomenclatureInputDto(nomenclatureId, "label2", JsonNodeFactory.instance.arrayNode()));
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.NOMENCLATURE)).get(nomenclatureId)).isNull();

        // when retrieving nomenclature, cache is created
        nomenclature = nomenclatureService.getNomenclature(nomenclatureId);
        nomenclatureCache = (NomenclatureDto) Objects.requireNonNull(cacheManager.getCache(CacheName.NOMENCLATURE)).get(nomenclatureId).get();
        assertThat(nomenclature).isEqualTo(nomenclatureCache);
    }
}
