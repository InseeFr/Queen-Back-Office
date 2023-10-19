package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.dto.surveyunit.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
public interface SurveyUnitRepository extends JpaRepository<SurveyUnit, String> {

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto(
		    s.id,
		    s.questionnaireModel.id
		)
		from SurveyUnit s where s.id=:surveyUnitId""")
	Optional<SurveyUnitSummaryDto> findSummaryById(String surveyUnitId);

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto(
		    s.id,
		    s.questionnaireModel.id
		)
		from SurveyUnit s where s.campaign.id=:campaignId""")
	List<SurveyUnitSummaryDto> findAllSummaryByCampaignId(String campaignId);

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitSummaryDto(
		    s.id,
		    s.questionnaireModel.id
		)
		from SurveyUnit s where s.id in :surveyUnitIds""")
	List<SurveyUnitSummaryDto> findAllSummaryByIdIn(List<String> surveyUnitIds);

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitDto(
		    s.id,
		    s.questionnaireModel.id,
		    s.personalization,
		    s.data,
		    s.comment,
		    new fr.insee.queen.api.dto.statedata.StateDataDto(
		        s.stateData.state,
		        s.stateData.date,
		        s.stateData.currentPage
		    ) as stateData
		)
		from SurveyUnit s where s.id=:surveyUnitId""")
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
		from SurveyUnit s where s.id=:surveyUnitId""")
	Optional<SurveyUnitDepositProofDto> findWithCampaignAndStateById(String surveyUnitId);

	@Query("""
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitHabilitationDto(
		    s.id,
		    s.campaign.id
		)
		from SurveyUnit s where s.id=:surveyUnitId""")
	Optional<SurveyUnitHabilitationDto> findWithCampaignById(String surveyUnitId);

	@Query("select s.id from SurveyUnit s order by s.id asc")
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
		from SurveyUnit s where s.id in :surveyUnitIds""")
	List<SurveyUnitWithStateDto> findAllWithStateByIdIn(List<String> surveyUnitIds);

	@Query("""
		select new fr.insee.queen.api.dto.statedata.StateDataDto(
		    s.stateData.state,
		    s.stateData.date,
		    s.stateData.currentPage
		)
		from SurveyUnit s where s.id = :surveyUnitId""")
	Optional<StateDataDto> findStateDataBySurveyUnitId(String surveyUnitId);

	@Transactional
	@Modifying
	@Query("""
		UPDATE SurveyUnit s 
		    SET s.stateData.currentPage=:#{#stateData.currentPage},
		    s.stateData.date=:#{#stateData.date}, 
		    s.stateData.state=:#{#stateData.state} 
		    WHERE s.id=:surveyUnitId""")
	void updateStateData(String surveyUnitId, StateDataDto stateData);

	@Transactional
    @Modifying
	@Query("delete from SurveyUnit s where s.campaign.id=:campaignId")
	void deleteSurveyUnits(String campaignId);

	@Transactional
    @Modifying
	@Query(value = """
		INSERT INTO survey_unit (id, campaign_id, questionnaire_model_id, data, comment, personalization, state, state_date, state_current_page)
		VALUES (:id, :campaignId, :questionnaireId, :data\\:\\:jsonb, :comment\\:\\:jsonb, :personalization\\:\\:jsonb, ?#{#stateData.state?.name() ?: NULL}, :#{#stateData.date}, :#{#stateData.currentPage} )""", nativeQuery = true)
	void createSurveyUnit(String id, String campaignId, String questionnaireId, String data, String comment, String personalization, StateDataDto stateData);

	@Transactional
    @Modifying
	@Query("update SurveyUnit s set s.personalization = :personalization where s.id = :surveyUnitId")
	void updatePersonalization(String surveyUnitId, String personalization);

	@Transactional
    @Modifying
	@Query("update SurveyUnit s set s.comment = :comment where s.id = :surveyUnitId")
	void updateComment(String surveyUnitId, String comment);

	@Transactional
    @Modifying
	@Query("update SurveyUnit s set s.data = :data where s.id = :surveyUnitId")
	void updateData(String surveyUnitId, String data);

	@Query("select s.comment from SurveyUnit s where s.id=:surveyUnitId")
	String getComment(String surveyUnitId);

	@Query("select s.data from SurveyUnit s where s.id=:surveyUnitId")
	String getData(String surveyUnitId);

	@Query("select s.personalization from SurveyUnit s where s.id=:surveyUnitId")
	String getPersonalization(String surveyUnitId);
}
