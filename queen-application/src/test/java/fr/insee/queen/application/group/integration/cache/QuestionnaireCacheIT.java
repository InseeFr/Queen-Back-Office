package fr.insee.queen.application.group.integration.cache;

import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.application.utils.JsonTestHelper;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.model.QuestionnaireModel;
import fr.insee.queen.domain.group.service.GroupService;
import fr.insee.queen.domain.group.service.MetadataService;
import fr.insee.queen.domain.group.service.NomenclatureService;
import fr.insee.queen.domain.group.service.QuestionnaireModelService;
import fr.insee.queen.domain.common.cache.CacheName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(properties = {"feature.cache.enabled=true"})
@ActiveProfiles("test")
class QuestionnaireCacheIT {

    @Autowired
    private GroupService groupService;

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
        check_questionnaire_cache_on_creation(QuestionnaireModel.create(questionnaireId, "label", JsonNodeFactory.instance.objectNode(), Set.of("cities2019", "regions2019")));
    }

    @Test
    @DisplayName("When updating questionnaire, cache is handled")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache02() {
        String questionnaireId = "questionnaire-cache-id";
        String groupId = "group-cache-id";

        ObjectNode metadataNode = JsonTestHelper.getResourceFileAsObjectNode("group/metadata/metadata.json");
        groupService.createGroup(new Group(groupId, "label",  new HashSet<>(), metadataNode));
        check_questionnaire_cache_on_creation(QuestionnaireModel.create(questionnaireId, "label", JsonNodeFactory.instance.objectNode(), Set.of("regions2019")));

        // attach the questionnaire to the group via the N:M join (owned by Group)
        groupService.updateGroup(new Group(groupId, "label", Set.of(questionnaireId), metadataNode));

        // when updating questionnaire, questionnaire caches are evicted (metadata now belongs to the group, not the questionnaire)
        questionnaireModelService.updateQuestionnaire(QuestionnaireModel.create(questionnaireId, "label2", JsonNodeFactory.instance.objectNode(), Set.of("regions2019", "cities2019")));
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_METADATA)).get(groupId)).isNull();

        ObjectNode questionnaire = questionnaireModelService.getQuestionnaireData(questionnaireId);
        List<String> requiredNomenclatures = nomenclatureService.findRequiredNomenclatureByQuestionnaire(questionnaireId);
        ObjectNode metadata = metadataService.getMetadata(groupId);

        // when retrieving questionnaire and metadata, caches contain the updated values (keyed by questionnaireId / groupId respectively)
        ObjectNode questionnaireCache = Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE).get(questionnaireId, ObjectNode.class));
        @SuppressWarnings("unchecked")
        List<String> requiredNomenclaturesCache = (List<String>) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES).get(questionnaireId).get());
        ObjectNode metadataCache = Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_METADATA).get(groupId, ObjectNode.class));

        assertThat(questionnaire).isEqualTo(questionnaireCache);
        assertThat(requiredNomenclatures).isEqualTo(requiredNomenclaturesCache);
        assertThat(metadata).isEqualTo(metadataCache);
    }

    @Test
    @DisplayName("When deleting groups, handle cache eviction on associated questionnaires and on group metadata")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache03() {
        String questionnaireId1 = "questionnaire-cache-id1";
        String questionnaireId2 = "questionnaire-cache-id2";
        String groupId = "group-with-questionnaires-cache-id";

        ObjectNode metadataNode = JsonTestHelper.getResourceFileAsObjectNode("group/metadata/metadata.json");
        check_questionnaire_cache_on_creation(QuestionnaireModel.create(questionnaireId1, "label1", JsonNodeFactory.instance.objectNode(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModel.create(questionnaireId2, "label2", JsonNodeFactory.instance.objectNode(), Set.of("cities2019")));

        groupService.createGroup(new Group(groupId, "label",  Set.of(questionnaireId1, questionnaireId2), metadataNode));

        // populate GROUP_METADATA cache
        metadataService.getMetadata(groupId);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_METADATA)).get(groupId)).isNotNull();

        // when deleting group, associated questionnaires and group metadata are evicted from cache
        groupService.delete(groupId, true);

        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId1)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId1)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId2)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId2)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_METADATA)).get(groupId)).isNull();
    }

    @Test
    @DisplayName("When updating group, group metadata cache entry is evicted")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_questionnaire_cache04() {
        String questionnaireId1 = "questionnaire-cache-id1";
        String questionnaireId2 = "questionnaire-cache-id2";
        String groupId = "group-with-questionnaires-cache-id";
        String otherGroupId = "other-group-cache-id";

        ObjectNode metadataNode = JsonTestHelper.getResourceFileAsObjectNode("group/metadata/metadata.json");
        check_questionnaire_cache_on_creation(QuestionnaireModel.create(questionnaireId1, "label1", JsonNodeFactory.instance.objectNode(), Set.of("regions2019", "cities2019")));
        check_questionnaire_cache_on_creation(QuestionnaireModel.create(questionnaireId2, "label2", JsonNodeFactory.instance.objectNode(), Set.of("regions2019", "cities2019")));

        Group group = new Group(groupId, "label",  Set.of(questionnaireId1, questionnaireId2), metadataNode);
        groupService.createGroup(group);
        Group otherGroup = new Group(otherGroupId, "otherLabel", Set.of(questionnaireId1), metadataNode);
        groupService.createGroup(otherGroup);

        // populate GROUP_METADATA cache for both groups
        metadataService.getMetadata(groupId);
        metadataService.getMetadata(otherGroupId);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_METADATA)).get(groupId)).isNotNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_METADATA)).get(otherGroupId)).isNotNull();

        // when updating group, only its own GROUP_METADATA entry is evicted (targeted key eviction)
        groupService.updateGroup(new Group(groupId, "labelUpdated", Set.of(questionnaireId2), metadataNode));

        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_METADATA)).get(groupId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_METADATA)).get(otherGroupId)).isNotNull();
    }

    void check_questionnaire_cache_on_creation(QuestionnaireModel questionnaireData) {
        String questionnaireId = questionnaireData.getId();

        // before creating questionnaire, cache does not contain the questionnaire
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE)).get(questionnaireId)).isNull();
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES)).get(questionnaireId)).isNull();

        questionnaireModelService.createQuestionnaire(questionnaireData);
        ObjectNode questionnaire = questionnaireModelService.getQuestionnaireData(questionnaireId);
        List<String> requiredNomenclatures = nomenclatureService.findRequiredNomenclatureByQuestionnaire(questionnaireId);

        // when retrieving questionnaire, cache does contain the questionnaire now
        ObjectNode questionnaireCache = Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE).get(questionnaireId, ObjectNode.class));
        @SuppressWarnings("unchecked")
        List<String> requiredNomenclaturesCache = (List<String>) Objects.requireNonNull(cacheManager.getCache(CacheName.QUESTIONNAIRE_NOMENCLATURES).get(questionnaireId).get());

        assertThat(questionnaire).isEqualTo(questionnaireCache);
        assertThat(requiredNomenclatures).isEqualTo(requiredNomenclaturesCache);
    }
}
