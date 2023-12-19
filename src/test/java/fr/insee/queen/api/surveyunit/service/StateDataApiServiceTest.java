package fr.insee.queen.api.surveyunit.service;

import fr.insee.queen.api.depositproof.service.model.StateDataType;
import fr.insee.queen.api.surveyunit.dao.dummy.StateDataFakeDao;
import fr.insee.queen.api.surveyunit.service.exception.StateDataInvalidDateException;
import fr.insee.queen.api.surveyunit.service.model.StateData;
import fr.insee.queen.api.web.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StateDataApiServiceTest {

    private StateDataFakeDao stateDataDao;
    private StateDataApiService stateDataService;
    private final String surveyUnitId = "11";

    @BeforeEach
    void init() {
        stateDataDao = new StateDataFakeDao();
        stateDataService = new StateDataApiService(stateDataDao);
    }

    @Test
    @DisplayName("On retrieving state data, get correct state data")
    void testGet01() {
        StateData stateData = stateDataService.getStateData(surveyUnitId);
        assertThat(stateData).isEqualTo(StateDataFakeDao.STATE_DATA);
    }

    @Test
    @DisplayName("On retrieving state data, when state data is empty, throw exception")
    void testGet02() {
        String surveyUnitId = "11";
        stateDataDao.setHasEmptyStateData(true);
        assertThatThrownBy(() -> stateDataService.getStateData(surveyUnitId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(String.format(StateDataApiService.NOT_FOUND_MESSAGE, surveyUnitId));
    }

    @Test
    @DisplayName("On saving new state data, when previous state data doesn't exist, save new state data")
    void testSave01() throws StateDataInvalidDateException {
        stateDataDao.setHasEmptyStateData(true);
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 1000000L, "5");
        stateDataService.saveStateData(surveyUnitId, stateDataUpdate);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is older, save new state data")
    void testSave02() throws StateDataInvalidDateException {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 100000000L, "5");
        assertThat(stateDataUpdate.date()).isGreaterThan(StateDataFakeDao.STATE_DATA.date());
        stateDataService.saveStateData(surveyUnitId, stateDataUpdate);
        assertThat(stateDataUpdate).isEqualTo(stateDataDao.getStateDataSaved());
    }

    @Test
    @DisplayName("On saving new state data, when previous state data is newer, throw exception")
    void testSave03() {
        StateData stateDataUpdate = new StateData(StateDataType.VALIDATED, 800000L, "5");
        assertThat(stateDataUpdate.date()).isLessThanOrEqualTo(StateDataFakeDao.STATE_DATA.date());
        assertThatThrownBy(() -> stateDataService.saveStateData(surveyUnitId, stateDataUpdate))
                .isInstanceOf(StateDataInvalidDateException.class)
                .hasMessage(StateDataApiService.INVALID_DATE_MESSAGE);
        assertThat(stateDataDao.getStateDataSaved()).isNull();
    }
}
