package fr.insee.queen.infrastructure.mongo.surveyunit.dao;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitDepositProof;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitState;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.infrastructure.mongo.paradata.repository.ParadataEventMongoRepository;
import fr.insee.queen.infrastructure.mongo.questionnaire.document.QuestionnaireModelDocument;
import fr.insee.queen.infrastructure.mongo.questionnaire.repository.QuestionnaireMongoRepository;
import fr.insee.queen.infrastructure.mongo.surveyunit.document.CommentObject;
import fr.insee.queen.infrastructure.mongo.surveyunit.document.DataObject;
import fr.insee.queen.infrastructure.mongo.surveyunit.document.PersonalizationObject;
import fr.insee.queen.infrastructure.mongo.surveyunit.document.SurveyUnitDocument;
import fr.insee.queen.infrastructure.mongo.surveyunit.repository.SurveyUnitMongoRepository;
import fr.insee.queen.infrastructure.mongo.surveyunittempzone.repository.SurveyUnitTempZoneMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * DAO to handle survey units in DB
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class SurveyUnitMongoDao implements SurveyUnitRepository {
    private final SurveyUnitMongoRepository crudRepository;
    private final QuestionnaireMongoRepository questionnaireRepository;
    private final SurveyUnitTempZoneMongoRepository surveyUnitTempZoneRepository;
    private final ParadataEventMongoRepository paradataEventRepository;

    @Override
    public Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId) {
        return crudRepository.findSummaryById(surveyUnitId)
                .map(SurveyUnitDocument::toSummaryModel);
    }

    @Override
    public List<SurveyUnitSummary> findAllSummaryByCampaignId(String campaignId) {
        return crudRepository.findAllSummaryByCampaignId(campaignId).stream()
                .map(SurveyUnitDocument::toSummaryModel)
                .toList();
    }

    @Override
    public List<SurveyUnitSummary> findAllSummaryByIdIn(List<String> surveyUnitIds) {
        return crudRepository.findAllSummaryByIdIn(surveyUnitIds).stream()
                .map(SurveyUnitDocument::toSummaryModel)
                .toList();
    }

    @Override
    public Optional<SurveyUnit> find(String surveyUnitId) {
        return crudRepository.findOneById(surveyUnitId)
                .map(SurveyUnitDocument::toModel);
    }

    @Override
    public Optional<SurveyUnitDepositProof> findWithCampaignAndStateById(String surveyUnitId) {
        return crudRepository.findWithCampaignAndStateById(surveyUnitId)
                .map(surveyUnitDocument ->
                    questionnaireRepository.findQuestionnaireSummary(surveyUnitDocument.getQuestionnaireId())
                            .map(QuestionnaireModelDocument::getCampaign)
                            .map(campaignObject -> SurveyUnitDocument.toDepositProofModel(surveyUnitDocument, campaignObject))
                )
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Override
    public Optional<List<String>> findAllIds() {
        List<String> ids = crudRepository.findAllIds().stream()
                .flatMap(Collection::stream)
                .map(SurveyUnitDocument::getId)
                .toList();
        return Optional.of(ids);
    }

    @Override
    public List<SurveyUnitState> findAllWithStateByIdIn(List<String> surveyUnitIds) {
        return crudRepository.findWithState(surveyUnitIds).stream()
                .map(SurveyUnitDocument::toStateModel)
                .toList();
    }

    @Override
    public void deleteSurveyUnits(String campaignId) {
        // TODO: resolve this later
        // surveyUnitTempZoneRepository.deleteSurveyUnits(campaignId);
        // paradataEventRepository.deleteBySurveyUnitCampaignId(campaignId);
        crudRepository.deleteByCampaignId(campaignId);
    }

    @Override
    public void delete(String surveyUnitId) {
        surveyUnitTempZoneRepository.deleteBySurveyUnitId(surveyUnitId);
        paradataEventRepository.deleteBySurveyUnitId(surveyUnitId);
        crudRepository.deleteById(surveyUnitId);
    }

    @Override
    public void create(SurveyUnit surveyUnit) {
        SurveyUnitDocument surveyUnitDocument = SurveyUnitDocument.fromModel(surveyUnit);
        crudRepository.save(surveyUnitDocument);
    }

    @Override
    public Optional<ObjectNode> findComment(String surveyUnitId) {
        return crudRepository.findComment(surveyUnitId)
                .map(SurveyUnitDocument::getComment)
                .map(CommentObject::toModel);
    }

    @Override
    public Optional<ObjectNode> findData(String surveyUnitId) {
        return crudRepository.findData(surveyUnitId)
                .map(SurveyUnitDocument::getData)
                .map(DataObject::toModel);
    }

    @Override
    public Optional<ArrayNode> findPersonalization(String surveyUnitId) {
        return crudRepository.findPersonalization(surveyUnitId)
                .map(SurveyUnitDocument::getPersonalization)
                .map(PersonalizationObject::toModel);
    }

    @Override
    public void savePersonalization(String surveyUnitId, ArrayNode personalization) {
        if (personalization == null) {
            return;
        }
        crudRepository.savePersonalization(surveyUnitId, PersonalizationObject.fromModel(personalization));
    }

    @Override
    public void saveComment(String surveyUnitId, ObjectNode comment) {
        if (comment == null) {
            return;
        }

        crudRepository.saveComment(surveyUnitId, CommentObject.fromModel(comment));
    }

    @Override
    public void saveData(String surveyUnitId, ObjectNode data) {
        if (data == null) {
            return;
        }

        crudRepository.saveData(surveyUnitId, DataObject.fromModel(data));
    }

    @Override
    public void updateCollectedData(String surveyUnitId, ObjectNode partialCollectedDataNode) {
        crudRepository.updateCollectedData(surveyUnitId, partialCollectedDataNode);
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
                .map(SurveyUnitDocument::toModel)
                .toList();
    }

    @Override
    public List<SurveyUnit> findAll() {
        return crudRepository.findAllSurveyUnits().stream()
                .map(SurveyUnitDocument::toModel)
                .toList();
    }


}
