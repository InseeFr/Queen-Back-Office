package fr.insee.queen.infrastructure.db.interrogation.repository.jpa;

import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.infrastructure.db.interrogation.entity.InterrogationDB;
import fr.insee.queen.infrastructure.db.interrogation.projection.InterrogationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository to handle interrogations in DB
 */
@Repository
public interface InterrogationJpaRepository extends JpaRepository<InterrogationDB, String> {

    /**
     * Find summary of interrogation by id
     *
     * @param interrogationId interrogation id
     * @return {@link InterrogationSummary} interrogation summary
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationSummary(
                s.id,
                s.surveyUnitId,
                s.questionnaireModel.id,
                new fr.insee.queen.domain.group.model.GroupSummary(
                    s.group.id,
                    s.group.label)
            )
            from InterrogationDB s where s.id=:interrogationId""")
    Optional<InterrogationSummary> findSummaryById(String interrogationId);

    /**
     * Find personalization of interrogation by id
     *
     * @param interrogationId interrogation id
     * @return {@link InterrogationPersonalization} interrogation summary
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationPersonalization(
                s.id,
                s.group.id,
                s.questionnaireModel.id,
                s.personalization.value
            )
            from InterrogationDB s left join s.personalization where s.id=:interrogationId""")
    InterrogationPersonalization getPersonalizationById(String interrogationId);

    /**
     * Find all interrogation summary by group
     *
     * @param groupId group id
     * @return List of {@link InterrogationSummary} interrogation summary
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationSummary(
                s.id,
                s.surveyUnitId,
                s.questionnaireModel.id,
                new fr.insee.queen.domain.group.model.GroupSummary(
                    s.group.id,
                    s.group.label)
            )
            from InterrogationDB s where s.group.id=:groupId""")
    List<InterrogationSummary> findAllSummaryByGroupId(String groupId);

    /**
     * Find all interrogation summary by interrogation ids
     *
     * @param interrogationIds interrogations we want to retrieve
     * @return List of {@link InterrogationSummary} interrogation summary
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationSummary(
                s.id,
                s.surveyUnitId,
                s.questionnaireModel.id,
                new fr.insee.queen.domain.group.model.GroupSummary(
                    s.group.id,
                    s.group.label)
            )
            from InterrogationDB s where s.id in :interrogationIds""")
    List<InterrogationSummary> findAllSummaryByIdIn(List<String> interrogationIds);

    /**
     * Retrieve an interrogation with all details
     *
     * @param interrogationId interrogation id
     * @return {@link Interrogation} interrogation
     */
    @Query("""
            select new fr.insee.queen.infrastructure.db.interrogation.projection.InterrogationProjection(
                s.id,
                s.surveyUnitId,
                s.group.id,
                s.questionnaireModel.id,
                s.personalization.value,
                s.data.value,
                s.stateData.state,
                s.stateData.date,
                s.stateData.currentPage
            )
            from InterrogationDB s left join s.personalization left join s.data left join s.stateData where s.id=:interrogationId""")
    Optional<InterrogationProjection> findOneById(String interrogationId);

    /**
     * Retrieve all interrogations with all details
     *
     * @return List of {@link Interrogation} interrogations
     */
    @Query("""
            select new fr.insee.queen.infrastructure.db.interrogation.projection.InterrogationProjection(
                s.id,
                s.surveyUnitId,
                s.group.id,
                s.questionnaireModel.id,
                s.personalization.value,
                s.data.value,
                s.stateData.state,
                s.stateData.date,
                s.stateData.currentPage
            )
            from InterrogationDB s left join s.personalization left join s.data left join s.stateData order by s.id asc""")
    List<InterrogationProjection> findAllInterrogations();

    /**
     * Retrieve an interrogation with group and state data linked (used for deposit proof)
     *
     * @param interrogationId interrogation id
     * @return {@link InterrogationDepositProof} interrogation
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationDepositProof(
                s.id,
                s.surveyUnitId,
                new fr.insee.queen.domain.group.model.GroupSummary(
                    s.group.id,
                    s.group.label
                ),
                new fr.insee.queen.domain.interrogation.model.StateData(
                    s.stateData.state,
                    s.stateData.date,
                    s.stateData.currentPage
                )
            )
            from InterrogationDB s where s.id=:interrogationId""")
    Optional<InterrogationDepositProof> findWithGroupAndStateById(String interrogationId);

    /**
     * Find all interrogation ids
     *
     * @return List of interrogation ids
     */
    @Query("select s.id from InterrogationDB s order by s.id asc")
    Optional<List<String>> findAllIds();

    /**
     * Find all interrogations by group when no state
     *
     * @param groupId group id
     * @return List of interrogations by group when no state
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationState(
                s.id,
                s.surveyUnitId,
                s.questionnaireModel.id,
                s.group.id,
                new fr.insee.queen.domain.interrogation.model.StateData(
                    st.state,
                    st.date,
                    st.currentPage
                )
            )
            from InterrogationDB s left join s.stateData st
            where st.state = :stateDataType
            and s.group.id = :groupId""")
    List<InterrogationState> findAllByGroupAndState(String groupId, StateDataType stateDataType);

    /**
     * Find all interrogations by group when no state
     *
     * @param groupId group id
     * @return List of interrogations by group when no state
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationState(
                s.id,
                s.surveyUnitId,
                s.questionnaireModel.id,
                s.group.id,
                null
            )
            from InterrogationDB s left join s.stateData st
            where st is null
            and s.group.id = :groupId""")
    List<InterrogationState> findAllByGroupWithoutState(String groupId);

    /**
     * Search interrogations by ids
     * @param interrogationIds ids to search
     * @return List of {@link Interrogation} interrogations found
     */
    @Query("""
            select new fr.insee.queen.infrastructure.db.interrogation.projection.InterrogationProjection(
                s.id,
                s.surveyUnitId,
                s.group.id,
                s.questionnaireModel.id,
                s.personalization.value,
                s.data.value,
                s.stateData.state,
                s.stateData.date,
                s.stateData.currentPage
            )
            from InterrogationDB s
            left join s.personalization
            left join s.data
            left join s.stateData
            where s.id in :interrogationIds
            order by s.id asc""")
    List<InterrogationProjection> findInterrogationsByIdIn(List<String> interrogationIds);

    /**
     * Find interrogations with state linked by ids
     *
     * @param interrogationIds interrogation ids
     * @return List of {@link InterrogationState} interrogations
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationState(
                s.id,
                s.surveyUnitId,
                s.questionnaireModel.id,
                s.group.id,
                new fr.insee.queen.domain.interrogation.model.StateData(
                    s.stateData.state,
                    s.stateData.date,
                    s.stateData.currentPage
                )
            )
            from InterrogationDB s left join s.stateData where s.id in :interrogationIds""")
    List<InterrogationState> findAllWithStateByIdIn(List<String> interrogationIds);

    @Transactional
    @Modifying
    @Query(value = """
            UPDATE interrogation SET
            survey_unit_id = COALESCE(:surveyUnitId, survey_unit_id),
            survey_group_id = COALESCE(:groupId, survey_group_id),
            questionnaire_model_id = COALESCE(:questionnaireId, questionnaire_model_id)
            WHERE id = :interrogationId
            """, nativeQuery = true)
    void updateFields(
            @Param("interrogationId") String interrogationId,
            @Param("surveyUnitId") String surveyUnitId,
            @Param("groupId") String groupId,
            @Param("questionnaireId") String questionnaireId);

    /**
     * Delete interrogations linked to a group
     *
     * @param groupId group id
     */
    @Transactional
    @Modifying
    @Query("delete from InterrogationDB s where s.group.id=:groupId")
    void deleteInterrogations(String groupId);

    /**
     * Find all interrogation summary by survey-unit
     *
     * @param surveyUnitId survey unit id
     * @return List of {@link InterrogationSummary} interrogation summary
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationSummary(
                s.id,
                s.surveyUnitId,
                s.questionnaireModel.id,
                new fr.insee.queen.domain.group.model.GroupSummary(
                    s.group.id,
                    s.group.label)
            )
            from InterrogationDB s where s.surveyUnitId=:surveyUnitId""")
    List<InterrogationSummary> findAllSummaryBySurveyUnitId(String surveyUnitId);

    /**
     * Check if interrogations exist within the group
     *
     * @param groupId group id
     * @return true if exist, false otherwise
     */
    boolean existsByGroupId(String groupId);
}
