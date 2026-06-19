package fr.insee.queen.application.group.integration.cache;

import tools.jackson.databind.node.JsonNodeFactory;
import fr.insee.queen.application.configuration.ScriptConstants;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.service.GroupExistenceService;
import fr.insee.queen.domain.group.service.GroupService;
import fr.insee.queen.domain.common.cache.CacheName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(properties = {"feature.cache.enabled=true"})
@ActiveProfiles("test")
class GroupCacheIT {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupExistenceService groupExistenceService;

    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    void clearCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Test
    @DisplayName("When handling groups, handle correctly cache for group existence")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void check_group_existence_cache() {
        String groupId = "group-cache-id";
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_EXIST)).get(groupId)).isNull();

        groupExistenceService.existsById(groupId);
        Boolean groupExist = Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_EXIST).get(groupId, Boolean.class));
        assertThat(groupExist).isFalse();

        groupService.createGroup(new Group(groupId, "label",  new HashSet<>(), JsonNodeFactory.instance.objectNode()));
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_EXIST)).get(groupId)).isNull();

        groupExistenceService.existsById(groupId);
        groupExist =Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_EXIST).get(groupId, Boolean.class));
        assertThat(groupExist).isTrue();

        groupService.delete(groupId, true);
        assertThat(Objects.requireNonNull(cacheManager.getCache(CacheName.GROUP_EXIST)).get(groupId)).isNull();
    }
}
