package fr.insee.queen.infrastructure.db.interrogation.repository.jpa;

import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.infrastructure.db.interrogation.entity.InterrogationDB;
import fr.insee.queen.infrastructure.db.interrogation.projection.InterrogationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
                new fr.insee.queen.domain.campaign.model.CampaignSummary(
                    s.campaign.id,
                    s.campaign.label,
                    s.campaign.sensitivity)
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
                s.questionnaireModel.id,
                s.personalization.value
            )
            from InterrogationDB s left join s.personalization where s.id=:interrogationId""")
    InterrogationPersonalization getPersonalizationById(String interrogationId);

    /**
     * Find all interrogation summary by campaign
     *
     * @param campaignId campaign id
     * @return List of {@link InterrogationSummary} interrogation summary
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationSummary(
                s.id,
                s.surveyUnitId,
                s.questionnaireModel.id,
                new fr.insee.queen.domain.campaign.model.CampaignSummary(
                    s.campaign.id,
                    s.campaign.label,
                    s.campaign.sensitivity)
            )
            from InterrogationDB s where s.campaign.id=:campaignId""")
    List<InterrogationSummary> findAllSummaryByCampaignId(String campaignId);

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
                new fr.insee.queen.domain.campaign.model.CampaignSummary(
                    s.campaign.id,
                    s.campaign.label,
                    s.campaign.sensitivity)
            )
            from InterrogationDB s where s.id in :interrogationIds""")
    List<InterrogationSummary> findAllSummaryByIdIn(List<String> interrogationIds);

    /**
     * Retrieve a interrogation with all details
     *
     * @param interrogationId interrogation id
     * @return {@link Interrogation} interrogation
     */
    @Query("""
            select new fr.insee.queen.infrastructure.db.interrogation.projection.InterrogationProjection(
                s.id,
                s.surveyUnitId,
                s.campaign.id,
                s.questionnaireModel.id,
                s.personalization.value,
                s.data.value,
                s.comment.value,
                s.stateData.state,
                s.stateData.date,
                s.stateData.currentPage
            )
            from InterrogationDB s left join s.personalization left join s.data left join s.comment left join s.stateData where s.id=:interrogationId""")
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
                s.campaign.id,
                s.questionnaireModel.id,
                s.personalization.value,
                s.data.value,
                s.comment.value,
                s.stateData.state,
                s.stateData.date,
                s.stateData.currentPage
            )
            from InterrogationDB s left join s.personalization left join s.data left join s.comment left join s.stateData order by s.id asc""")
    List<InterrogationProjection> findAllInterrogations();

    /**
     * Retrieve a interrogation with campaign and state data linked (used for deposit proof)
     *
     * @param interrogationId interrogation id
     * @return {@link InterrogationDepositProof} interrogation
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationDepositProof(
                s.id,
                new fr.insee.queen.domain.campaign.model.CampaignSummary(
                    s.campaign.id,
                    s.campaign.label,
                    s.campaign.sensitivity
                ),
                new fr.insee.queen.domain.interrogation.model.StateData(
                    s.stateData.state,
                    s.stateData.date,
                    s.stateData.currentPage
                )
            )
            from InterrogationDB s where s.id=:interrogationId""")
    Optional<InterrogationDepositProof> findWithCampaignAndStateById(String interrogationId);

    /**
     * Find all interrogation ids
     *
     * @return List of interrogation ids
     */
    @Query("select s.id from InterrogationDB s order by s.id asc")
    Optional<List<String>> findAllIds();

    /**
     * Find all interrogations by state
     *
     * @param campaignId campaign id
     * @param stateDataType state data used for filtering
     * @return List of interrogations by state
     */
    @Query("""
            select new fr.insee.queen.domain.interrogation.model.InterrogationState(
                s.id,
                s.surveyUnitId,
                s.questionnaireModel.id,
                s.campaign.id,
                new fr.insee.queen.domain.interrogation.model.StateData(
                    st.state,
                    st.date,
                    st.currentPage
                )
            )
            from InterrogationDB s left join s.stateData st
            where st.state = :stateDataType
            and s.campaign.id = :campaignId""")
    List<InterrogationState> findAllByState(String campaignId, StateDataType stateDataType);

    /**
     * Search interrogations by ids
     * @param interrogationIds ids to search
     * @return List of {@link Interrogation} interrogations found
     */
    @Query("""
            select new fr.insee.queen.infrastructure.db.interrogation.projection.InterrogationProjection(
                s.id,
                s.surveyUnitId,
                s.campaign.id,
                s.questionnaireModel.id,
                s.personalization.value,
                s.data.value,
                s.comment.value,
                s.stateData.state,
                s.stateData.date,
                s.stateData.currentPage
            )
            from InterrogationDB s
            left join s.personalization
            left join s.data
            left join s.comment
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
                s.campaign.id,
                new fr.insee.queen.domain.interrogation.model.StateData(
                    s.stateData.state,
                    s.stateData.date,
                    s.stateData.currentPage
                )
            )
            from InterrogationDB s left join s.stateData where s.id in :interrogationIds""")
    List<InterrogationState> findAllWithStateByIdIn(List<String> interrogationIds);

    /**
     * Delete interrogations linked to a campaign
     *
     * @param campaignId campaign id
     */
    @Transactional
    @Modifying
    @Query("delete from InterrogationDB s where s.campaign.id=:campaignId")
    void deleteInterrogations(String campaignId);
}
