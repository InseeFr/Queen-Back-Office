package fr.insee.queen.domain.group.gateway;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.group.model.Group;
import fr.insee.queen.domain.group.model.GroupSummary;

import java.util.List;
import java.util.Optional;

/**
 * Repository to handle groups
 */
public interface GroupRepository {

    /**
     * Create group
     * @param group group to create
     */
    void create(Group group);

    /**
     * Check if group exists
     *
     * @param groupId group id to check
     * @return true if exists, false otherwise
     */
    boolean exists(String groupId);

    /**
     * Retrieve all groups summary
     *
     * @return List of {@link GroupSummary} groups
     */
    List<GroupSummary> getAllWithQuestionnaireIds();

    /**
     * Delete group
     *
     * @param groupId group id
     */
    void delete(String groupId);

    /**
     * Retrieve group summary
     *
     * @param groupId group id
     * @return {@link GroupSummary} group
     */
    Optional<GroupSummary> findWithQuestionnaireIds(String groupId);

    /**
     * Update group
     *
     * @param group group to update
     */
    void update(Group group);

    /**
     * Retrieve the metadata json value of a group
     *
     * @param groupId group id
     * @return {@link String} json metadata value
     */
    Optional<ObjectNode> findMetadataByGroupId(String groupId);

    /**
     * Retrieve the metadata json value of a group byt the questionnaire id
     *
     * @param questionnaireId questionnaire id
     * @return {@link String} json metadata value
     */
    Optional<ObjectNode> findMetadataByQuestionnaireId(String questionnaireId);

    /**
     * Find group
     *
     * @param groupId group id
     */
    Optional<Group> findGroup(String groupId);

    /**
     * Retrieve all groups ids
     *
     * @return List of {@link String} groups ids
     */
    List<String> getAllGroupIds();
}
