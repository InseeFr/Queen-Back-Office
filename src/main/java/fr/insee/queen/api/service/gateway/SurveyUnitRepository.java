package fr.insee.queen.api.service.gateway;

import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.dto.surveyunit.*;

import java.util.List;
import java.util.Optional;

/**
* CommentRepository is the repository using to access to  Comment table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface SurveyUnitRepository {
	Optional<SurveyUnitSummaryDto> findSummaryById(String surveyUnitId);
	List<SurveyUnitSummaryDto> findAllSummaryByCampaignId(String campaignId);
	List<SurveyUnitSummaryDto> findAllSummaryByIdIn(List<String> surveyUnitIds);
	Optional<SurveyUnitDto> find(String surveyUnitId);
	Optional<SurveyUnitDepositProofDto> findWithCampaignAndStateById(String surveyUnitId);
	Optional<SurveyUnitHabilitationDto> findWithCampaignById(String surveyUnitId);
	Optional<List<String>> findAllIds();
	List<SurveyUnitWithStateDto> findAllWithStateByIdIn(List<String> surveyUnitIds);
	void deleteSurveyUnits(String campaignId);
	void delete(String surveyUnitId);
	void create(String id, String campaignId, String questionnaireId, String dataValue, String commentValue, String personalizationValue, StateDataDto stateDataValue);
	void updatePersonalization(String surveyUnitId, String personalization);
	void updateComment(String surveyUnitId, String comment);
	void updateData(String surveyUnitId, String data);
	Optional<String> findComment(String surveyUnitId);
	Optional<String> findData(String surveyUnitId);
	Optional<String> findPersonalization(String surveyUnitId);
	boolean exists(String surveyUnitId);
	void update(String surveyUnitId, String personalization, String comment, String data, StateDataDto stateData);
}
