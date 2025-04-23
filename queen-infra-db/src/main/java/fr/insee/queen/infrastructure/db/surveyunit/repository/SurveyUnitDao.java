package fr.insee.queen.infrastructure.db.surveyunit.repository;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import fr.insee.queen.domain.surveyunit.model.*;
import fr.insee.queen.infrastructure.db.campaign.entity.CampaignDB;
import fr.insee.queen.infrastructure.db.campaign.entity.QuestionnaireModelDB;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.CampaignJpaRepository;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.QuestionnaireModelJpaRepository;
import fr.insee.queen.infrastructure.db.data.entity.common.DataDB;
import fr.insee.queen.infrastructure.db.surveyunit.entity.*;
import fr.insee.queen.infrastructure.db.surveyunit.projection.SurveyUnitProjection;
import fr.insee.queen.infrastructure.db.surveyunit.repository.jpa.*;
import fr.insee.queen.infrastructure.db.configuration.DataFactory;
import fr.insee.queen.infrastructure.db.data.repository.jpa.DataRepository;
import fr.insee.queen.infrastructure.db.surveyunittempzone.repository.jpa.SurveyUnitTempZoneJpaRepository;
import fr.insee.queen.infrastructure.db.paradata.repository.jpa.ParadataEventJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DAO to handle survey units in DB
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class SurveyUnitDao implements SurveyUnitRepository {

    private final SurveyUnitJpaRepository crudRepository;
    private final CommentJpaRepository commentRepository;
    private final PersonalizationJpaRepository personalizationRepository;
    private final DataRepository dataRepository;
    private final StateDataDao stateDataDao;
    private final CampaignJpaRepository campaignRepository;
    private final QuestionnaireModelJpaRepository questionnaireModelRepository;
    private final SurveyUnitTempZoneJpaRepository surveyUnitTempZoneRepository;
    private final ParadataEventJpaRepository paradataEventRepository;
    private final DataFactory dataFactory;
    private final EntityManager entityManager;

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
        return crudRepository.findOneById(surveyUnitId)
                .map(SurveyUnitProjection::toModel);
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
    public List<SurveyUnitState> findAllByState(String campaignId, StateDataType state) {
        return crudRepository.findAllByState(campaignId, state);
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
        paradataEventRepository.deleteBySurveyUnitCampaignId(campaignId);
        crudRepository.deleteSurveyUnits(campaignId);
    }

    @Override
    public void delete(String surveyUnitId) {
        dataRepository.deleteBySurveyUnitId(surveyUnitId);
        stateDataDao.deleteBySurveyUnitId(surveyUnitId);
        commentRepository.deleteBySurveyUnitId(surveyUnitId);
        personalizationRepository.deleteBySurveyUnitId(surveyUnitId);
        surveyUnitTempZoneRepository.deleteBySurveyUnitId(surveyUnitId);
        paradataEventRepository.deleteBySurveyUnitId(surveyUnitId);
        crudRepository.deleteById(surveyUnitId);
    }

    @Override
    public void create(SurveyUnit surveyUnit) {
        CampaignDB campaign = campaignRepository.getReferenceById(surveyUnit.campaignId());
        QuestionnaireModelDB questionnaire = questionnaireModelRepository.getReferenceById(surveyUnit.questionnaireId());
        SurveyUnitDB surveyUnitDB = new SurveyUnitDB(surveyUnit.id(), campaign, questionnaire);
        DataDB dataDB = dataFactory.buildData(surveyUnit.data(), surveyUnitDB);
        CommentDB commentDB = new CommentDB(surveyUnit.comment(), surveyUnitDB);
        PersonalizationDB personalizationDB = new PersonalizationDB(surveyUnit.personalization(), surveyUnitDB);
        surveyUnitDB.setPersonalization(personalizationDB);
        surveyUnitDB.setComment(commentDB);
        surveyUnitDB.setData(dataDB);
        crudRepository.save(surveyUnitDB);
    }

    @Override
    public void savePersonalization(String surveyUnitId, ArrayNode personalization) {
        if (personalization == null) {
            return;
        }

        int countUpdated = personalizationRepository.updatePersonalization(surveyUnitId, personalization);
        if (countUpdated == 0) {
            SurveyUnitDB surveyUnit = crudRepository.getReferenceById(surveyUnitId);
            PersonalizationDB personalizationDB = new PersonalizationDB(personalization, surveyUnit);
            personalizationRepository.save(personalizationDB);
        }
    }

    @Override
    public void saveComment(String surveyUnitId, ObjectNode comment) {
        if (comment == null) {
            return;
        }

        int countUpdated = commentRepository.updateComment(surveyUnitId, comment);
        if (countUpdated == 0) {
            SurveyUnitDB surveyUnit = crudRepository.getReferenceById(surveyUnitId);
            CommentDB commentDB = new CommentDB(comment, surveyUnit);
            commentRepository.save(commentDB);
        }
    }

    @Override
    public void saveData(String surveyUnitId, ObjectNode data) {
        if (data == null) {
            return;
        }
        int countUpdated = dataRepository.updateData(surveyUnitId, data);
        if (countUpdated == 0) {
            SurveyUnitDB surveyUnit = crudRepository.getReferenceById(surveyUnitId);
            DataDB dataDB = dataFactory.buildData(data, surveyUnit);
            dataRepository.save(dataDB);
        }
    }

    @Override
    public void updateCollectedData(String surveyUnitId, ObjectNode partialCollectedDataNode) {
        dataRepository.updateCollectedData(surveyUnitId, partialCollectedDataNode);
    }

    @Override
    public Optional<ObjectNode> findComment(String surveyUnitId) {
        return commentRepository.findComment(surveyUnitId);
    }

    @Override
    public Optional<ObjectNode> findData(String surveyUnitId) {
        return dataRepository.findData(surveyUnitId);
    }

    @Override
    public SurveyUnitPersonalization getSurveyUnitPersonalization(String surveyUnitId) {
        return crudRepository.getPersonalizationById(surveyUnitId);
    }

    @Override
    public Optional<ArrayNode> findPersonalization(String surveyUnitId) {
        return personalizationRepository.findPersonalization(surveyUnitId);
    }

    @Override
    public boolean exists(String surveyUnitId) {
        return crudRepository.existsById(surveyUnitId);
    }

    @Transactional
    @Override
    public void update(SurveyUnit surveyUnit) {
        String surveyUnitId = surveyUnit.id();
        save(surveyUnitId, surveyUnit.campaignId(), surveyUnit.questionnaireId());
        savePersonalization(surveyUnitId, surveyUnit.personalization());
        saveComment(surveyUnitId, surveyUnit.comment());
        saveData(surveyUnitId, surveyUnit.data());
    }

    @Override
    public List<SurveyUnit> find(List<String> surveyUnitIds) {
        return crudRepository.findSurveyUnitsByIdIn(surveyUnitIds).stream()
                .map(SurveyUnitProjection::toModel)
                .toList();
    }

    @Override
    public List<SurveyUnit> findAll() {
        return crudRepository.findAllSurveyUnits().stream()
                .map(SurveyUnitProjection::toModel)
                .toList();
    }

    @Override
    public void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp) {
        dataRepository.cleanExtractedData(campaignId, startTimestamp, endTimestamp);
    }

    private void save(String surveyUnitId, String campaignId, String questionnaireId) {
        Map<String, Object> fieldsToUpdate = new LinkedHashMap<>();
        if (campaignId != null) {
            fieldsToUpdate.put("campaign_id", campaignId);
        }

        if (questionnaireId != null) {
            fieldsToUpdate.put("questionnaire_model_id", questionnaireId);
        }

        if (fieldsToUpdate.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("UPDATE survey_unit SET ");

        boolean firstKey = true;
        for (String fieldKey : fieldsToUpdate.keySet()) {
            if (!firstKey) {
                sql.append(", ");
            }
            sql
                .append(fieldKey)
                .append(" = :")
                .append(fieldKey);
            firstKey = false;
        }

        sql.append(" WHERE id = :surveyUnitId");

        var query = entityManager.createNativeQuery(sql.toString());
        fieldsToUpdate.forEach(query::setParameter);
        query.setParameter("surveyUnitId", surveyUnitId);

        query.executeUpdate();
    }
}
