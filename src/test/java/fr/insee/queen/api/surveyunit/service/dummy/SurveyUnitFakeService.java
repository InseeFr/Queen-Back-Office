package fr.insee.queen.api.surveyunit.service.dummy;

import fr.insee.queen.api.depositproof.service.model.SurveyUnitDepositProof;
import fr.insee.queen.api.surveyunit.service.SurveyUnitService;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitState;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnitSummary;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class SurveyUnitFakeService implements SurveyUnitService {

    public static final String SURVEY_UNIT1_ID = "survey-unit1";

    public static final String SURVEY_UNIT2_ID = "survey-unit2";

    @Override
    public boolean existsById(String surveyUnitId) {
        return false;
    }

    @Override
    public void throwExceptionIfSurveyUnitNotExist(String surveyUnitId) {

    }

    @Override
    public SurveyUnit getSurveyUnit(String id) {
        return null;
    }

    @Override
    public List<SurveyUnitSummary> findSummariesByCampaignId(String campaignId) {
        return List.of(
                new SurveyUnitSummary(SURVEY_UNIT1_ID, "questionnaire-id", campaignId),
                new SurveyUnitSummary(SURVEY_UNIT2_ID, "questionnaire-id", campaignId)
        );
    }

    @Override
    public List<String> findAllSurveyUnitIds() {
        return null;
    }

    @Override
    public void updateSurveyUnit(SurveyUnit surveyUnit) {

    }

    @Override
    public void createSurveyUnit(SurveyUnit surveyUnit) {

    }

    @Override
    public List<SurveyUnitSummary> findSummariesByIds(List<String> surveyUnitIds) {
        List<SurveyUnitSummary> surveyUnits = new ArrayList<>();

        surveyUnitIds.forEach(surveyUnitId -> surveyUnits.add(new SurveyUnitSummary(surveyUnitId, "questionnaire-id", "campaign-id")));
        return surveyUnits;
    }

    @Override
    public Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId) {
        SurveyUnitSummary surveyUnit = new SurveyUnitSummary(surveyUnitId, "questionnaire-id", "campaign-id");
        return Optional.of(surveyUnit);
    }

    @Override
    public List<SurveyUnitState> findWithStateByIds(List<String> surveyUnits) {
        return null;
    }

    @Override
    public void delete(String surveyUnitId) {

    }

    @Override
    public SurveyUnitDepositProof getSurveyUnitDepositProof(String surveyUnitId) {
        return null;
    }

    @Override
    public SurveyUnitSummary getSurveyUnitWithCampaignById(String surveyUnitId) {
        return new SurveyUnitSummary("survey-unit1", "questionnaire-id", "campaign-id");
    }

    @Override
    public List<SurveyUnit> findByIds(List<String> surveyUnitIds) {
        return null;
    }

    @Override
    public List<SurveyUnit> findAllSurveyUnits() {
        return List.of(
                new SurveyUnit(SURVEY_UNIT1_ID, "campaign-id", "questionnaire-id", null, null, null, null),
                new SurveyUnit(SURVEY_UNIT2_ID, "campaign-id", "questionnaire-id", null, null, null, null)
        );
    }
}
