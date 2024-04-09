package fr.insee.queen.infrastructure.db.surveyunit.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitDepositProof;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitState;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.infrastructure.db.campaign.entity.CampaignDB;
import fr.insee.queen.infrastructure.db.campaign.entity.QuestionnaireModelDB;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.CampaignJpaRepository;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.QuestionnaireModelJpaRepository;
import fr.insee.queen.infrastructure.db.surveyunit.entity.*;
import fr.insee.queen.infrastructure.db.surveyunit.projection.SurveyUnitProjection;
import fr.insee.queen.infrastructure.db.surveyunit.repository.jpa.*;
import fr.insee.queen.infrastructure.db.surveyunittempzone.repository.jpa.SurveyUnitTempZoneJpaRepository;
import fr.insee.queen.infrastructure.db.paradata.repository.jpa.ParadataEventJpaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DAO to handle survey units in DB
 */
@Repository
@AllArgsConstructor
@Slf4j
public class SurveyUnitDao implements SurveyUnitRepository {

    private final SurveyUnitJpaRepository crudRepository;
    private final CommentJpaRepository commentRepository;
    private final PersonalizationJpaRepository personalizationRepository;
    private final DataJpaRepository dataRepository;
    private final StateDataDao stateDataDao;
    private final CampaignJpaRepository campaignRepository;
    private final QuestionnaireModelJpaRepository questionnaireModelRepository;
    private final SurveyUnitTempZoneJpaRepository surveyUnitTempZoneRepository;
    private final ParadataEventJpaRepository paradataEventRepository;
    private static final String COLLECTED_DATA_ATTRIBUTE = "COLLECTED";

    @Value("${feature.perfdata.collected.native-insert}")
    private boolean isNativeInsert;

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
        DataDB dataDB = new DataDB(surveyUnit.data(), surveyUnitDB);
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
            DataDB dataDB = new DataDB(data, surveyUnit);
            dataRepository.save(dataDB);
        }
    }

    @Override
    public void updateCollectedData(String surveyUnitId, ObjectNode partialCollectedDataNode) {
        if(isNativeInsert) {
            dataRepository.updateCollectedData(surveyUnitId, partialCollectedDataNode);
            return;
        }

        ObjectNode dataNode = dataRepository.getData(surveyUnitId);

        if(!dataNode.has(COLLECTED_DATA_ATTRIBUTE)) {
            dataNode.set(COLLECTED_DATA_ATTRIBUTE, partialCollectedDataNode);
            dataRepository.updateData(surveyUnitId, dataNode);
            return;
        }

        ObjectNode collectedNode = (ObjectNode) dataNode.get(COLLECTED_DATA_ATTRIBUTE);
        for (Iterator<Map.Entry<String, JsonNode>> it = partialCollectedDataNode.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> field = it.next();
            collectedNode.set(field.getKey(), field.getValue());
        }
        dataRepository.updateData(surveyUnitId, dataNode);
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
    public Optional<ArrayNode> findPersonalization(String surveyUnitId) {
        return personalizationRepository.findPersonalization(surveyUnitId);
    }

    @Override
    public boolean exists(String surveyUnitId) {
        return crudRepository.existsById(surveyUnitId);
    }

    @Override
    public void updateInfos(SurveyUnit surveyUnit) {
        String surveyUnitId = surveyUnit.id();
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


}
