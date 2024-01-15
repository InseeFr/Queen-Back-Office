package fr.insee.queen.domain.surveyunit.infrastructure.dummy;

import fr.insee.queen.domain.surveyunit.gateway.SurveyUnitRepository;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitDepositProof;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitState;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
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
    public List<SurveyUnitState> findAllWithStateByIdIn(List<String> surveyUnitIds) {
        return null;
    }

    @Override
    public void deleteSurveyUnits(String campaignId) {

    }

    @Override
    public void delete(String surveyUnitId) {

    }

    @Override
    public void create(SurveyUnit surveyUnit) {
        surveyUnitCreated = surveyUnit;
    }

    @Override
    public void savePersonalization(String surveyUnitId, String personalization) {

    }

    @Override
    public void saveComment(String surveyUnitId, String comment) {

    }

    @Override
    public void saveData(String surveyUnitId, String data) {

    }

    @Override
    public Optional<String> findComment(String surveyUnitId) {
        return Optional.empty();
    }

    @Override
    public Optional<String> findData(String surveyUnitId) {
        return Optional.empty();
    }

    @Override
    public Optional<String> findPersonalization(String surveyUnitId) {
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
