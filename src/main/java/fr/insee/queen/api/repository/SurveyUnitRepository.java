package fr.insee.queen.api.repository;

import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.surveyunit.*;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
* CommentRepository is the repository using to access to  Comment table in DB
* 
* @author Claudel Benjamin
* 
*/
@Transactional
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
		    s.personalization.value,
		    s.data.value,
		    s.comment.value,
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
		select new fr.insee.queen.api.dto.surveyunit.SurveyUnitDepositProofDto(
		    s.id,
		    new fr.insee.queen.api.dto.statedata.StateDataDto(
		        s.stateData.state,
		        s.stateData.date,
		        s.stateData.currentPage
		    )
		)
		from SurveyUnit s where s.id in :surveyUnitIds""")
	List<SurveyUnitWithStateDto> findAllWithStateByIdIn(List<String> surveyUnitIds);


	@Modifying
	@Query(value = """
		INSERT INTO survey_unit (id, campaign_id, questionnaire_model_id)
		VALUES (:id,:campaignId,:questionnaireId)""", nativeQuery = true)
	void createSurveyUnit(String id, String campaignId, String questionnaireId);

	@Modifying
	@Query(value = """
	delete from paradata_event where id in (
	    select p.id from survey_unit s inner join paradata_event p
	        on text(s.id) = p.value->>'idSU'
	        where s.campaign_id = :campaignId
	)""", nativeQuery = true)
	void deleteParadataEvents(String campaignId);
}
