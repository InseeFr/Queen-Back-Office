package fr.insee.queen.infrastructure.db.interrogation.repository;

import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.interrogation.gateway.InterrogationRepository;
import fr.insee.queen.domain.interrogation.model.*;
import fr.insee.queen.infrastructure.db.group.entity.GroupDB;
import fr.insee.queen.infrastructure.db.group.entity.QuestionnaireModelDB;
import fr.insee.queen.infrastructure.db.group.repository.jpa.GroupJpaRepository;
import fr.insee.queen.infrastructure.db.group.repository.jpa.QuestionnaireModelJpaRepository;
import fr.insee.queen.infrastructure.db.data.entity.common.DataDB;
import org.springframework.transaction.annotation.Transactional;
import fr.insee.queen.infrastructure.db.interrogation.entity.*;
import fr.insee.queen.infrastructure.db.interrogation.projection.InterrogationProjection;
import fr.insee.queen.infrastructure.db.interrogation.repository.jpa.*;
import fr.insee.queen.infrastructure.db.configuration.DataFactory;
import fr.insee.queen.infrastructure.db.data.repository.jpa.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DAO to handle interrogations in DB
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class InterrogationDao implements InterrogationRepository {

    private final InterrogationJpaRepository crudRepository;
    private final PersonalizationJpaRepository personalizationRepository;
    private final DataRepository dataRepository;
    private final GroupJpaRepository groupRepository;
    private final QuestionnaireModelJpaRepository questionnaireModelRepository;
    private final DataFactory dataFactory;

    @Override
    public Optional<InterrogationSummary> findSummaryById(String interrogationId) {
        return crudRepository.findSummaryById(interrogationId);
    }

    @Override
    public List<InterrogationSummary> findAllSummaryByGroupId(String groupId) {
        return crudRepository.findAllSummaryByGroupId(groupId);
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
    public Optional<InterrogationDepositProof> findWithGroupAndStateById(String interrogationId) {
        return crudRepository.findWithGroupAndStateById(interrogationId);
    }

    @Override
    public Optional<List<String>> findAllIds() {
        return crudRepository.findAllIds();
    }

    @Override
    public List<InterrogationState> findAllByState(String groupId, StateDataType state) {
        if(state == null) {
            return crudRepository.findAllByGroupWithoutState(groupId);
        }
        return crudRepository.findAllByGroupAndState(groupId, state);
    }

    @Override
    public List<InterrogationState> findAllWithStateByIdIn(List<String> interrogationIds) {
        return crudRepository.findAllWithStateByIdIn(interrogationIds);
    }

    @Override
    public void deleteInterrogations(String groupId) {
        crudRepository.deleteInterrogations(groupId);
    }

    @Override
    public void delete(String interrogationId) {
        crudRepository.deleteById(interrogationId);
    }

    @Override
    public void create(Interrogation interrogation) {
        GroupDB group = groupRepository.getReferenceById(interrogation.groupId());
        QuestionnaireModelDB questionnaire = questionnaireModelRepository.getReferenceById(interrogation.questionnaireId());
        InterrogationDB interrogationDB = new InterrogationDB(interrogation.id(), interrogation.surveyUnitId(), group, questionnaire, interrogation.correlationId());
        DataDB dataDB = dataFactory.buildData(interrogation.data(), interrogationDB);
        if (interrogation.personalization() != null) {
            PersonalizationDB personalizationDB = new PersonalizationDB(interrogation.personalization(), interrogationDB);
            interrogationDB.setPersonalization(personalizationDB);
        }
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
    @Transactional
    public void update(Interrogation interrogation) {
        crudRepository.updateFields(interrogation.id(), interrogation.surveyUnitId(), interrogation.groupId(), interrogation.questionnaireId());
        savePersonalization(interrogation.id(), interrogation.personalization());
        saveData(interrogation.id(), interrogation.data());
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
    public void cleanExtractedData(String groupId, Long startTimestamp, Long endTimestamp) {
        dataRepository.cleanExtractedData(groupId, startTimestamp, endTimestamp);
    }

    @Override
    public void cleanExtractedDataByIds(String groupId, List<String> interrogationIds) {
        dataRepository.cleanExtractedDataByIds(groupId, interrogationIds);
    }

    @Override
    public boolean existsByGroupId(String groupId) {
        return crudRepository.existsByGroupId(groupId);
    }

}
