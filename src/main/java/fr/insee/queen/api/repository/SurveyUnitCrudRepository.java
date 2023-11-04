package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.surveyunit.*;
import fr.insee.queen.api.entity.SurveyUnitDB;
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
* 
*/
@Repository
public interface SurveyUnitCrudRepository extends JpaRepository<SurveyUnitDB, String> {

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto(
		    s.id,
		    s.questionnaireModel.id
		)
		from SurveyUnitDB s where s.id=:surveyUnitId""")
	Optional<SurveyUnitSummaryDto> findSummaryById(String surveyUnitId);

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto(
		    s.id,
		    s.questionnaireModel.id
		)
		from SurveyUnitDB s where s.campaign.id=:campaignId""")
	List<SurveyUnitSummaryDto> findAllSummaryByCampaignId(String campaignId);

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto(
		    s.id,
		    s.questionnaireModel.id
		)
		from SurveyUnitDB s where s.id in :surveyUnitIds""")
	List<SurveyUnitSummaryDto> findAllSummaryByIdIn(List<String> surveyUnitIds);

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitDto(
		    s.id,
		    s.questionnaireModel.id,
		    s.personalization.value,
		    s.data.value,
		    s.comment.value,
		    new fr.insee.queen.api.dto.statedata.StateDataDto(
		        s.stateData.state,
		        s.stateData.date,
		        s.stateData.currentPage
		    ) as stateData
		)
		from SurveyUnitDB s where s.id=:surveyUnitId""")
	Optional<SurveyUnitDto> findOneById(String surveyUnitId);

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitDepositProofDto(
		    s.id,
		    new fr.insee.queen.api.dto.campaign.CampaignDto(
		        s.campaign.id,
		        s.campaign.label
		    ),
		    new fr.insee.queen.api.dto.statedata.StateDataDto(
		        s.stateData.state,
		        s.stateData.date,
		        s.stateData.currentPage
		    )
		)
		from SurveyUnitDB s where s.id=:surveyUnitId""")
	Optional<SurveyUnitDepositProofDto> findWithCampaignAndStateById(String surveyUnitId);

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto(
		    s.id,
		    s.campaign.id
		)
		from SurveyUnitDB s where s.id=:surveyUnitId""")
	Optional<SurveyUnitHabilitationDto> findWithCampaignById(String surveyUnitId);

	@Query("select s.id from SurveyUnitDB s order by s.id asc")
	Optional<List<String>> findAllIds();

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitWithStateDto(
		    s.id,
		    new fr.insee.queen.api.dto.statedata.StateDataDto(
		        s.stateData.state,
		        s.stateData.date,
		        s.stateData.currentPage
		    )
		)
		from SurveyUnitDB s where s.id in :surveyUnitIds""")
	List<SurveyUnitWithStateDto> findAllWithStateByIdIn(List<String> surveyUnitIds);

	@Transactional
    @Modifying
	@Query("delete from SurveyUnitDB s where s.campaign.id=:campaignId")
	void deleteSurveyUnits(String campaignId);

	@Transactional
    @Modifying
	@Query(value = """
		INSERT INTO survey_unit (id, campaign_id, questionnaire_model_id, data, comment, personalization)
		VALUES (:id, :campaignId, :questionnaireId, :data\\:\\:jsonb, :comment\\:\\:jsonb, :personalization\\:\\:jsonb)""", nativeQuery = true)
	void createSurveyUnit(String id, String campaignId, String questionnaireId, String data, String comment, String personalization);

	@Transactional
    @Modifying
	@Query("update PersonalizationDB p set p.value = :personalization where p.surveyUnit.id = :surveyUnitId")
	void updatePersonalization(String surveyUnitId, String personalization);

	@Transactional
    @Modifying
	@Query("update CommentDB c set c.value = :comment where c.surveyUnit.id = :surveyUnitId")
	void updateComment(String surveyUnitId, String comment);

	@Transactional
    @Modifying
	@Query("update DataDB d set d.value = :data where d.surveyUnit.id = :surveyUnitId")
	void updateData(String surveyUnitId, String data);

	@Query("select s.comment.value from SurveyUnitDB s where s.id=:surveyUnitId")
	String getComment(String surveyUnitId);

	@Query("select s.data.value from SurveyUnitDB s where s.id=:surveyUnitId")
	String getData(String surveyUnitId);

	@Query("select s.personalization.value from SurveyUnitDB s where s.id=:surveyUnitId")
	String getPersonalization(String surveyUnitId);
}
