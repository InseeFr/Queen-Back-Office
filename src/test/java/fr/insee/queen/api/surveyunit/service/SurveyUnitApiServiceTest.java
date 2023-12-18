package fr.insee.queen.api.surveyunit.service;

import fr.insee.queen.api.campaign.service.dummy.CampaignExistenceFakeService;
import fr.insee.queen.api.campaign.service.dummy.QuestionnaireModelExistenceFakeService;
import fr.insee.queen.api.depositproof.service.model.StateDataType;
import fr.insee.queen.api.surveyunit.dao.dummy.SurveyUnitFakeDao;
import fr.insee.queen.api.surveyunit.service.dummy.StateDataFakeService;
import fr.insee.queen.api.surveyunit.service.exception.StateDataDateInvalidDateException;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import fr.insee.queen.api.surveyunit.service.model.SurveyUnit;
import fr.insee.queen.api.web.exception.EntityAlreadyExistException;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SurveyUnitApiServiceTest {

    private SurveyUnitApiService surveyUnitApiService;
    private StateDataFakeService stateDataFakeService;
    private CampaignExistenceFakeService campaignExistenceFakeService;
    private QuestionnaireModelExistenceFakeService questionnaireModelExistenceFakeService;
    private SurveyUnitFakeDao surveyUnitFakeDao;

    @BeforeEach
    void init() {
        CacheManager cacheManager = new NoOpCacheManager();
        surveyUnitFakeDao = new SurveyUnitFakeDao();
        stateDataFakeService = new StateDataFakeService();
        campaignExistenceFakeService = new CampaignExistenceFakeService();
        questionnaireModelExistenceFakeService = new QuestionnaireModelExistenceFakeService();
        surveyUnitApiService = new SurveyUnitApiService(surveyUnitFakeDao, stateDataFakeService,
                campaignExistenceFakeService, questionnaireModelExistenceFakeService, cacheManager);
    }

    @Test
    @DisplayName("On creating survey unit, check campaign existence")
    void testCreate01() throws StateDataDateInvalidDateException {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", stateData);
        surveyUnitFakeDao.setSurveyUnitExist(false);
        surveyUnitApiService.createSurveyUnit(surveyUnit);

        assertThat(campaignExistenceFakeService.isCheckCampaignExist()).isTrue();
    }

    @Test
    @DisplayName("On creating survey unit, check questionnaire existence")
    void testCreate02() throws StateDataDateInvalidDateException {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", stateData);
        surveyUnitFakeDao.setSurveyUnitExist(false);
        surveyUnitApiService.createSurveyUnit(surveyUnit);

        assertThat(questionnaireModelExistenceFakeService.isCheckQuestionnaireExist()).isTrue();
    }

    @Test
    @DisplayName("On creating survey unit, when survey unit exists, throw exception")
    void testCreate03() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", stateData);
        assertThatThrownBy(() -> surveyUnitApiService.createSurveyUnit(surveyUnit))
                .isInstanceOf(EntityAlreadyExistException.class)
                .hasMessage(String.format(SurveyUnitApiService.ALREADY_EXIST_MESSAGE, surveyUnit.id()));
    }

    @Test
    @DisplayName("On creating survey unit, when state data is null, don't save it")
    void testCreate04() throws StateDataDateInvalidDateException {
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", null);
        surveyUnitFakeDao.setSurveyUnitExist(false);
        surveyUnitApiService.createSurveyUnit(surveyUnit);
        assertThat(surveyUnitFakeDao.getSurveyUnitCreated()).isEqualTo(surveyUnit);
        assertThat(stateDataFakeService.getStateDataSaved()).isNull();
    }

    @Test
    @DisplayName("On creating survey unit, when state data is not null, save it")
    void testCreate05() throws StateDataDateInvalidDateException {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", stateData);
        surveyUnitFakeDao.setSurveyUnitExist(false);
        surveyUnitApiService.createSurveyUnit(surveyUnit);
        assertThat(surveyUnitFakeDao.getSurveyUnitCreated()).isEqualTo(surveyUnit);
        assertThat(stateDataFakeService.getStateDataSaved()).isEqualTo(stateData);
    }

    @Test
    @DisplayName("On updating survey unit, when survey unit not exist, throw exception")
    void testUpdate01() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", stateData);
        surveyUnitFakeDao.setSurveyUnitExist(false);
        assertThatThrownBy(() -> surveyUnitApiService.updateSurveyUnit(surveyUnit))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(String.format(SurveyUnitApiService.NOT_FOUND_MESSAGE, surveyUnit.id()));
        assertThat(surveyUnitFakeDao.getSurveyUnitUpdated()).isNull();
        assertThat(stateDataFakeService.getStateDataSaved()).isNull();
    }

    @Test
    @DisplayName("On updating survey unit, when state data is null, don't save it")
    void testUpdate02() {
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", null);
        surveyUnitApiService.updateSurveyUnit(surveyUnit);
        assertThat(surveyUnitFakeDao.getSurveyUnitUpdated()).isEqualTo(surveyUnit);
        assertThat(stateDataFakeService.getStateDataSaved()).isNull();
    }

    @Test
    @DisplayName("On updating survey unit, when state data is not null, save it")
    void testUpdate03() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", stateData);
        surveyUnitApiService.updateSurveyUnit(surveyUnit);
        assertThat(surveyUnitFakeDao.getSurveyUnitUpdated()).isEqualTo(surveyUnit);
        assertThat(stateDataFakeService.getStateDataSaved()).isEqualTo(stateData);
    }

    @Test
    @DisplayName("On updating survey unit, when date is invalid on state data, ignore the error")
    void testUpdate04() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", stateData);
        stateDataFakeService.setDateInvalid(true);
        surveyUnitApiService.updateSurveyUnit(surveyUnit);
        assertThat(surveyUnitFakeDao.getSurveyUnitUpdated()).isEqualTo(surveyUnit);
        assertThat(stateDataFakeService.getStateDataSaved()).isNull();
    }
}
