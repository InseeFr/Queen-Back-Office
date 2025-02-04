package fr.insee.queen.domain.surveyunit.infrastructure.dummy;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.common.paging.PagingResult;
import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import fr.insee.queen.domain.surveyunit.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

public class SurveyUnitFakeDao implements SurveyUnitRepository {

    @Getter
    private SurveyUnit surveyUnitCreated = null;

    @Getter
    private SurveyUnit surveyUnitUpdated = null;

    @Setter
    private SurveyUnitPersonalization surveyUnitPersonalization;

    @Setter
    private boolean surveyUnitExist = true;

    @Override
    public Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId) {
        return Optional.empty();
    }

    @Override
    public List<SurveyUnitSummary> findAllSummaryByCampaignId(String campaignId) {
        return null;
    }

    @Override
    public List<SurveyUnitSummary> findAllSummaryByIdIn(List<String> surveyUnitIds) {
        return null;
    }

    @Override
    public Optional<SurveyUnit> find(String surveyUnitId) {
        return Optional.empty();
    }

    @Override
    public Optional<SurveyUnitDepositProof> findWithCampaignAndStateById(String surveyUnitId) {
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> findAllIds() {
        return Optional.empty();
    }

    @Override
    public PagingResult<SurveyUnitState> findAllByState(String campaignId, StateDataType stateDataType, Integer pageNumber) {
        return null;
    }

    @Override
    public List<SurveyUnitState> findAllWithStateByIdIn(List<String> surveyUnitIds) {
        return null;
    }

    @Override
    public void deleteSurveyUnits(String campaignId) {
        // not used at this moment for unit tests
    }

    @Override
    public void delete(String surveyUnitId) {
        // not used at this moment for unit tests
    }

    @Override
    public void create(SurveyUnit surveyUnit) {
        surveyUnitCreated = surveyUnit;
    }

    @Override
    public void savePersonalization(String surveyUnitId, ArrayNode personalization) {
        // not used at this moment for unit tests
    }

    @Override
    public void saveComment(String surveyUnitId, ObjectNode comment) {
        // not used at this moment for unit tests
    }

    @Override
    public void saveData(String surveyUnitId, ObjectNode data) {
        // not used at this moment for unit tests
    }

    @Override
    public void updateCollectedData(String surveyUnitId, ObjectNode partialCollectedDataNode) {
        // not used at this moment for unit tests
    }

    @Override
    public Optional<ObjectNode> findComment(String surveyUnitId) {
        return Optional.empty();
    }

    @Override
    public Optional<ObjectNode> findData(String surveyUnitId) {
        return Optional.empty();
    }

    @Override
    public SurveyUnitPersonalization getSurveyUnitPersonalization(String surveyUnitId) {
        return surveyUnitPersonalization;
    }

    @Override
    public Optional<ArrayNode> findPersonalization(String surveyUnitId) {
        return Optional.empty();
    }

    @Override
    public boolean exists(String surveyUnitId) {
        return surveyUnitExist;
    }

    @Override
    public void updateInfos(SurveyUnit surveyUnit) {
        surveyUnitUpdated = surveyUnit;
    }

    @Override
    public List<SurveyUnit> find(List<String> surveyUnitIds) {
        return null;
    }

    @Override
    public List<SurveyUnit> findAll() {
        return null;
    }
}
