package fr.insee.queen.api.repository;

import fr.insee.queen.api.dto.statedata.StateDataDto;
import fr.insee.queen.api.dto.surveyunit.*;
import fr.insee.queen.api.repository.entity.*;
import fr.insee.queen.api.repository.jpa.*;
import fr.insee.queen.api.service.gateway.SurveyUnitRepository;
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
public class SurveyUnitDao implements SurveyUnitRepository {

	private final SurveyUnitJpaRepository crudRepository;
	private final CommentJpaRepository commentRepository;
	private final PersonalizationJpaRepository personalizationRepository;
	private final DataJpaRepository dataRepository;
	private final StateDataJpaRepository stateDataRepository;
	private final CampaignJpaRepository campaignRepository;
	private final QuestionnaireModelJpaRepository questionnaireModelRepository;
	private final SurveyUnitTempZoneJpaRepository surveyUnitTempZoneRepository;

	public Optional<SurveyUnitSummaryDto> findSummaryById(String surveyUnitId) {
		return crudRepository.findSummaryById(surveyUnitId);
	}

	public List<SurveyUnitSummaryDto> findAllSummaryByCampaignId(String campaignId) {
		return crudRepository.findAllSummaryByCampaignId(campaignId);
	}

	public List<SurveyUnitSummaryDto> findAllSummaryByIdIn(List<String> surveyUnitIds) {
		return crudRepository.findAllSummaryByIdIn(surveyUnitIds);
	}

	public Optional<SurveyUnitDto> find(String surveyUnitId) {
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
		dataRepository.deleteDatas(campaignId);
		stateDataRepository.deleteStateDatas(campaignId);
		commentRepository.deleteComments(campaignId);
		personalizationRepository.deletePersonalizations(campaignId);
		surveyUnitTempZoneRepository.deleteSurveyUnits(campaignId);
		crudRepository.deleteSurveyUnits(campaignId);
	}

	public void delete(String surveyUnitId) {
		dataRepository.deleteBySurveyUnitId(surveyUnitId);
		stateDataRepository.deleteBySurveyUnitId(surveyUnitId);
		commentRepository.deleteBySurveyUnitId(surveyUnitId);
		personalizationRepository.deleteBySurveyUnitId(surveyUnitId);
		surveyUnitTempZoneRepository.deleteBySurveyUnitId(surveyUnitId);
		crudRepository.deleteById(surveyUnitId);
	}

	public void create(String id, String campaignId, String questionnaireId, String dataValue, String commentValue, String personalizationValue, StateDataDto stateDataValue) {
		CampaignDB campaign = campaignRepository.getReferenceById(campaignId);
		QuestionnaireModelDB questionnaire = questionnaireModelRepository.getReferenceById(questionnaireId);
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
		personalizationRepository.updatePersonalization(surveyUnitId, personalization);
	}

	public void updateComment(String surveyUnitId, String comment) {
		commentRepository.updateComment(surveyUnitId, comment);
	}

	public void updateData(String surveyUnitId, String data) {
		dataRepository.updateData(surveyUnitId, data);
	}

	public String getComment(String surveyUnitId) {
		return commentRepository.getComment(surveyUnitId);
	}

	public String getData(String surveyUnitId) {
		return dataRepository.getData(surveyUnitId);
	}

	public String getPersonalization(String surveyUnitId) {
		return personalizationRepository.getPersonalization(surveyUnitId);
	}
	public boolean exists(String surveyUnitId) {
		return crudRepository.existsById(surveyUnitId);
	}
}
