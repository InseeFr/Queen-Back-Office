package fr.insee.queen.domain.surveyunit.service.dummy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.surveyunit.model.*;
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
            new SurveyUnitSummary(SURVEY_UNIT1_ID, "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL)),
            new SurveyUnitSummary("survey-unit2", "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL)),
            new SurveyUnitSummary("survey-unit3", "questionnaire-id", new CampaignSummary("campaign-id2", "campaign-label", CampaignSensitivity.SENSITIVE))
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
        // not used for test at this moment
    }

    @Override
    public void updateSurveyUnit(String surveyUnitId, ObjectNode data, StateData stateData) {
        // not used for test at this moment
    }

    @Override
    public void createSurveyUnit(SurveyUnit surveyUnit) {
        // not used for test at this moment
    }

    @Override
    public List<SurveyUnitSummary> findSummariesByIds(List<String> surveyUnitIds) {
        List<SurveyUnitSummary> surveyUnits = new ArrayList<>();

        surveyUnitIds.forEach(surveyUnitId -> surveyUnits.add(new SurveyUnitSummary(surveyUnitId, "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL))));
        return surveyUnits;
    }

    @Override
    public Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId) {
        SurveyUnitSummary surveyUnit = new SurveyUnitSummary(surveyUnitId, "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL));
        return Optional.of(surveyUnit);
    }

    @Override
    public List<SurveyUnitState> findWithStateByIds(List<String> surveyUnits) {
        return null;
    }

    @Override
    public void delete(String surveyUnitId) {
        // not used for test at this moment
    }

    @Override
    public SurveyUnitDepositProof getSurveyUnitDepositProof(String surveyUnitId) {
        return null;
    }

    @Override
    public SurveyUnitMetadata getSurveyUnitMetadata(String surveyUnitId) {
        return null;
    }

    @Override
    public SurveyUnitSummary getSummaryById(String surveyUnitId) {
        return new SurveyUnitSummary(SURVEY_UNIT1_ID, "questionnaire-id", new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL));
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

    @Override
    public List<SurveyUnitState> getSurveyUnits(String campaignId, StateDataType stateDataType) {
        return null;
    }
}
