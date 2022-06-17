package fr.insee.queen.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.insee.queen.api.domain.SurveyUnit;
import fr.insee.queen.api.dto.surveyunit.SurveyUnitDto;
/**
* CommentRepository is the repository using to access to  Comment table in DB
* 
* @author Claudel Benjamin
* 
*/
@Transactional
@Repository
public interface SurveyUnitRepository extends JpaRepository<SurveyUnit, String> {
	/**
	* This method retrieve all SurveyUnit in DB
	* 
	* @return List of all {@link SurveyUnit}
	*/
	List<SurveyUnitDto> findDtoBy();
	/**
	* This method retrieve all reporting units associated to a specific campaign
	* 
	* @param id id of the campaign
	* @return {@link SurveyUnitDto}
	*/
	List<SurveyUnitDto> findDtoByCampaignId(String id);
	
	/**
	* This method retrieve a reporting unit by his id
	* 
	* @param id id of the reporting unit
	* @return {@link ReportingUnitDto}
	*/
	SurveyUnitDto findDtoById(String id);
	List<SurveyUnit> findByCampaignId(String id);

	List<SurveyUnit> findAll();
}
