package fr.insee.queen.api.surveyunit.repository;

import fr.insee.queen.api.campaign.repository.entity.CampaignDB;
import fr.insee.queen.api.campaign.repository.entity.QuestionnaireModelDB;
import fr.insee.queen.api.campaign.repository.jpa.CampaignJpaRepository;
import fr.insee.queen.api.campaign.repository.jpa.QuestionnaireModelJpaRepository;
import fr.insee.queen.api.depositproof.service.model.SurveyUnitDepositProof;
import fr.insee.queen.api.surveyunit.repository.entity.*;
import fr.insee.queen.api.surveyunit.repository.jpa.CommentJpaRepository;
import fr.insee.queen.api.surveyunit.repository.jpa.DataJpaRepository;
import fr.insee.queen.api.surveyunit.repository.jpa.PersonalizationJpaRepository;
import fr.insee.queen.api.surveyunit.repository.jpa.SurveyUnitJpaRepository;
import fr.insee.queen.api.surveyunit.service.gateway.SurveyUnitRepository;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitState;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import fr.insee.queen.api.surveyunittempzone.repository.jpa.SurveyUnitTempZoneJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CommentRepository is the repository using to access to  Comment table in DB
 *
 * @author Claudel Benjamin
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

    @Override
    public Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId) {
        return crudRepository.findSummaryById(surveyUnitId);
    }

    @Override
    public List<SurveyUnitSummary> findAllSummaryByCampaignId(String campaignId) {
        return crudRepository.findAllSummaryByCampaignId(campaignId);
    }

    @Override
    public List<SurveyUnitSummary> findAllSummaryByIdIn(List<String> surveyUnitIds) {
        return crudRepository.findAllSummaryByIdIn(surveyUnitIds);
    }

    @Override
    public Optional<SurveyUnit> find(String surveyUnitId) {
        return crudRepository.findOneById(surveyUnitId);
    }

    @Override
    public Optional<SurveyUnitDepositProof> findWithCampaignAndStateById(String surveyUnitId) {
        return crudRepository.findWithCampaignAndStateById(surveyUnitId);
    }

    @Override
    public Optional<List<String>> findAllIds() {
        return crudRepository.findAllIds();
    }

    @Override
    public List<SurveyUnitState> findAllWithStateByIdIn(List<String> surveyUnitIds) {
        return crudRepository.findAllWithStateByIdIn(surveyUnitIds);
    }

    @Override
    public void deleteSurveyUnits(String campaignId) {
        dataRepository.deleteDatas(campaignId);
        stateDataDao.deleteStateDatas(campaignId);
        commentRepository.deleteComments(campaignId);
        personalizationRepository.deletePersonalizations(campaignId);
        surveyUnitTempZoneRepository.deleteSurveyUnits(campaignId);
        crudRepository.deleteSurveyUnits(campaignId);
    }

    @Override
    public void delete(String surveyUnitId) {
        dataRepository.deleteBySurveyUnitId(surveyUnitId);
        stateDataDao.deleteBySurveyUnitId(surveyUnitId);
        commentRepository.deleteBySurveyUnitId(surveyUnitId);
        personalizationRepository.deleteBySurveyUnitId(surveyUnitId);
        surveyUnitTempZoneRepository.deleteBySurveyUnitId(surveyUnitId);
        crudRepository.deleteById(surveyUnitId);
    }

    @Override
    public void create(SurveyUnit surveyUnit) {
        CampaignDB campaign = campaignRepository.getReferenceById(surveyUnit.campaignId());
        QuestionnaireModelDB questionnaire = questionnaireModelRepository.getReferenceById(surveyUnit.questionnaireId());
        SurveyUnitDB surveyUnitDB = new SurveyUnitDB(surveyUnit.id(), campaign, questionnaire);
        DataDB dataDB = new DataDB(UUID.randomUUID(), surveyUnit.data(), surveyUnitDB);
        CommentDB commentDB = new CommentDB(UUID.randomUUID(), surveyUnit.comment(), surveyUnitDB);
        PersonalizationDB personalizationDB = new PersonalizationDB(UUID.randomUUID(), surveyUnit.personalization(), surveyUnitDB);
        StateDataDB stateDataDB;
        StateData stateData = surveyUnit.stateData();
        if (stateData == null) {
            stateDataDB = new StateDataDB();
            stateDataDB.surveyUnit(surveyUnitDB);
        } else {
            stateDataDB = new StateDataDB(UUID.randomUUID(), stateData.state(), stateData.date(), stateData.currentPage(), surveyUnitDB);
        }

        surveyUnitDB.personalization(personalizationDB);
        surveyUnitDB.comment(commentDB);
        surveyUnitDB.data(dataDB);
        surveyUnitDB.stateData(stateDataDB);
        crudRepository.save(surveyUnitDB);
    }

    @Override
    public void updatePersonalization(String surveyUnitId, String personalization) {
        if (personalization == null) {
            return;
        }

        int countUpdated = personalizationRepository.updatePersonalization(surveyUnitId, personalization);
        if (countUpdated == 0) {
            SurveyUnitDB surveyUnit = crudRepository.getReferenceById(surveyUnitId);
            PersonalizationDB personalizationDB = new PersonalizationDB(UUID.randomUUID(), personalization, surveyUnit);
            personalizationRepository.save(personalizationDB);
        }
    }

    @Override
    public void updateComment(String surveyUnitId, String comment) {
        if (comment == null) {
            return;
        }

        int countUpdated = commentRepository.updateComment(surveyUnitId, comment);
        if (countUpdated == 0) {
            SurveyUnitDB surveyUnit = crudRepository.getReferenceById(surveyUnitId);
            CommentDB commentDB = new CommentDB(UUID.randomUUID(), comment, surveyUnit);
            commentRepository.save(commentDB);
        }
    }

    @Override
    public void updateData(String surveyUnitId, String data) {
        if (data == null) {
            return;
        }

        int countUpdated = dataRepository.updateData(surveyUnitId, data);
        if (countUpdated == 0) {
            SurveyUnitDB surveyUnit = crudRepository.getReferenceById(surveyUnitId);
            DataDB dataDB = new DataDB(UUID.randomUUID(), data, surveyUnit);
            dataRepository.save(dataDB);
        }
    }

    @Override
    public Optional<String> findComment(String surveyUnitId) {
        return commentRepository.findComment(surveyUnitId);
    }

    @Override
    public Optional<String> findData(String surveyUnitId) {
        return dataRepository.findData(surveyUnitId);
    }

    @Override
    public Optional<String> findPersonalization(String surveyUnitId) {
        return personalizationRepository.findPersonalization(surveyUnitId);
    }

    @Override
    public boolean exists(String surveyUnitId) {
        return crudRepository.existsById(surveyUnitId);
    }

    @Override
    public void updateInfos(SurveyUnit surveyUnit) {
        String surveyUnitId = surveyUnit.id();
        updatePersonalization(surveyUnitId, surveyUnit.personalization());
        updateComment(surveyUnitId, surveyUnit.comment());
        updateData(surveyUnitId, surveyUnit.data());
        stateDataDao.update(surveyUnitId, surveyUnit.stateData());
    }
}
