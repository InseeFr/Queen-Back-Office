package fr.insee.queen.api.campaign.integration.cache;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.api.campaign.service.CampaignService;
import fr.insee.queen.api.campaign.service.MetadataService;
import fr.insee.queen.api.campaign.service.NomenclatureService;
import fr.insee.queen.api.campaign.service.QuestionnaireModelService;
import fr.insee.queen.api.campaign.service.model.Campaign;
import fr.insee.queen.api.campaign.service.model.QuestionnaireModel;
import fr.insee.queen.api.configuration.Constants;
import fr.insee.queen.api.configuration.cache.CacheName;
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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cache-testing")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase
@AutoConfigureMockMvc
class QuestionnaireCacheTests {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private QuestionnaireModelService questionnaireModelService;

    @Autowired
    private NomenclatureService nomenclatureService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    public void clearCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Test
    @DisplayName("When creating questionnaire, cache is handled")
    @Sql(value = Constants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache01() {
        String questionnaireId = "questionnaire-cache-id";
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId, "label", JsonNodeFactory.instance.objectNode().toString(), Set.of("cities2019", "regions2019")));
    }

    @Test
    @DisplayName("When updating questionnaire, cache is handled")
    @Sql(value = Constants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache02() {
        String questionnaireId = "questionnaire-cache-id";
        String campaignId = "campaign-cache-id";

        campaignService.createCampaign(new Campaign(campaignId, "label", new HashSet<>(), JsonNodeFactory.instance.objectNode().toString()));
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId, "label", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019")));

        // when updating questionnaire, cache is evicted
        questionnaireModelService.updateQuestionnaire(QuestionnaireModel.createQuestionnaireWithCampaign(questionnaireId, "label2", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019"), campaignId));
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId)).isNull();

        String questionnaire = questionnaireModelService.getQuestionnaireData(questionnaireId);
        List<String> requiredNomenclatures = nomenclatureService.findRequiredNomenclatureByQuestionnaire(questionnaireId);
        String metadata = metadataService.getMetadataByQuestionnaireId(questionnaireId);

        // when retrieving questionnaire, cache does contain the updated questionnaire
        String questionnaireCache = Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE).get(questionnaireId, String.class));
        @SuppressWarnings("unchecked")
        List<String> requiredNomenclaturesCache = (List<String>) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES).get(questionnaireId).get());
        String metadataCache = Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA).get(questionnaireId, String.class));


        assertThat(questionnaire).isEqualTo(questionnaireCache);
        assertThat(requiredNomenclatures).isEqualTo(requiredNomenclaturesCache);
        assertThat(metadata).isEqualTo(metadataCache);
    }

    @Test
    @DisplayName("When deleting campaigns, handle cache eviction on associated questionnaires")
    @Sql(value = Constants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache03() {
        String questionnaireId1 = "questionnaire-cache-id1";
        String questionnaireId2 = "questionnaire-cache-id2";

        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId1, "label1", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId2, "label2", JsonNodeFactory.instance.objectNode().toString(), Set.of("cities2019")));

        String campaignId = "campaign-with-questionnaires-cache-id";
        campaignService.createCampaign(new Campaign(campaignId, "label", Set.of(questionnaireId1, questionnaireId2), JsonNodeFactory.instance.objectNode().toString()));

        // when deleting campaign, associated questionnaires are evicted from cache
        campaignService.delete(campaignId);

        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId1)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId1)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId1)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId2)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId2)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId2)).isNull();
    }

    @Test
    @DisplayName("When updating campaign, handle cache eviction on all questionnaire metadatas")
    @Sql(value = Constants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache04() {
        String questionnaireId1 = "questionnaire-cache-id1";
        String questionnaireId2 = "questionnaire-cache-id2";
        String questionnaireId3 = "questionnaire-cache-id3";
        String questionnaireId4 = "questionnaire-cache-id4";

        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId1, "label1", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId2, "label2", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId3, "label3", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId4, "label4", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019")));

        // create campaign and associate questionnaireId1 & questionnaireId2
        String campaignId = "campaign-with-questionnaires-cache-id";
        Campaign campaign = new Campaign(campaignId, "label", Set.of(questionnaireId1, questionnaireId2), JsonNodeFactory.instance.objectNode().toString());
        campaignService.createCampaign(campaign);

        campaign = new Campaign(campaignId, "labelUpdated", Set.of(questionnaireId2), JsonNodeFactory.instance.objectNode().toString());
        // when deleting campaign, associated questionnaires are evicted from cache
        campaignService.updateCampaign(campaign);

        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId1)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId2)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId3)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId4)).isNull();
    }

    void check_questionnaire_cache_on_creation(QuestionnaireModel questionnaireData) {
        String questionnaireId = questionnaireData.getId();

        // before creating questionnaire, cache does not contain the questionnaire
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId)).isNull();

        questionnaireModelService.createQuestionnaire(questionnaireData);
        String questionnaire = questionnaireModelService.getQuestionnaireData(questionnaireId);
        List<String> requiredNomenclatures = nomenclatureService.findRequiredNomenclatureByQuestionnaire(questionnaireId);

        // when retrieving questionnaire, cache does contain the questionnaire now
        String questionnaireCache = Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE).get(questionnaireId, String.class));
        @SuppressWarnings("unchecked")
        List<String> requiredNomenclaturesCache = (List<String>) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES).get(questionnaireId).get());

        assertThat(questionnaire).isEqualTo(questionnaireCache);
        assertThat(requiredNomenclatures).isEqualTo(requiredNomenclaturesCache);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId)).isNull();
    }
}
