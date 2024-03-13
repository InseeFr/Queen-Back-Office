package fr.insee.queen.domain.surveyunit.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.queen.domain.campaign.service.dummy.CampaignExistenceFakeService;
import fr.insee.queen.domain.common.exception.EntityAlreadyExistException;
import fr.insee.queen.domain.common.exception.EntityNotFoundException;
import fr.insee.queen.domain.surveyunit.infrastructure.dummy.SurveyUnitFakeDao;
import fr.insee.queen.domain.surveyunit.model.StateDataType;
import fr.insee.queen.domain.surveyunit.service.dummy.DataFakeService;
import fr.insee.queen.domain.surveyunit.service.dummy.StateDataFakeService;
import fr.insee.queen.domain.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.domain.surveyunit.model.StateData;
import fr.insee.queen.domain.surveyunit.model.SurveyUnit;
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
    private DataFakeService dataFakeService;
    private CampaignExistenceFakeService campaignExistenceFakeService;
    private SurveyUnitFakeDao surveyUnitFakeDao;

    @BeforeEach
    void init() {
        CacheManager cacheManager = new NoOpCacheManager();
        surveyUnitFakeDao = new SurveyUnitFakeDao();
        stateDataFakeService = new StateDataFakeService();
        campaignExistenceFakeService = new CampaignExistenceFakeService();
        dataFakeService = new DataFakeService();
        surveyUnitApiService = new SurveyUnitApiService(surveyUnitFakeDao, stateDataFakeService, dataFakeService,
                campaignExistenceFakeService, cacheManager);
    }

    @Test
    @DisplayName("On creating survey unit, check questionnaire is linked to campaign")
    void testCreate02() throws StateDataInvalidDateException {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", stateData);
        surveyUnitFakeDao.setSurveyUnitExist(false);
        surveyUnitApiService.createSurveyUnit(surveyUnit);

        assertThat(campaignExistenceFakeService.isCheckCampaignLinkedToQuestionnaire()).isTrue();
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
    void testCreate04() throws StateDataInvalidDateException {
        SurveyUnit surveyUnit = new SurveyUnit("11", "campaign-id", "questionnaire-id", "[]", "{}", "{}", null);
        surveyUnitFakeDao.setSurveyUnitExist(false);
        surveyUnitApiService.createSurveyUnit(surveyUnit);
        assertThat(surveyUnitFakeDao.getSurveyUnitCreated()).isEqualTo(surveyUnit);
        assertThat(stateDataFakeService.getStateDataSaved()).isNull();
    }

    @Test
    @DisplayName("On creating survey unit, when state data is not null, save it")
    void testCreate05() throws StateDataInvalidDateException {
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

    @Test
    @DisplayName("On updating survey unit, when state data is not null, save it")
    void testUpdateDataStateData02() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        String surveyUnitId = "11";
        surveyUnitApiService.updateSurveyUnit(surveyUnitId, data, stateData);
        assertThat(stateDataFakeService.getStateDataSaved()).isEqualTo(stateData);
        assertThat(dataFakeService.getDataSaved()).isEqualTo(data);
    }

    @Test
    @DisplayName("On updating survey unit, when state data update throws an invalid date exception, check data is saved ")
    void testUpdateDataStateData03() {
        StateData stateData = new StateData(StateDataType.VALIDATED, 800000L, "5");
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        String surveyUnitId = "11";
        stateDataFakeService.setDateInvalid(true);
        surveyUnitApiService.updateSurveyUnit(surveyUnitId, data, stateData);
        assertThat(dataFakeService.getDataSaved()).isEqualTo(data);
    }
}
