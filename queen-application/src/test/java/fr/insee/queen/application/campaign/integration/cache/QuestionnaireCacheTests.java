package fr.insee.queen.application.campaign.integration.cache;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.application.configuration.ContainerConfiguration;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.JsonTestHelper;
import fr.insee.queen.domain.campaign.model.Campaign;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.QuestionnaireModel;
import fr.insee.queen.domain.campaign.service.CampaignService;
import fr.insee.queen.domain.campaign.service.MetadataService;
import fr.insee.queen.domain.campaign.service.NomenclatureService;
import fr.insee.queen.domain.campaign.service.QuestionnaireModelService;
import fr.insee.queen.domain.common.cache.CacheName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ActiveProfiles("test-cache")
class QuestionnaireCacheTests extends ContainerConfiguration {

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
    void clearCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Test
    @DisplayName("When creating questionnaire, cache is handled")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache01() {
        String questionnaireId = "questionnaire-cache-id";
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId, "label", JsonNodeFactory.instance.objectNode(), Set.of("cities2019", "regions2019")));
    }

    @Test
    @DisplayName("When updating questionnaire, cache is handled")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache02() throws IOException {
        String questionnaireId = "questionnaire-cache-id";
        String campaignId = "campaign-cache-id";

        ObjectNode metadataNode = JsonTestHelper.getResourceFileAsObjectNode("campaign/metadata/metadata.json");
        campaignService.createCampaign(new Campaign(campaignId, "label", CampaignSensitivity.NORMAL, new HashSet<>(), metadataNode));
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId, "label", JsonNodeFactory.instance.objectNode(), Set.of("regions2019")));

        // when updating questionnaire, cache is evicted
        questionnaireModelService.updateQuestionnaire(QuestionnaireModel.createQuestionnaireWithCampaign(questionnaireId, "label2", JsonNodeFactory.instance.objectNode(), Set.of("regions2019", "cities2019"), campaignId));
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId)).isNull();

        ObjectNode questionnaire = questionnaireModelService.getQuestionnaireData(questionnaireId);
        List<String> requiredNomenclatures = nomenclatureService.findRequiredNomenclatureByQuestionnaire(questionnaireId);
        ObjectNode metadata = metadataService.getMetadataByQuestionnaireId(questionnaireId);

        // when retrieving questionnaire, cache does contain the updated questionnaire
        ObjectNode questionnaireCache = Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE).get(questionnaireId, ObjectNode.class));
        @SuppressWarnings("unchecked")
        List<String> requiredNomenclaturesCache = (List<String>) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES).get(questionnaireId).get());
        ObjectNode metadataCache = Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA).get(questionnaireId, ObjectNode.class));

        assertThat(questionnaire).isEqualTo(questionnaireCache);
        assertThat(requiredNomenclatures).isEqualTo(requiredNomenclaturesCache);
        assertThat(metadata).isEqualTo(metadataCache);
    }

    @Test
    @DisplayName("When deleting campaigns, handle cache eviction on associated questionnaires")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache03() {
        String questionnaireId1 = "questionnaire-cache-id1";
        String questionnaireId2 = "questionnaire-cache-id2";

        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId1, "label1", JsonNodeFactory.instance.objectNode(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId2, "label2", JsonNodeFactory.instance.objectNode(), Set.of("cities2019")));

        String campaignId = "campaign-with-questionnaires-cache-id";
        campaignService.createCampaign(new Campaign(campaignId, "label", CampaignSensitivity.NORMAL, Set.of(questionnaireId1, questionnaireId2), JsonNodeFactory.instance.objectNode()));

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
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache04() {
        String questionnaireId1 = "questionnaire-cache-id1";
        String questionnaireId2 = "questionnaire-cache-id2";
        String questionnaireId3 = "questionnaire-cache-id3";
        String questionnaireId4 = "questionnaire-cache-id4";

        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId1, "label1", JsonNodeFactory.instance.objectNode(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId2, "label2", JsonNodeFactory.instance.objectNode(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId3, "label3", JsonNodeFactory.instance.objectNode(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModel.createQuestionnaireWithoutCampaign(questionnaireId4, "label4", JsonNodeFactory.instance.objectNode(), Set.of("regions2019", "cities2019")));

        // create campaign and associate questionnaireId1 & questionnaireId2
        String campaignId = "campaign-with-questionnaires-cache-id";
        Campaign campaign = new Campaign(campaignId, "label", CampaignSensitivity.NORMAL, Set.of(questionnaireId1, questionnaireId2), JsonNodeFactory.instance.objectNode());
        campaignService.createCampaign(campaign);

        campaign = new Campaign(campaignId, "labelUpdated", CampaignSensitivity.NORMAL, Set.of(questionnaireId2), JsonNodeFactory.instance.objectNode());
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
        ObjectNode questionnaire = questionnaireModelService.getQuestionnaireData(questionnaireId);
        List<String> requiredNomenclatures = nomenclatureService.findRequiredNomenclatureByQuestionnaire(questionnaireId);

        // when retrieving questionnaire, cache does contain the questionnaire now
        ObjectNode questionnaireCache = Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE).get(questionnaireId, ObjectNode.class));
        @SuppressWarnings("unchecked")
        List<String> requiredNomenclaturesCache = (List<String>) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES).get(questionnaireId).get());

        assertThat(questionnaire).isEqualTo(questionnaireCache);
        assertThat(requiredNomenclatures).isEqualTo(requiredNomenclaturesCache);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_METADATA)).get(questionnaireId)).isNull();
    }
}
