package fr.insee.queen.api.repository.jpa;

import fr.insee.queen.api.dto.surveyunit.*;
import fr.insee.queen.api.repository.entity.SurveyUnitDB;
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
public interface SurveyUnitJpaRepository extends JpaRepository<SurveyUnitDB, String> {

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
}
