package fr.insee.queen.domain.surveyunit.service.dummy;

import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitDepositProof;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitState;
import fr.insee.queen.domain.surveyunit.model.SurveyUnitSummary;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SurveyUnitFakeService implements SurveyUnitService {

    public static final String SURVEY_UNIT1_ID = "survey-unit1";
    public static final String SURVEY_UNIT2_ID = "survey-unit2";

    @Setter
    private boolean surveyUnitExist = true;
    @Getter
    private boolean checkSurveyUnitExist = false;

    @Getter
    private boolean checkSurveyUnitNotExist = false;

    @Getter
    private final List<SurveyUnitSummary> surveyUnitSummaries = List.of(
            new SurveyUnitSummary(SURVEY_UNIT1_ID, "questionnaire-id", "campaign-id"),
            new SurveyUnitSummary("survey-unit2", "questionnaire-id", "campaign-id"),
            new SurveyUnitSummary("survey-unit3", "questionnaire-id", "campaign-id")
    );

    @Override
    public void throwExceptionIfSurveyUnitNotExist(String surveyUnitId) {
        checkSurveyUnitExist = true;
    }

    @Override
    public void throwExceptionIfSurveyUnitExist(String surveyUnitId) {
        checkSurveyUnitNotExist = true;
    }

    @Override
    public boolean existsById(String surveyUnitId) {
        return surveyUnitExist;
    }

    @Override
    public SurveyUnit getSurveyUnit(String id) {
        return null;
    }

    @Override
    public List<SurveyUnitSummary> findSummariesByCampaignId(String campaignId) {
        return surveyUnitSummaries;
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
        return new SurveyUnitSummary(SURVEY_UNIT1_ID, "questionnaire-id", "campaign-id");
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
