package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.dto.surveyunit.*;
import fr.insee.queen.api.entity.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
* CommentRepository is the repository using to access to  Comment table in DB
* 
* @author Claudel Benjamin
* 
*/
@Repository
@AllArgsConstructor
public class SurveyUnitRepository {

	private final SurveyUnitCrudRepository crudRepository;
	private final CampaignCrudRepository campaignCrudRepository;
	private final QuestionnaireModelCrudRepository questionnaireModelCrudRepository;

	public Optional<SurveyUnitSummaryDto> findSummaryById(String surveyUnitId) {
		return crudRepository.findSummaryById(surveyUnitId);
	}

	public List<SurveyUnitSummaryDto> findAllSummaryByCampaignId(String campaignId) {
		return crudRepository.findAllSummaryByCampaignId(campaignId);
	}

	public List<SurveyUnitSummaryDto> findAllSummaryByIdIn(List<String> surveyUnitIds) {
		return crudRepository.findAllSummaryByIdIn(surveyUnitIds);
	}

	public Optional<SurveyUnitDto> findOneById(String surveyUnitId) {
		return crudRepository.findOneById(surveyUnitId);
	}

	public Optional<SurveyUnitDepositProofDto> findWithCampaignAndStateById(String surveyUnitId) {
		return crudRepository.findWithCampaignAndStateById(surveyUnitId);
	}

	public Optional<SurveyUnitHabilitationDto> findWithCampaignById(String surveyUnitId) {
		return crudRepository.findWithCampaignById(surveyUnitId);
	}

	public Optional<List<String>> findAllIds() {
			return crudRepository.findAllIds();
	}

	public List<SurveyUnitWithStateDto> findAllWithStateByIdIn(List<String> surveyUnitIds) {
			return crudRepository.findAllWithStateByIdIn(surveyUnitIds);
	}

	public void deleteSurveyUnits(String campaignId) {
		crudRepository.deleteSurveyUnits(campaignId);
	}

	public void createSurveyUnit(String id, String campaignId, String questionnaireId, String dataValue, String commentValue, String personalizationValue, StateDataDto stateDataValue) {
		CampaignDB campaign = campaignCrudRepository.getReferenceById(campaignId);
		QuestionnaireModelDB questionnaire = questionnaireModelCrudRepository.getReferenceById(questionnaireId);
		SurveyUnitDB surveyUnit = new SurveyUnitDB(id, campaign, questionnaire);
		DataDB data = new DataDB(UUID.randomUUID(), dataValue, surveyUnit);
		CommentDB comment = new CommentDB(UUID.randomUUID(), commentValue, surveyUnit);
		PersonalizationDB personalization = new PersonalizationDB(UUID.randomUUID(), personalizationValue, surveyUnit);
		StateDataDB stateData;
		if(stateDataValue == null) {
			stateData = new StateDataDB();
			stateData.surveyUnit(surveyUnit);
		} else {
			stateData = new StateDataDB(UUID.randomUUID(), stateDataValue.state(), stateDataValue.date(), stateDataValue.currentPage(), surveyUnit);
		}

		surveyUnit.personalization(personalization);
		surveyUnit.comment(comment);
		surveyUnit.data(data);
		surveyUnit.stateData(stateData);
		crudRepository.save(surveyUnit);
	}

	public void updatePersonalization(String surveyUnitId, String personalization) {
		crudRepository.updatePersonalization(surveyUnitId, personalization);
	}

	public void updateComment(String surveyUnitId, String comment) {
		crudRepository.updateComment(surveyUnitId, comment);
	}

	public void updateData(String surveyUnitId, String data) {
		crudRepository.updateData(surveyUnitId, data);
	}

	public String getComment(String surveyUnitId) {
		return crudRepository.getComment(surveyUnitId);
	}

	public String getData(String surveyUnitId) {
		return crudRepository.getData(surveyUnitId);
	}

	public String getPersonalization(String surveyUnitId) {
		return crudRepository.getPersonalization(surveyUnitId);
	}

	public void deleteById(String surveyUnitId) {
		crudRepository.deleteById(surveyUnitId);
	}

	public boolean existsById(String surveyUnitId) {
		return crudRepository.existsById(surveyUnitId);
	}
}
