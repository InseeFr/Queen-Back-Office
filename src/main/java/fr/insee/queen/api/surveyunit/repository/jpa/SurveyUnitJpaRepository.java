package fr.insee.queen.api.surveyunit.repository.jpa;

import fr.insee.queen.api.depositproof.service.model.SurveyUnitDepositProof;
import fr.insee.queen.api.surveyunit.repository.entity.SurveyUnitDB;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitState;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CommentRepository is the repository using to access to  Comment table in DB
 *
 * @author Claudel Benjamin
 */
@Repository
public interface SurveyUnitJpaRepository extends JpaRepository<SurveyUnitDB, String> {

    @Query("""
            select new fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary(
                s.id,
                s.questionnaireModel.id,
                s.campaign.id
            )
            from SurveyUnitDB s where s.id=:surveyUnitId""")
    Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId);

    @Query("""
            select new fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary(
                s.id,
                s.questionnaireModel.id,
                s.campaign.id
            )
            from SurveyUnitDB s where s.campaign.id=:campaignId""")
    List<SurveyUnitSummary> findAllSummaryByCampaignId(String campaignId);

    @Query("""
            select new fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary(
                s.id,
                s.questionnaireModel.id,
                s.campaign.id
            )
            from SurveyUnitDB s where s.id in :surveyUnitIds""")
    List<SurveyUnitSummary> findAllSummaryByIdIn(List<String> surveyUnitIds);

    @Query("""
            select new fr.insee.queen.api.surveyunit.service.model.SurveyUnit(
                s.id,
                s.campaign.id,
                s.questionnaireModel.id,
                s.personalization.value,
                s.data.value,
                s.comment.value,
                new fr.insee.queen.api.surveyunit.service.model.StateData(
                    s.stateData.state,
                    s.stateData.date,
                    s.stateData.currentPage
                ) as stateData
            )
            from SurveyUnitDB s left join s.personalization left join s.data left join s.comment left join s.stateData where s.id=:surveyUnitId""")
    Optional<SurveyUnit> findOneById(String surveyUnitId);

    @Query("""
            select new fr.insee.queen.api.depositproof.service.model.SurveyUnitDepositProof(
                s.id,
                new fr.insee.queen.api.campaign.service.model.CampaignSummary(
                    s.campaign.id,
                    s.campaign.label
                ),
                new fr.insee.queen.api.surveyunit.service.model.StateData(
                    s.stateData.state,
                    s.stateData.date,
                    s.stateData.currentPage
                )
            )
            from SurveyUnitDB s where s.id=:surveyUnitId""")
    Optional<SurveyUnitDepositProof> findWithCampaignAndStateById(String surveyUnitId);

    @Query("select s.id from SurveyUnitDB s order by s.id asc")
    Optional<List<String>> findAllIds();

    @Query("""
            select new fr.insee.queen.api.surveyunit.service.model.SurveyUnitState(
                s.id,
                s.questionnaireModel.id,
                s.campaign.id,
                new fr.insee.queen.api.surveyunit.service.model.StateData(
                    s.stateData.state,
                    s.stateData.date,
                    s.stateData.currentPage
                )
            )
            from SurveyUnitDB s left join s.stateData where s.id in :surveyUnitIds""")
    List<SurveyUnitState> findAllWithStateByIdIn(List<String> surveyUnitIds);

    @Transactional
    @Modifying
    @Query("delete from SurveyUnitDB s where s.campaign.id=:campaignId")
    void deleteSurveyUnits(String campaignId);
}
