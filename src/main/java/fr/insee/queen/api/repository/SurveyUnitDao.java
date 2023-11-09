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
	private final StateDataDao stateDataDao;
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
		stateDataDao.deleteStateDatas(campaignId);
		commentRepository.deleteComments(campaignId);
		personalizationRepository.deletePersonalizations(campaignId);
		surveyUnitTempZoneRepository.deleteSurveyUnits(campaignId);
		crudRepository.deleteSurveyUnits(campaignId);
	}

	public void delete(String surveyUnitId) {
		dataRepository.deleteBySurveyUnitId(surveyUnitId);
		stateDataDao.deleteBySurveyUnitId(surveyUnitId);
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
		if(personalization == null) {
			return;
		}

		int countUpdated = personalizationRepository.updatePersonalization(surveyUnitId, personalization);
		if(countUpdated == 0) {
			SurveyUnitDB surveyUnit = crudRepository.getReferenceById(surveyUnitId);
			PersonalizationDB personalizationDB = new PersonalizationDB(UUID.randomUUID(), personalization, surveyUnit);
			personalizationRepository.save(personalizationDB);
		}
	}

	public void updateComment(String surveyUnitId, String comment) {
		if(comment == null) {
			return;
		}

		int countUpdated = commentRepository.updateComment(surveyUnitId, comment);
		if(countUpdated == 0) {
			SurveyUnitDB surveyUnit = crudRepository.getReferenceById(surveyUnitId);
			CommentDB commentDB = new CommentDB(UUID.randomUUID(), comment, surveyUnit);
			commentRepository.save(commentDB);
		}
	}

	public void updateData(String surveyUnitId, String data) {
		if(data == null) {
			return;
		}

		int countUpdated = dataRepository.updateData(surveyUnitId, data);
		if(countUpdated == 0) {
			SurveyUnitDB surveyUnit = crudRepository.getReferenceById(surveyUnitId);
			DataDB dataDB = new DataDB(UUID.randomUUID(), data, surveyUnit);
			dataRepository.save(dataDB);
		}
	}

	public Optional<String> findComment(String surveyUnitId) {
		return commentRepository.findComment(surveyUnitId);
	}

	public Optional<String> findData(String surveyUnitId) {
		return dataRepository.findData(surveyUnitId);
	}

	public Optional<String> findPersonalization(String surveyUnitId) {
		return personalizationRepository.findPersonalization(surveyUnitId);
	}
	public boolean exists(String surveyUnitId) {
		return crudRepository.existsById(surveyUnitId);
	}

	@Override
	public void update(String surveyUnitId, String personalization, String comment, String data, StateDataDto stateData) {
		updatePersonalization(surveyUnitId, personalization);
		updateComment(surveyUnitId, comment);
		updateData(surveyUnitId, data);
		stateDataDao.update(surveyUnitId, stateData);
	}
}
