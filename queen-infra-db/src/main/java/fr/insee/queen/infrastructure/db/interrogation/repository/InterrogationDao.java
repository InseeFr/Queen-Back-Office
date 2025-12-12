package fr.insee.queen.infrastructure.db.interrogation.repository;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.infrastructure.db.campaign.entity.CampaignDB;
import fr.insee.queen.infrastructure.db.campaign.entity.QuestionnaireModelDB;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.CampaignJpaRepository;
import fr.insee.queen.infrastructure.db.campaign.repository.jpa.QuestionnaireModelJpaRepository;
import fr.insee.queen.infrastructure.db.data.entity.common.DataDB;
import fr.insee.queen.infrastructure.db.interrogation.entity.*;
import fr.insee.queen.infrastructure.db.interrogation.projection.InterrogationProjection;
import fr.insee.queen.infrastructure.db.interrogation.repository.jpa.*;
import fr.insee.queen.infrastructure.db.configuration.DataFactory;
import fr.insee.queen.infrastructure.db.data.repository.jpa.DataRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.stream.Collectors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DAO to handle interrogations in DB
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class InterrogationDao implements InterrogationRepository {

    private final InterrogationJpaRepository crudRepository;
    private final CommentJpaRepository commentRepository;
    private final PersonalizationJpaRepository personalizationRepository;
    private final DataRepository dataRepository;
    private final CampaignJpaRepository campaignRepository;
    private final QuestionnaireModelJpaRepository questionnaireModelRepository;
    private final DataFactory dataFactory;
    private final EntityManager entityManager;

    @Override
    public Optional<InterrogationSummary> findSummaryById(String interrogationId) {
        return crudRepository.findSummaryById(interrogationId);
    }

    @Override
    public List<InterrogationSummary> findAllSummaryByCampaignId(String campaignId) {
        return crudRepository.findAllSummaryByCampaignId(campaignId);
    }

    @Override
    public List<InterrogationSummary> findAllSummaryBySurveyUnitId(String surveyUnitId) {
        return crudRepository.findAllSummaryBySurveyUnitId(surveyUnitId);
    }

    @Override
    public List<InterrogationSummary> findAllSummaryByIdIn(List<String> interrogationIds) {
        return crudRepository.findAllSummaryByIdIn(interrogationIds);
    }

    @Override
    public Optional<Interrogation> find(String interrogationId) {
        return crudRepository.findOneById(interrogationId)
                .map(InterrogationProjection::toModel);
    }

    @Override
    public Optional<InterrogationDepositProof> findWithCampaignAndStateById(String interrogationId) {
        return crudRepository.findWithCampaignAndStateById(interrogationId);
    }

    @Override
    public Optional<List<String>> findAllIds() {
        return crudRepository.findAllIds();
    }

    @Override
    public List<InterrogationState> findAllByState(String campaignId, StateDataType state) {
        return crudRepository.findAllByState(campaignId, state);
    }

    @Override
    public List<Interrogation> findAllByState(StateDataType state) {
        return crudRepository.findAllInterrogationsByState(state).stream()
                .map(InterrogationProjection::toModel)
                .toList();
    }

    @Override
    public List<InterrogationState> findAllWithStateByIdIn(List<String> interrogationIds) {
        return crudRepository.findAllWithStateByIdIn(interrogationIds);
    }

    @Override
    public void deleteInterrogations(String campaignId) {
        crudRepository.deleteInterrogations(campaignId);
    }

    @Override
    public void delete(String interrogationId) {
        crudRepository.deleteById(interrogationId);
    }

    @Override
    public void create(Interrogation interrogation) {
        CampaignDB campaign = campaignRepository.getReferenceById(interrogation.campaignId());
        QuestionnaireModelDB questionnaire = questionnaireModelRepository.getReferenceById(interrogation.questionnaireId());
        InterrogationDB interrogationDB = new InterrogationDB(interrogation.id(), interrogation.surveyUnitId(), campaign, questionnaire, interrogation.correlationId());
        DataDB dataDB = dataFactory.buildData(interrogation.data(), interrogationDB);
        CommentDB commentDB = new CommentDB(interrogation.comment(), interrogationDB);
        if (interrogation.personalization() != null) {
            PersonalizationDB personalizationDB = new PersonalizationDB(interrogation.personalization(), interrogationDB);
            interrogationDB.setPersonalization(personalizationDB);
        }
        interrogationDB.setComment(commentDB);
        interrogationDB.setData(dataDB);
        interrogationDB.setCorrelationId(interrogation.correlationId());
        crudRepository.save(interrogationDB);
    }

    @Override
    public void savePersonalization(String interrogationId, ArrayNode personalization) {
        if (personalization == null) {
            return;
        }

        int countUpdated = personalizationRepository.updatePersonalization(interrogationId, personalization);
        if (countUpdated == 0) {
            InterrogationDB interrogation = crudRepository.getReferenceById(interrogationId);
            PersonalizationDB personalizationDB = new PersonalizationDB(personalization, interrogation);
            personalizationRepository.save(personalizationDB);
        }
    }

    @Override
    public void saveComment(String interrogationId, ObjectNode comment) {
        if (comment == null) {
            return;
        }

        int countUpdated = commentRepository.updateComment(interrogationId, comment);
        if (countUpdated == 0) {
            InterrogationDB interrogation = crudRepository.getReferenceById(interrogationId);
            CommentDB commentDB = new CommentDB(comment, interrogation);
            commentRepository.save(commentDB);
        }
    }

    @Override
    public void saveData(String interrogationId, ObjectNode data) {
        if (data == null) {
            return;
        }
        int countUpdated = dataRepository.updateData(interrogationId, data);
        if (countUpdated == 0) {
            InterrogationDB interrogation = crudRepository.getReferenceById(interrogationId);
            DataDB dataDB = dataFactory.buildData(data, interrogation);
            dataRepository.save(dataDB);
        }
    }

    @Override
    public void updateCollectedData(String interrogationId, ObjectNode partialCollectedDataNode) {
        dataRepository.updateCollectedData(interrogationId, partialCollectedDataNode);
    }

    @Override
    public Optional<ObjectNode> findComment(String interrogationId) {
        return commentRepository.findComment(interrogationId);
    }

    @Override
    public Optional<ObjectNode> findData(String interrogationId) {
        return dataRepository.findData(interrogationId);
    }

    @Override
    public InterrogationPersonalization getInterrogationPersonalization(String interrogationId) {
        return crudRepository.getPersonalizationById(interrogationId);
    }

    @Override
    public Optional<ArrayNode> findPersonalization(String interrogationId) {
        return personalizationRepository.findPersonalization(interrogationId);
    }

    @Override
    public boolean exists(String interrogationId) {
        return crudRepository.existsById(interrogationId);
    }

    @Override
    public void update(Interrogation interrogation) {
        String interrogationId = interrogation.id();
        save(interrogation.id(), interrogation.surveyUnitId(), interrogation.campaignId(), interrogation.questionnaireId());
        savePersonalization(interrogationId, interrogation.personalization());
        saveComment(interrogationId, interrogation.comment());
        saveData(interrogationId, interrogation.data());
    }

    @Override
    public List<Interrogation> find(List<String> interrogationIds) {
        return crudRepository.findInterrogationsByIdIn(interrogationIds).stream()
                .map(InterrogationProjection::toModel)
                .toList();
    }

    @Override
    public List<Interrogation> findAll() {
        return crudRepository.findAllInterrogations().stream()
                .map(InterrogationProjection::toModel)
                .toList();
    }

    @Override
    public void cleanExtractedData(String campaignId, Long startTimestamp, Long endTimestamp) {
        dataRepository.cleanExtractedData(campaignId, startTimestamp, endTimestamp);
    }

    private void save(String interrogationId, String surveyUnitId, String campaignId, String questionnaireId) {
        Map<String, Object> fieldsToUpdate = new LinkedHashMap<>();
        if (campaignId != null) {
            fieldsToUpdate.put("campaign_id", campaignId);
        }

        if (questionnaireId != null) {
            fieldsToUpdate.put("questionnaire_model_id", questionnaireId);
        }

        if (surveyUnitId != null) {
            fieldsToUpdate.put("survey_unit_id", surveyUnitId);
        }

        if (fieldsToUpdate.isEmpty()) {
            return;
        }

        String fields = fieldsToUpdate.keySet()
                .stream()
                .map(key -> key + " = :" + key)
                .collect(Collectors.joining(", "));
        String sql = String.format("UPDATE interrogation SET %s WHERE id = :interrogationId", fields);

        var query = entityManager.createNativeQuery(sql);
        fieldsToUpdate.forEach(query::setParameter);
        query.setParameter("interrogationId", interrogationId);

        query.executeUpdate();
    }
}
