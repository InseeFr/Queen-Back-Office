package fr.insee.queen.application.surveyunit.service.dummy;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.model.CampaignSensitivity;
import fr.insee.queen.domain.campaign.model.CampaignSummary;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.surveyunit.model.*;
import fr.insee.queen.domain.surveyunit.service.SurveyUnitService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

public class SurveyUnitFakeService implements SurveyUnitService {

    private final StateDataFakeService stateDataFakeService;
    public static final String SURVEY_UNIT1_ID = "survey-unit1";
    public static final String SURVEY_UNIT2_ID = "survey-unit2";
    public static final String SURVEY_UNIT3_ID = "survey-unit3";
    public static final String SURVEY_UNIT4_ID = "survey-unit4";
    public static final String SURVEY_UNIT5_ID = "survey-unit5";
    public static final String SURVEY_UNIT6_ID = "survey-unit6";

    @Getter
    private final List<SurveyUnit> surveyUnits;
    @Getter
    private final List<SurveyUnitSummary> surveyUnitSummaries;
    @Setter
    private boolean surveyUnitExist = true;
    @Getter
    private boolean checkSurveyUnitExist = false;
    @Getter
    private boolean checkSurveyUnitNotExist = false;
    @Getter
    private boolean checkSurveyUnitUpdate = false;
    @Getter
    private boolean checkSurveyUnitCreated = false;
    @Getter
    private boolean checkSurveyUnitDeleted = false;
    @Getter
    private SurveyUnit surveyUnitUpdated;

    public SurveyUnitFakeService() {
        stateDataFakeService = new StateDataFakeService();

        CampaignSummary normalCampaign = new CampaignSummary("campaign-id", "campaign-label", CampaignSensitivity.NORMAL);
        CampaignSummary sensitiveCampaign = new CampaignSummary("campaign-id2", "campaign-label2", CampaignSensitivity.SENSITIVE);
        surveyUnitSummaries = List.of(
                new SurveyUnitSummary(SURVEY_UNIT1_ID, "questionnaire-id", normalCampaign),
                new SurveyUnitSummary(SURVEY_UNIT2_ID, "questionnaire-id", normalCampaign),
                new SurveyUnitSummary(SURVEY_UNIT3_ID, "questionnaire-id", sensitiveCampaign),
                new SurveyUnitSummary(SURVEY_UNIT4_ID, "questionnaire-id", sensitiveCampaign),
                new SurveyUnitSummary(SURVEY_UNIT5_ID, "questionnaire-id", sensitiveCampaign),
                new SurveyUnitSummary(SURVEY_UNIT6_ID, "questionnaire-id", sensitiveCampaign)
        );

        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("data", "data-value");

        surveyUnits = List.of(
                new SurveyUnit(SURVEY_UNIT1_ID, normalCampaign.getId(), "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(), data,
                        JsonNodeFactory.instance.objectNode(), stateDataFakeService.getStateData(SURVEY_UNIT1_ID)),
                new SurveyUnit(SURVEY_UNIT2_ID, normalCampaign.getId(), "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(), data,
                        JsonNodeFactory.instance.objectNode(), stateDataFakeService.getStateData(SURVEY_UNIT2_ID)),
                new SurveyUnit(SURVEY_UNIT3_ID, sensitiveCampaign.getId(), "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(), data,
                        JsonNodeFactory.instance.objectNode(), stateDataFakeService.getStateData(SURVEY_UNIT3_ID)),
                new SurveyUnit(SURVEY_UNIT4_ID, sensitiveCampaign.getId(), "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(), data,
                        JsonNodeFactory.instance.objectNode(), stateDataFakeService.getStateData(SURVEY_UNIT4_ID)),
                new SurveyUnit(SURVEY_UNIT5_ID, sensitiveCampaign.getId(), "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(), data,
                        JsonNodeFactory.instance.objectNode(), stateDataFakeService.getStateData(SURVEY_UNIT5_ID)),
                new SurveyUnit(SURVEY_UNIT6_ID, sensitiveCampaign.getId(), "questionnaire-id",
                        JsonNodeFactory.instance.arrayNode(), data,
                        JsonNodeFactory.instance.objectNode(), stateDataFakeService.getStateData(SURVEY_UNIT6_ID))
        );
    }

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
        return surveyUnits
                .stream()
                .filter(surveyUnit -> surveyUnit.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("survey unit not found"));
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
        surveyUnitUpdated = surveyUnit;
        checkSurveyUnitUpdate = true;
    }

    @Override
    public void updateSurveyUnit(String surveyUnitId, ObjectNode data, StateData stateData) {
        checkSurveyUnitUpdate = true;
        surveyUnitUpdated = new SurveyUnit(surveyUnitId, null, null, null, data, null, stateData);
    }

    @Override
    public void createSurveyUnit(SurveyUnit surveyUnit) {
        checkSurveyUnitCreated = true;
    }

    @Override
    public List<SurveyUnitSummary> findSummariesByIds(List<String> surveyUnitIds) {
        return surveyUnitSummaries
                .stream()
                .filter(surveyUnitSummary -> surveyUnitIds.contains(surveyUnitSummary.id()))
                .toList();
    }

    @Override
    public Optional<SurveyUnitSummary> findSummaryById(String surveyUnitId) {
        return surveyUnitSummaries
                .stream()
                .filter(surveyUnitSummary -> surveyUnitSummary.id().equals(surveyUnitId))
                .findFirst();
    }

    @Override
    public List<SurveyUnitState> findWithStateByIds(List<String> surveyUnits) {
        return null;
    }

    @Override
    public void delete(String surveyUnitId) {
        checkSurveyUnitDeleted = true;
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
        return surveyUnitSummaries
                .stream()
                .filter(surveyUnitSummary -> surveyUnitSummary.id().equals(surveyUnitId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("survey unit not found"));
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
