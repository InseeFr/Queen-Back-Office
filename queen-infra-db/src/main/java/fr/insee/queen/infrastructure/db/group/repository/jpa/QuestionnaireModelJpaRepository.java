package fr.insee.queen.infrastructure.db.group.repository.jpa;

import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.infrastructure.db.group.entity.QuestionnaireModelDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * JPA repositiory to handle questionnaires
 */
@Repository
public interface QuestionnaireModelJpaRepository extends JpaRepository<QuestionnaireModelDB, String> {

    /**
     * Find ids for all questionnaire linked to a group
     *
     * @param groupId group id
     * @return all questionnaire ids for a group
     */
    @Query("select qm.id from GroupDB g join g.questionnaireModels qm where g.id = :groupId")
    List<String> findAllIdByGroupId(String groupId);

    /**
     * Find data structure for all questionnaire linked to a group
     *
     * @param groupId group id
     * @return all questionnaire values for a group
     */
    @Query("select qm.value from GroupDB g join g.questionnaireModels qm where g.id = :groupId")
    List<ObjectNode> findAllValueByGroupId(String groupId);

    /**
     * Find data structure for a questionnaire
     *
     * @param questionnaireId questionnaire id
     * @return questionnaire data for a group
     */
    @Query(value = "select qm.value from QuestionnaireModelDB qm where qm.id=:questionnaireId")
    Optional<ObjectNode> findQuestionnaireData(String questionnaireId);

    /**
     * Count how many of the given questionnaire ids exist in database
     *
     * @param questionnaireIds questionnaire ids to check
     * @return number of existing questionnaires
     */
    Long countByIdIn(Set<String> questionnaireIds);

    /**
     * Find questionnaires by ids
     *
     * @param questionnaireIds questionnaire ids
     * @return {@link QuestionnaireModelDB}
     */
    Set<QuestionnaireModelDB> findByIdIn(Set<String> questionnaireIds);

    /**
     * Find questionnaire ids that are no longer linked to any group
     *
     * @param questionnaireIds candidate questionnaire ids to check
     * @return ids not present in survey_group_questionnaire_model
     */
    @NativeQuery("""
            select qm.id from questionnaire_model qm
            where qm.id in :questionnaireIds
            and not exists (
                select 1 from survey_group_questionnaire_model sgqm
                where sgqm.questionnaire_model_id = qm.id
            )
            """)
    List<String> findOrphanedIds(Set<String> questionnaireIds);

}
