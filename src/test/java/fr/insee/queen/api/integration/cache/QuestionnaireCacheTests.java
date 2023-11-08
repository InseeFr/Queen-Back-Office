package fr.insee.queen.api.integration.cache;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.api.configuration.cache.CacheName;
import fr.insee.queen.api.dto.campaign.CampaignData;
import fr.insee.queen.api.dto.metadata.MetadataDto;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelData;
import fr.insee.queen.api.dto.questionnairemodel.QuestionnaireModelValueDto;
import fr.insee.queen.api.service.campaign.CampaignService;
import fr.insee.queen.api.service.campaign.MetadataService;
import fr.insee.queen.api.service.questionnaire.NomenclatureService;
import fr.insee.queen.api.service.questionnaire.QuestionnaireModelService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cache-testing")
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
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
        for(String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Test
    @Order(0)
    @DisplayName("When creating questionnaire, handle correctly cache")
    void check_questionnaire_cache01() throws Exception {
        String questionnaireId = "questionnaire-cache-id";
        check_questionnaire_cache_on_creation(QuestionnaireModelData.createQuestionnaireWithoutCampaign(questionnaireId, "label", JsonNodeFactory.instance.objectNode().toString(), Set.of("cities2019", "regions2019")));
    }

    @Test
    @Order(1)
    @DisplayName("When updating questionnaire, handle correctly cache")
    void check_questionnaire_cache02() throws Exception {
        String questionnaireId = "questionnaire-cache-id";
        String campaignId = "campaign-cache-id";

        campaignService.createCampaign(new CampaignData(campaignId, "label", new HashSet<>(), JsonNodeFactory.instance.objectNode().toString()));
        check_questionnaire_cache_on_creation(QuestionnaireModelData.createQuestionnaireWithoutCampaign(questionnaireId, "label", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019")));

        // when updating questionnaire, cache is evicted
        questionnaireModelService.updateQuestionnaire(QuestionnaireModelData.createQuestionnaireWithCampaign(questionnaireId, "label2", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019"), campaignId));
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId)).isNull();

        QuestionnaireModelValueDto questionnaire = questionnaireModelService.getQuestionnaireModelDto(questionnaireId);
        List<String> requiredNomenclatures = nomenclatureService.findRequiredNomenclatureByQuestionnaire(questionnaireId);
        MetadataDto metadata = metadataService.getMetadataByQuestionnaireId(questionnaireId);

        // when retrieving questionnaire, cache does contain the updated questionnaire
        QuestionnaireModelValueDto questionnaireCache = (QuestionnaireModelValueDto) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE).get(questionnaireId).get());
        List<String> requiredNomenclaturesCache = (List<String>) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES).get(questionnaireId).get());
        MetadataDto metadataCache = (MetadataDto) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA).get(questionnaireId).get());


        assertThat(questionnaire).isEqualTo(questionnaireCache);
        assertThat(requiredNomenclatures).isEqualTo(requiredNomenclaturesCache);
        assertThat(metadata).isEqualTo(metadataCache);
    }

    @Test
    @Order(2)
    @DisplayName("When deleting campaigns, handle cache eviction on associated questionnaires")
    void check_questionnaire_cache03() throws Exception {
        String questionnaireId1 = "questionnaire-cache-id1";
        String questionnaireId2 = "questionnaire-cache-id2";

        check_questionnaire_cache_on_creation(QuestionnaireModelData.createQuestionnaireWithoutCampaign(questionnaireId1, "label1", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModelData.createQuestionnaireWithoutCampaign(questionnaireId2, "label2", JsonNodeFactory.instance.objectNode().toString(), Set.of("cities2019")));

        String campaignId = "campaign-with-questionnaires-cache-id";
        campaignService.createCampaign(new CampaignData(campaignId, "label", Set.of(questionnaireId1, questionnaireId2), JsonNodeFactory.instance.objectNode().toString()));

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
    @Order(3)
    @DisplayName("When updating campaign, handle cache eviction on all questionnaire metadatas")
    void check_questionnaire_cache04() throws Exception {
        String questionnaireId1 = "questionnaire-cache-id1";
        String questionnaireId2 = "questionnaire-cache-id2";
        String questionnaireId3 = "questionnaire-cache-id3";
        String questionnaireId4 = "questionnaire-cache-id4";

        check_questionnaire_cache_on_creation(QuestionnaireModelData.createQuestionnaireWithoutCampaign(questionnaireId1, "label1", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModelData.createQuestionnaireWithoutCampaign(questionnaireId2, "label2", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModelData.createQuestionnaireWithoutCampaign(questionnaireId3, "label3", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModelData.createQuestionnaireWithoutCampaign(questionnaireId4, "label4", JsonNodeFactory.instance.objectNode().toString(), Set.of("regions2019", "cities2019")));

        // create campaign and associate questionnaireId1 & questionnaireId2
        String campaignId = "campaign-with-questionnaires-cache-id";
        CampaignData campaign = new CampaignData(campaignId, "label", Set.of(questionnaireId1, questionnaireId2), JsonNodeFactory.instance.objectNode().toString());
        campaignService.createCampaign(campaign);

        campaign = new CampaignData(campaignId, "labelUpdated", Set.of(questionnaireId2), JsonNodeFactory.instance.objectNode().toString());
        // when deleting campaign, associated questionnaires are evicted from cache
        campaignService.updateCampaign(campaign);

        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId1)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId2)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId3)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId4)).isNull();
    }

    void check_questionnaire_cache_on_creation(QuestionnaireModelData questionnaireData) throws Exception {
        String questionnaireId = questionnaireData.id();

        // before creating questionnaire, cache does not contain the questionnaire
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId)).isNull();

        questionnaireModelService.createQuestionnaire(questionnaireData);
        QuestionnaireModelValueDto questionnaire = questionnaireModelService.getQuestionnaireModelDto(questionnaireId);
        List<String> requiredNomenclatures = nomenclatureService.findRequiredNomenclatureByQuestionnaire(questionnaireId);

        // when retrieving questionnaire, cache does contain the questionnaire now
        QuestionnaireModelValueDto questionnaireCache = (QuestionnaireModelValueDto) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE).get(questionnaireId).get());
        List<String> requiredNomenclaturesCache = (List<String>) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES).get(questionnaireId).get());

        assertThat(questionnaire).isEqualTo(questionnaireCache);
        assertThat(requiredNomenclatures).isEqualTo(requiredNomenclaturesCache);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId)).isNull();
    }
}
